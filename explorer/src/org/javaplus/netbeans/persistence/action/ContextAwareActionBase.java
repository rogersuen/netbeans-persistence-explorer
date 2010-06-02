/*
 * @(#)ContextAwareActionBase.java   10/06/01
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

import javax.swing.Action;
import javax.swing.SwingUtilities;

/**
 *
 * @author Roger Suen
 */
public abstract class ContextAwareActionBase<T> extends ActionBase
        implements ContextAwareAction {
    protected final Lookup context;
    protected Lookup.Result<T> lookupResult;
    private final Class<T> type;
    private LookupListener lookupListener;

    protected ContextAwareActionBase(Class<T> type, Lookup context) {
        if (type == null) {
            throw new NullPointerException("null type");
        }

        this.type = type;
        if (context != null) {
            this.context = context;
        } else {
            this.context = Utilities.actionsGlobalContext();
        }
    }

    @Override
    public abstract Action createContextAwareInstance(Lookup context);

    protected abstract void actionPerformed();

    protected void contextChanged() {

        // do nothing
    }

    @Override
    public final boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    @Override
    public final void actionPerformed(ActionEvent e) {
        init();
        actionPerformed();
    }

    private void init() {
        assert SwingUtilities.isEventDispatchThread() :
               "this shall be called just from AWT thread";
        if (lookupResult != null) {
            return;
        }

        lookupListener = new LookupListenerImpl();
        lookupResult = context.lookupResult(type);
        lookupResult.addLookupListener(lookupListener);
        contextChanged();
    }

    private class LookupListenerImpl implements LookupListener {
        @Override
        public void resultChanged(LookupEvent ev) {
            contextChanged();
        }
    }
}
