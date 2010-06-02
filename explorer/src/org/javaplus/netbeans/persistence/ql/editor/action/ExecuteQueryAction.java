/*
 * @(#)ExecuteQueryAction.java   10/06/01
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

package org.javaplus.netbeans.persistence.ql.editor.action;

import org.javaplus.netbeans.persistence.action.ContextAwareActionBase;
import org.javaplus.netbeans.persistence.connection.Session;

import org.openide.awt.Actions;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

import java.awt.Component;

import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.JButton;

/**
 *
 * @author Roger Suen
 */
public class ExecuteQueryAction extends ContextAwareActionBase<Session>
        implements Presenter.Toolbar {
    private static final Logger logger =
        Logger.getLogger(ExecuteQueryAction.class.getName());

    public ExecuteQueryAction() {
        this(null);
    }

    public ExecuteQueryAction(Lookup context) {
        super(Session.class, context);
        putValue(NAME, "Execute Query");
        putValue("iconBase",
                 "org/javaplus/netbeans/persistence/resources/executeJPQL.gif");
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new ExecuteQueryAction(context);
    }

    @Override
    protected void actionPerformed() {
        Collection<? extends Session> instances = lookupResult.allInstances();
        if (instances.size() != 1) {
            return;
        }

        Session session = instances.iterator().next();
        session.executeQuery();
    }

    @Override
    protected void contextChanged() {}

    @Override
    public Component getToolbarPresenter() {
        JButton button = new JButton();
        Actions.connect(button, this);
        return button;
    }
}
