/*
 * @(#)ContextAwareActionBase.java   10/05/18
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

package org.javaplus.netbeans.persistence.action;

import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

import java.awt.event.ActionEvent;

import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 *
 * @author Roger Suen
 */
public abstract class ContextAwareActionBase<T> extends AbstractAction
        implements ContextAwareAction, LookupListener {
    private final Class<T> type;
    private Lookup context;
    private Lookup.Result<T> lookupResult;

    protected ContextAwareActionBase(Class<T> type) {
        this.type = type;
        this.context = Utilities.actionsGlobalContext();
    }

    private void init() {
        assert SwingUtilities.isEventDispatchThread() :
               "this shall be called just from AWT thread";
        if (lookupResult != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lookupResult = context.lookupResult(type);
        lookupResult.addLookupListener(this);
        resultChanged(null);
    }

    public final void resultChanged(LookupEvent ev) {
        contextChanged((Collection<T>) lookupResult.allInstances());
    }

    public final void actionPerformed(ActionEvent e) {
        init();
        actionPerformed((Collection<T>) lookupResult.allInstances());
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        ContextAwareActionBase action = null;
        try {
            action = getClass().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException();
        }

        action.context = actionContext;
        return action;
    }

    protected abstract void contextChanged(Collection<T> instances);

    protected abstract void actionPerformed(Collection<T> instances);

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }
}
