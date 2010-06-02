/*
 * @(#)DeleteProviderAction.java   10/06/02
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

import org.javaplus.netbeans.api.persistence.PersistenceProvider;
import org.javaplus.netbeans.api.persistence.PersistenceProviderException;
import org.javaplus.netbeans.api.persistence.PersistenceProviderManager;
import org.javaplus.netbeans.persistence.action.ContextAwareActionBase;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.Action;

/**
 *
 * @author Roger Suen
 */
public class DeleteProviderAction
        extends ContextAwareActionBase<PersistenceProvider> {
    private static final String KEY_NAME = "DeleteProviderAction.NAME";
    private static final Logger logger =
        Logger.getLogger(DeleteProviderAction.class.getName());

    public DeleteProviderAction() {
        this(null);
    }

    public DeleteProviderAction(Lookup context) {
        super(PersistenceProvider.class, context);
        putValue(NAME, NbBundle.getMessage(CloseUnitAction.class, KEY_NAME));
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new DeleteProviderAction(context);
    }

    @Override
    protected void actionPerformed() {
        Collection<? extends PersistenceProvider> providers =
            lookupResult.allInstances();
        for (PersistenceProvider p : providers) {
            try {
                PersistenceProviderManager.getDefault().removeProvider(p);
            } catch (PersistenceProviderException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected void contextChanged() {
        if (lookupResult.allItems().size() > 0) {
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }
}
