/*
 * @(#)UnitActionBase.java   10/05/13
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

import org.javaplus.netbeans.persistence.action.ActionBase;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;

import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

import java.awt.event.ActionEvent;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * TODO: javadoc. refactor this class to be generic
 *
 * @author Roger Suen
 */
public abstract class UnitActionBase extends ActionBase
        implements ContextAwareAction {

    /**
     * Singleton private logger
     */
    private static final Logger logger =
        Logger.getLogger(UnitActionBase.class.getName());
    private final Lookup context;
    private Lookup.Result<PersistenceUnit> lookupResult;
    private LookupListener lookupListener;

    /**
     *
     */
    protected UnitActionBase() {
        this(Utilities.actionsGlobalContext());
    }

    /**
     *
     * @param context
     */
    protected UnitActionBase(Lookup context) {
        if (context == null) {
            throw new NullPointerException("null context");
        }

        this.context = context;
        this.lookupListener = new LookupListenerImpl();
        this.lookupResult = context.lookupResult(PersistenceUnit.class);
        this.lookupResult.addLookupListener(lookupListener);
        selectionChanged();
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected final Collection<PersistenceUnit> getSelection() {
        return (Collection<PersistenceUnit>) lookupResult.allInstances();
    }

    /**
     * 
     */
    protected abstract void selectionChanged();

    /**
     * 
     * @param event
     */
    abstract public void actionPerformed(ActionEvent event);

    /**
     * 
     */
    private final class LookupListenerImpl implements LookupListener {
        public void resultChanged(LookupEvent ev) {
            selectionChanged();
        }
    }
}
