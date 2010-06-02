/*
 * @(#)PersistenceUnitDialog.java   10/04/21
 * 
 * Copyright (c) 2010 Roger Suen(SUNRUJUN)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.javaplus.netbeans.persistence.explorer.dialog;

import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.event.ListDataEvent;
import org.javaplus.netbeans.api.persistence.PersistenceUnitException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dialog;
import java.awt.event.ItemListener;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

import javax.swing.JPanel;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.api.persistence.PersistenceUnitManager;
import org.javaplus.netbeans.api.persistence.UrlSpec;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.xml.XMLUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Roger Suen
 */
public class PersistenceUnitDialog extends JPanel {

    private static final String PERSISTENCE_XML = "META-INF/persistence.xml";
    private static final String MESSAGE_TITLE = "PersistenceUnitDialog.TITLE";
    private static final String MESSAGE_NO_FILE = "PersistenceUnitDialog.NO_FILE";
    private static final String MESSAGE_NO_CONFIGURATION = "PersistenceUnitDialog.NO_CONFIGURATION";
    private static final String MESSAGE_NO_UNIT = "PersistenceUnitDialog.NO_UNIT";
    private static final String MESSAGE_FILE_CHOOSER_TITLE = "PersistenceUnitDialog.FileChooser.TITLE";
    private static final String MESSAGE_FILE_CHOOSER_FILTER = "PersistenceUnitDialog.FileChooser.FILTER";
    private static final String MESSAGE_FILE_CHOOSER_DUPLICATED = "PersistenceUnitDialog.FileChooser.DUPLICATED";
    private static final String MESSAGE_FILE_CHOOSER_MALFORMED_URL = "PersistenceUnitDialog.FileChooser.MALFORMED_URL";
    private static final String MESSAGE_URLCLASSLOADER_IO_EXCEPTION = " PersistenceUnitDialog.MESSAGE_URLCLASSLOADER_IO_EXCEPTION";
    /**
     * Default model used to construct <tt>urlsList</tt> with
     * Custom Creation Code.
     */
    private DefaultListModel urlsListModel;
    /**
     * Default model used to construct <tt>configurationUrlComboBox</tt>
     * with Custom Creation Code.
     */
    private DefaultComboBoxModel confUrlComboBoxModel;
    /**
     * Default model used to construct <tt>nameComboBox</tt>
     * with Custom Creation Code.
     */
    private DefaultComboBoxModel nameComboBoxModel;
    /**
     * The dialog descriptor
     */
    private DialogDescriptor dialogDescriptor;
    private static final Logger logger = Logger.getLogger(PersistenceUnitDialog.class.getName());

