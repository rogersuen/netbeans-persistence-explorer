/*
 * @(#)ProviderDialog.java   10/04/21
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

import java.io.File;
import java.net.MalformedURLException;
import java.util.Enumeration;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListDataEvent;
import org.javaplus.netbeans.api.persistence.PersistenceProviderException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;

import javax.swing.JPanel;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import org.javaplus.netbeans.api.persistence.PersistenceProvider;
import org.javaplus.netbeans.api.persistence.PersistenceProviderManager;
import org.javaplus.netbeans.api.persistence.UrlSpec;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author Roger Suen
 */
public class ProviderDialog extends JPanel {

    private static final String PROVIDER_CONFIGURATION_FILE = "META-INF/services/javax.persistence.spi.PersistenceProvider";
    private static final String MESSAGE_TITLE = "ProviderDialog.TITLE";
    private static final String MESSAGE_NO_FILE = "ProviderDialog.NO_FILE";
    private static final String MESSAGE_NO_CLASS = "ProviderDialog.NO_CLASS";
    private static final String MESSAGE_NO_NAME = "ProviderDialog.NO_NAME";
    private static final String MESSAGE_FILE_CHOOSER_TITLE = "ProviderDialog.FileChooser.TITLE";
    private static final String MESSAGE_FILE_CHOOSER_FILTER = "ProviderDialog.FileChooser.FILTER";
    private static final String MESSAGE_FILE_CHOOSER_DUPLICATED = "ProviderDialog.FileChooser.DUPLICATED";
    private static final String MESSAGE_FILE_CHOOSER_MALFORMED_URL = "ProviderDialog.FileChooser.MALFORMED_URL";
    /**
     * Default model used to construct <tt>urlsList</tt> with
     * Custom Creation Code.
     */
    private DefaultListModel urlsListModel;
    /**
     * The dialog descriptor
     */
    private DialogDescriptor dialogDescriptor;
    private static final Logger logger = Logger.getLogger(ProviderDialog.class.getName());

