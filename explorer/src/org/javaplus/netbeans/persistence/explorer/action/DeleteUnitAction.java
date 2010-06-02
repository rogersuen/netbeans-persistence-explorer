/*
 * @(#)DeleteUnitAction.java   10/06/02
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
import org.javaplus.netbeans.api.persistence.PersistenceUnitException;
import org.javaplus.netbeans.api.persistence.PersistenceUnitManager;
import org.javaplus.netbeans.persistence.action.ContextAwareActionBase;
import org.javaplus.netbeans.persistence.connection.Connection;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import java.util.Collection;

import javax.swing.Action;

/**
 *
 * @author Roger Suen
 */
public class DeleteUnitAction extends ContextAwareActionBase<PersistenceUnit> {
    private static final String KEY_NAME = "DeleteUnitAction.NAME";

    public DeleteUnitAction() {
        this(null);
    }

    public DeleteUnitAction(Lookup context) {
        super(PersistenceUnit.class, context);
        putValue(NAME, NbBundle.getMessage(DeleteUnitAction.class, KEY_NAME));
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new DeleteUnitAction(context);
    }

    @Override
    protected void actionPerformed() {
        Collection<? extends PersistenceUnit> units =
            lookupResult.allInstances();
        for (PersistenceUnit u : units) {
            try {
                PersistenceUnitManager.getDefault().removeUnit(u);
            } catch (PersistenceUnitException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected void contextChanged() {
        // enabled if there is at least one unit selected, and no connection
        // opened on any selected unit
        if ((lookupResult.allItems().size() > 0)
                && (context.lookup(Connection.class) == null)) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }
}
