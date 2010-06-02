/*
 * @(#)QLEditorSupport.java   10/06/02
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

package org.javaplus.netbeans.persistence.ql.editor;

import org.javaplus.netbeans.persistence.connection.Session;
import org.javaplus.netbeans.persistence.ql.query.QueryResult;
import org.javaplus.netbeans.persistence.ql.view.DataView;

import org.openide.awt.TabbedPaneFactory;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.OpenCookie;
import org.openide.text.CloneableEditor;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.CloneableOpenSupport;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author Roger Suen
 */
public class QLEditorSupport extends CloneableEditorSupport
        implements OpenCookie, EditorCookie {

    /**
     *
     */
    private final Session session;

    /**
     *
     */
    private QLEditorContainer editorContainer;

    private QLEditorSupport(QLEditorSupport.Env env, Session session) {
        super(env);
        env.setQLEditorSupport(this);
        this.session = session;
    }

    public static QLEditorSupport create(Session session) {
        return new QLEditorSupport(new QLEditorSupport.Env(), session);
    }

    public Session getSession() {
        return session;
    }

    @Override
    protected CloneableEditor createCloneableEditor() {
        return new QLEditor(this);
    }

    @Override
    protected Component wrapEditorComponent(Component editorComponent) {
        editorContainer = new QLEditorContainer(editorComponent);
        return editorContainer;
    }

    private static final class QLEditorContainer extends JPanel {
        private final JSplitPane splitPane;
        private final Component editorComponent;
        private final Container resultContainer;

        private QLEditorContainer(Component editorComponent) {
            super(new BorderLayout());
            setName("QLEditorContainer");
            this.editorComponent = editorComponent;

            //
            // Component hierarchy:
            //
            // - JPanel("QLEditorContainer")
            //      - JSplitPane("QLEditorSplitPane")
            //          - editotComponent
            //          - JTabbedPane("QLEditorResultTabbedPane")
            //
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                       editorComponent, null);
            splitPane.setName("QLEditorSplitPane");
            splitPane.setBorder(null);
            add(splitPane);

            // create result tabbed pane, but not show it now
            resultContainer = TabbedPaneFactory.createCloseButtonTabbedPane();
            resultContainer.setName("QLEditorResultTabbedPane");
        }

        private void showQueryResult(QueryResult queryResult) {
            populateResultComponent(queryResult);
            showResultComponent();
        }

        private void populateResultComponent(QueryResult queryResult) {
            resultContainer.add(new DataView(queryResult));
        }

        private void showResultComponent() {
            splitPane.setBottomComponent(resultContainer);
            splitPane.setDividerLocation(250);
            splitPane.setDividerSize(7);
            invalidate();
            validate();
            repaint();
        }
    }


    public void showQueryResult(QueryResult queryResult) {
        editorContainer.showQueryResult(queryResult);
    }

    @Override
    protected String documentID() {
        return session.getName();
    }

    @Override
    protected boolean canClose() {
        return true;
    }

    @Override
    public void saveDocument() throws IOException {
        return;
    }

    protected String messageSave() {
        return null;
    }

    protected String messageName() {
        return documentID();
    }

    protected String messageToolTip() {
        return null;
    }

    protected String messageOpening() {
        return null;
    }

    protected String messageOpened() {
        return null;
    }

    private static final class Env implements CloneableEditorSupport.Env {
        private static final String MIME_TYPE = "text/x-ql";
        private final ByteArrayOutputStream out =
            new ByteArrayOutputStream(256);
        private QLEditorSupport editorSupport;
        private Date lastModified = new Date();
        private boolean isModified = true;
        private PropertyChangeSupport propertyChangeSupport;
        private VetoableChangeSupport vetoableChangeSupport;
        private final Object changeSupportLock = new Object();

        private Env() {}

        private void setQLEditorSupport(QLEditorSupport editorSupport) {
            this.editorSupport = editorSupport;
        }

        public InputStream inputStream() throws IOException {
            return new ByteArrayInputStream(out.toByteArray());
        }

        public OutputStream outputStream() throws IOException {
            return out;
        }

        public Date getTime() {
            return lastModified;
        }

        public String getMimeType() {
            return MIME_TYPE;
        }

        public String getText() {
            return out.toString();
        }

        private PropertyChangeSupport getPropertyChangeSupport() {
            synchronized (changeSupportLock) {    // lazy initialization
                if (propertyChangeSupport == null) {
                    propertyChangeSupport = new PropertyChangeSupport(this);
                }
            }

            return propertyChangeSupport;
        }

        private VetoableChangeSupport getVetoableChangeSupport() {
            synchronized (changeSupportLock) {    // lazy initialization
                if (vetoableChangeSupport == null) {
                    vetoableChangeSupport = new VetoableChangeSupport(this);
                }
            }

            return vetoableChangeSupport;
        }

        public void addPropertyChangeListener(PropertyChangeListener l) {}

        public void removePropertyChangeListener(PropertyChangeListener l) {}

        public void addVetoableChangeListener(VetoableChangeListener l) {}

        public void removeVetoableChangeListener(VetoableChangeListener l) {}

        public boolean isValid() {
            return true;
        }

        public boolean isModified() {
            return isModified;
        }

        public void markModified() throws IOException {
            lastModified = new Date();
            isModified = true;
        }

        public void unmarkModified() {
            isModified = false;
        }

        public CloneableOpenSupport findCloneableOpenSupport() {
            return editorSupport;
        }
    }
}
