/*
 * @(#)QLEditor.java   10/05/18
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

import org.javaplus.netbeans.persistence.connection.ConnectionException;
import org.javaplus.netbeans.persistence.connection.Session;

import org.openide.text.CloneableEditor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Roger Suen
 */
public class QLEditor extends CloneableEditor {
    private final InstanceContent instanceContent = new InstanceContent();
    private final Lookup lookup = new AbstractLookup(instanceContent);

    public QLEditor() {
        this(null);
    }

    public QLEditor(QLEditorSupport support) {
        super(support);
        instanceContent.add(this);
        instanceContent.add(support);
        instanceContent.add(support.getSession());
    }

    public QLEditorSupport getQLEditorSupport() {
        return (QLEditorSupport) cloneableEditorSupport();
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    /**
     * <p>
     * Called when the last editor in a clone group is closing.</p>
     * <p>
     * This method will close the corresponding session object if the call
     * to <tt>super.closeLast()</tt> returns <tt>true</tt>.</p>
     *
     * @return <tt>true</tt> if the editor is ready to be closed,
     *         <tt>false</tt> to cancel
     * @see Session#close()
     */
    @Override
    protected boolean closeLast() {
        if (!super.closeLast()) {
            return false;
        }

        // close the corresponding session object.
        // NOTE:
        // because the Session.close() method eventually call this method
        // indirectly after setting the session's open flag to false.
        // So call Session.close() ONLY if the session is open to avoid
        // recursion.
        Session session = getQLEditorSupport().getSession();
        if (session.isOpen()) {
            try {
                session.close();
            } catch (ConnectionException ex) {

                // TODO: handle exception
                Exceptions.printStackTrace(ex);
            }
        }

        return true;
    }
}