    /**
     * Creates new form ProviderDialog
     */
    public ProviderDialog() {
        initComponents();

        urlsList.getModel().addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                resolveImplentationClass();
                updateState();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                resolveImplentationClass();
                updateState();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                resolveImplentationClass();
                updateState();
            }
        });

        nameTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
    }

    public static void showDialog() {
        // init the dialog panel
        ProviderDialog panel = new ProviderDialog();
        DialogDescriptor dd = new DialogDescriptor(
                panel,
                NbBundle.getMessage(
                ProviderDialog.class,
                MESSAGE_TITLE));
        dd.createNotificationLineSupport();
        panel.dialogDescriptor = dd;

        // show the dialog
        panel.updateState();
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setVisible(true);

        if (DialogDescriptor.OK_OPTION == dd.getValue()) {
            String name = panel.nameTextField.getText();
            String description = panel.descriptionTextArea.getText();
            int size = panel.urlsListModel.size();
            ArrayList<UrlSpec> urlSpecs = new ArrayList<UrlSpec>(size);
            for (int i = 0; i < size; i++) {
                urlSpecs.add((UrlSpec) panel.urlsListModel.get(i));
            }

            PersistenceProvider provider = new PersistenceProvider(
                    name, name, description, urlSpecs);
            try {
                PersistenceProviderManager.getDefault().addProvider(provider);
            } catch (PersistenceProviderException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void updateState() {
        removeButton.setEnabled(urlsList.getSelectedIndices().length > 0);

        boolean valid = true;
        if (urlsListModel.getSize() == 0) {
            dialogDescriptor.getNotificationLineSupport().setErrorMessage(
                    NbBundle.getMessage(ProviderDialog.class,
                    MESSAGE_NO_FILE));
            valid = false;
        } else if (classesTextArea.getText().isEmpty()) {
            dialogDescriptor.getNotificationLineSupport().setErrorMessage(
                    NbBundle.getMessage(ProviderDialog.class,
                    MESSAGE_NO_CLASS));
            valid = false;
        } else if (nameTextField.getText().isEmpty()) {
            dialogDescriptor.getNotificationLineSupport().setErrorMessage(
                    NbBundle.getMessage(ProviderDialog.class,
                    MESSAGE_NO_NAME));
            valid = false;
        } else {
            dialogDescriptor.getNotificationLineSupport().clearMessages();
        }
        dialogDescriptor.setValid(valid);
    }

    private void resolveImplentationClass() {
        classesTextArea.setText("");

        // prepare the URLClassLoader used to load provider class
        URLClassLoader cl = null;
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

        cl = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        // load provider configuration files, building provider name list
        // NOTE:
        // If an IOException is raised at any time during building,
        // a empty name list will return
        ArrayList<String> names = new ArrayList<String>();
        try {
            Enumeration<URL> e = cl.findResources(PROVIDER_CONFIGURATION_FILE);
            while (e.hasMoreElements()) {
                URL u = e.nextElement();
                parse(u, names);
            }

            // populate the component
            StringBuilder sb = new StringBuilder();
            for (String name : names) {
                sb.append(name);
                sb.append('\n');
            }
            classesTextArea.setText(sb.toString());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error loading configuration file", ex);
        }
    }

    @SuppressWarnings("empty-statement")
    private void parse(URL u, List<String> names) throws IOException {
        InputStream in = null;
        BufferedReader r = null;
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            while (parseLine(u, r, names));
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error reading configuration file", ex);
            throw ex;
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Error closing configuration file", ex);
                throw ex;
            }
        }
    }

    // Parse a single line from the given configuration file, adding the name
    // on the line to the names list.
    private boolean parseLine(URL u, BufferedReader r, List<String> names) throws IOException {
        String ln = r.readLine();
        if (ln == null) {
            return false;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                logger.log(Level.WARNING,
                        "Illegal configuration-file syntax at line [{0}] in [{1}]",
                        new Object[]{ln, u});
                return false;
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                logger.log(Level.WARNING,
                        "Illegal provider-class name found at line [{0}] in [{1}]",
                        new Object[]{ln, u});
                return false;
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    logger.log(Level.WARNING,
                            "Illegal provider-class name found at line [{0}] in [{1}]",
                            new Object[]{ln, u});
                    return false;
                }

                // done
                if (!names.contains(ln)) {
                    names.add(ln);
                }
            }
        }
        return true;
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

        urlsLabel = new javax.swing.JLabel();
        urlsScrollPane = new javax.swing.JScrollPane();
        urlsListModel = new DefaultListModel();
        urlsList = new javax.swing.JList(urlsListModel);
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        nameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        descriptionLabel = new javax.swing.JLabel();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        classesLabel = new javax.swing.JLabel();
        classesTextArea = new javax.swing.JTextArea();

        setPreferredSize(new java.awt.Dimension(550, 350));
        setLayout(new java.awt.GridBagLayout());

        urlsLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        urlsLabel.setText(org.openide.util.NbBundle.getMessage(ProviderDialog.class, "ProviderDialog.urlsLabel.text")); // NOI18N
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

        addButton.setText(org.openide.util.NbBundle.getMessage(ProviderDialog.class, "ProviderDialog.addButton.text")); // NOI18N
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

        removeButton.setText(org.openide.util.NbBundle.getMessage(ProviderDialog.class, "ProviderDialog.removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 12);
        add(removeButton, gridBagConstraints);

        nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        nameLabel.setText(org.openide.util.NbBundle.getMessage(ProviderDialog.class, "ProviderDialog.nameLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(nameLabel, gridBagConstraints);

        nameTextField.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(nameTextField, gridBagConstraints);

        descriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        descriptionLabel.setText(org.openide.util.NbBundle.getMessage(ProviderDialog.class, "ProviderDialog.descriptionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(descriptionLabel, gridBagConstraints);

        descriptionTextArea.setColumns(20);
        descriptionScrollPane.setViewportView(descriptionTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(descriptionScrollPane, gridBagConstraints);

        classesLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        classesLabel.setText(org.openide.util.NbBundle.getMessage(ProviderDialog.class, "ProviderDialog.classesLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 12, 4, 4);
        add(classesLabel, gridBagConstraints);

        classesTextArea.setBackground(javax.swing.UIManager.getDefaults().getColor("TextField.inactiveBackground"));
        classesTextArea.setColumns(20);
        classesTextArea.setEditable(false);
        classesTextArea.setRows(3);
        classesTextArea.setAutoscrolls(false);
        classesTextArea.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(classesTextArea, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        FileChooserBuilder fileChooserBuilder = new FileChooserBuilder(ProviderDialog.class);
        fileChooserBuilder.setTitle(NbBundle.getMessage(ProviderDialog.class, MESSAGE_FILE_CHOOSER_TITLE));
        fileChooserBuilder.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) { // folder, ZIP or JAR
                return (f.isDirectory() || f.getName().endsWith(".jar") || f.getName().endsWith(".zip"));
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(ProviderDialog.class, MESSAGE_FILE_CHOOSER_FILTER);
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
                            NbBundle.getMessage(ProviderDialog.class,
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
                            NbBundle.getMessage(ProviderDialog.class,
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel classesLabel;
    private javax.swing.JTextArea classesTextArea;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton removeButton;
    private javax.swing.JLabel urlsLabel;
    private javax.swing.JList urlsList;
    private javax.swing.JScrollPane urlsScrollPane;
    // End of variables declaration//GEN-END:variables
}
