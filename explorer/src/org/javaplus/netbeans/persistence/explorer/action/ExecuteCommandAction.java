/*
 * @(#)ExecuteCommandAction.java   10/05/13
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

package org.javaplus.netbeans.persistence.explorer.action;

import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.persistence.connection.Connection;
import org.javaplus.netbeans.persistence.connection.ConnectionException;
import org.javaplus.netbeans.persistence.connection.ConnectionManager;
import org.openide.util.Exceptions;

import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import org.javaplus.netbeans.persistence.ql.editor.QLEditorSupport;

public class ExecuteCommandAction extends UnitActionBase {
    private static final String KEY_NAME = "ExecuteCommandAction.NAME";
    private static final Logger logger =
        Logger.getLogger(ExecuteCommandAction.class.getName());

    public ExecuteCommandAction() {
        super();
        init();
    }

    private ExecuteCommandAction(Lookup context) {
        super(context);
        init();
    }

    private void init() {
        putValue(NAME, NbBundle.getMessage(CloseUnitAction.class, KEY_NAME));
    }

    public void actionPerformed(ActionEvent e) {
        Collection<PersistenceUnit> units = getSelection();
        if (units.size() == 1) {
            PersistenceUnit unit = units.iterator().next();
            Connection conn =
                ConnectionManager.getDefault().getConnection(unit);
            if ((conn != null) && conn.isOpen()) {
                try {
                    conn.openSession();
                } catch (ConnectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    protected void selectionChanged() {

        // Determines the enable state of this action.
        // Enabled only when a single peristence unit node is selected,
        // and the corresponding persistence unit has an open connection
        // on it.
        boolean isEnabled = false;
        Collection<PersistenceUnit> units = getSelection();
        if (units.size() == 1) {
            PersistenceUnit unit = units.iterator().next();
            Connection conn =
                ConnectionManager.getDefault().getConnection(unit);
            if ((conn != null) && conn.isOpen()) {
                isEnabled = true;
            }
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "The enabled state of the action {0} will set to {1}.",
                       new Object[] { this,
                                      isEnabled });
        }

        setEnabled(isEnabled);
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        if (logger.isLoggable(Level.FINER)) {
            Action action = new ExecuteCommandAction(actionContext);
            logger.log(Level.FINER,
                       "Context aware action instance created: {0}", action);
            return action;
        } else {
            return new ExecuteCommandAction(actionContext);
        }
    }
}