    /**
     * Creates new form PersistenceUnitDialog
     */
    public PersistenceUnitDialog() {
        initComponents();

        urlsList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                resolveConfigurationFiles();
                updateState();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                resolveConfigurationFiles();
                updateState();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                resolveConfigurationFiles();
                updateState();
            }
        });

        confUrlComboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                resolveUnitNames();
            }
        });

    }

    public static void showDialog() {
        // init the dialog panel
        PersistenceUnitDialog panel = new PersistenceUnitDialog();
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(
                PersistenceUnitDialog.class,
                MESSAGE_TITLE));
        dd.createNotificationLineSupport();
        panel.dialogDescriptor = dd;

        // show the dialog
        panel.updateState();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setVisible(true);

        if (DialogDescriptor.OK_OPTION == dd.getValue()) {
            String name = (String) panel.nameComboBoxModel.getSelectedItem();
            String displayName = panel.displayNameTextField.getText();
            String description = panel.descriptionTextArea.getText();
            int size = panel.urlsListModel.size();
            ArrayList<UrlSpec> urlSpecs = new ArrayList<UrlSpec>(size);
            for (int i = 0; i < size; i++) {
                urlSpecs.add((UrlSpec) panel.urlsListModel.get(i));
            }

            PersistenceUnit pu = new PersistenceUnit(name, displayName, description, urlSpecs);
            try {
                PersistenceUnitManager.getDefault().addUnit(pu);
            } catch (PersistenceUnitException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void updateState() {
        removeButton.setEnabled(urlsList.getSelectedIndices().length > 0);

        boolean valid = true;
        if (urlsListModel.getSize() == 0) {
            dialogDescriptor.getNotificationLineSupport().setErrorMessage(
                    NbBundle.getMessage(PersistenceUnitDialog.class,
                    MESSAGE_NO_FILE));
            valid = false;
        } else if (confUrlComboBoxModel.getSelectedItem() == null) {
            dialogDescriptor.getNotificationLineSupport().setErrorMessage(
                    NbBundle.getMessage(PersistenceUnitDialog.class,
                    MESSAGE_NO_CONFIGURATION));
            valid = false;
        } else if (nameComboBoxModel.getSelectedItem() == null) {
            dialogDescriptor.getNotificationLineSupport().setErrorMessage(
                    NbBundle.getMessage(PersistenceUnitDialog.class,
                    MESSAGE_NO_UNIT));
            valid = false;
        } else {
            dialogDescriptor.getNotificationLineSupport().clearMessages();
        }
        dialogDescriptor.setValid(valid);
    }

    private void resolveConfigurationFiles() {
        confUrlComboBoxModel.removeAllElements();

        // prepare the URLClassLoader used to load persistence.xml
        URLClassLoader loader = null;
        int size = urlsListModel.getSize();
        ArrayList<URL> urls = new ArrayList<URL>(size);
        for (int i = 0; i < size; i++) {
            UrlSpec urlSpec = (UrlSpec) urlsListModel.get(i);
            URL url = urlSpec.getUrl();
            if (url != null) {
                // NOTE:
                // URLClassLoader will throw NullPointerException
                // on null URL entry, so skip any malformed URL
                urls.add(url);
            }
        }

        if (urls.isEmpty()) {
            return;
        }

        loader = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        // load all persistence.xml files
        Enumeration<URL> urlEnum = null;
        try {
            urlEnum = loader.getResources(PERSISTENCE_XML);
        } catch (IOException ex) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                        "Failed to load the persistence.xml from URLs: "
                        + Arrays.toString(loader.getURLs()),
                        ex);
            }
            NotifyDescriptor nd = new NotifyDescriptor.Message(
                    NbBundle.getMessage(PersistenceUnitDialog.class,
                    MESSAGE_URLCLASSLOADER_IO_EXCEPTION,
                    loader.getURLs()));
            DialogDisplayer.getDefault().notify(nd);
            return;
        }

        // Ok, add persistence.xml entries
        while (urlEnum.hasMoreElements()) {
            confUrlComboBoxModel.addElement(urlEnum.nextElement());
        }
    }

    private void resolveUnitNames() {
        nameComboBoxModel.removeAllElements();
        URL url = (URL) confUrlComboBoxModel.getSelectedItem();
        if (url == null) {
            return;
        }
        List<String> names = resolveUnitNames(url);
        for (String name : names) {
            nameComboBoxModel.addElement(name);
        }
    }

    private List<String> resolveUnitNames(URL url) {
        Handler handler = new Handler();
        try {
            XMLReader reader = XMLUtil.createXMLReader();
            reader.setContentHandler(handler);
            InputSource is = new InputSource(url.openStream());
            reader.parse(is);
        } catch (SAXException saxe) {
        } catch (IOException ioe) {
        }
        return handler.names;
    }

    /**
     * Default handler for reading persistence unit names from persistence.xml
     */
    private static final class Handler extends DefaultHandler {

        private List<String> names = new LinkedList<String>();

        @Override
        public void startElement(String uri, String localName,
                String qName, Attributes attributes)
                throws SAXException {
            if ("persistence-unit".equals(qName)) {
                String name = attributes.getValue("name");
                if (name != null) {
                    names.add(name);
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        nameLabel = new javax.swing.JLabel();
        nameComboBoxModel = new DefaultComboBoxModel();
        nameComboBox = new javax.swing.JComboBox(nameComboBoxModel);
        displayNameLabel = new javax.swing.JLabel();
        displayNameTextField = new javax.swing.JTextField();
        descriptionLabel = new javax.swing.JLabel();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        urlsLabel = new javax.swing.JLabel();
        urlsScrollPane = new javax.swing.JScrollPane();
        urlsListModel = new DefaultListModel();
        urlsList = new javax.swing.JList(urlsListModel);
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        confUrlLabel = new javax.swing.JLabel();
        confUrlComboBoxModel = new DefaultComboBoxModel();
        confUrlComboBox = new javax.swing.JComboBox(confUrlComboBoxModel);

        setPreferredSize(new java.awt.Dimension(550, 350));
        setLayout(new java.awt.GridBagLayout());

        nameLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.nameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(nameLabel, gridBagConstraints);

        nameComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                nameComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(nameComboBox, gridBagConstraints);

        displayNameLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.displayNameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(displayNameLabel, gridBagConstraints);

        displayNameTextField.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.displayNameTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(displayNameTextField, gridBagConstraints);

        descriptionLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.descriptionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(descriptionLabel, gridBagConstraints);

        descriptionTextArea.setColumns(20);
        descriptionScrollPane.setViewportView(descriptionTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(descriptionScrollPane, gridBagConstraints);

        urlsLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.urlsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 4, 4);
        add(urlsLabel, gridBagConstraints);

        urlsList.setVisibleRowCount(5);
        urlsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                urlsListValueChanged(evt);
            }
        });
        urlsScrollPane.setViewportView(urlsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 4, 4);
        add(urlsScrollPane, gridBagConstraints);

        addButton.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 4, 12);
        add(addButton, gridBagConstraints);

        removeButton.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 12);
        add(removeButton, gridBagConstraints);

        confUrlLabel.setText(org.openide.util.NbBundle.getMessage(PersistenceUnitDialog.class, "PersistenceUnitDialog.confUrlLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(confUrlLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(confUrlComboBox, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(PersistenceUnitDialog.class);
        fileChooserBuilder.setTitle(NbBundle.getMessage(PersistenceUnitDialog.class, MESSAGE_FILE_CHOOSER_TITLE));
        fileChooserBuilder.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) { // folder, ZIP or JAR
                return (f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip"));
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(PersistenceUnitDialog.class, MESSAGE_FILE_CHOOSER_FILTER);
            }
        });

        File[] files = fileChooserBuilder.showMultiOpenDialog();
        if (files != null) {
            for (File file : files) {
                UrlSpec urlSpec = null;
                // notify and skip malformed URL
                try {
                    urlSpec = new UrlSpec(file.toURI().toURL());
                } catch (MalformedURLException ex) {
                    if (logger.isLoggable(Level.WARNING)) {
                        logger.log(Level.WARNING,
                                "Failed to convert the file entry " + file + " to a URL. "
                                + "It was not added to the URL list of persistence unit files",
                                ex);
                    }
                    NotifyDescriptor nd = new NotifyDescriptor.Message(
                            NbBundle.getMessage(PersistenceUnitDialog.class,
                            MESSAGE_FILE_CHOOSER_MALFORMED_URL,
                            file));
                    DialogDisplayer.getDefault().notify(nd);
                    continue;
                }

                // notify and then skip any duplicated file
                if (urlsListModel.contains(urlSpec)) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.INFO,
                                "The file entry {0} has already been added, ignore it",
                                file);
                    }
                    NotifyDescriptor nd = new NotifyDescriptor.Message(
                            NbBundle.getMessage(PersistenceUnitDialog.class,
                            MESSAGE_FILE_CHOOSER_DUPLICATED,
                            file));
                    DialogDisplayer.getDefault().notify(nd);
                    continue;
                }
                // Ok, add it
                urlsListModel.addElement(urlSpec);
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void urlsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_urlsListValueChanged
        updateState();
    }//GEN-LAST:event_urlsListValueChanged

    private void nameComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_nameComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String name = evt.getItem().toString();
            displayNameTextField.setText(name);
        }
    }//GEN-LAST:event_nameComboBoxItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox confUrlComboBox;
    private javax.swing.JLabel confUrlLabel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel displayNameLabel;
    private javax.swing.JTextField displayNameTextField;
    private javax.swing.JComboBox nameComboBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JLabel urlsLabel;
    private javax.swing.JList urlsList;
    private javax.swing.JScrollPane urlsScrollPane;
    // End of variables declaration//GEN-END:variables
}
