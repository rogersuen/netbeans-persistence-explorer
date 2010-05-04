/*
 * @(#)NewUnitAction.java   10/04/20
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

import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;
import org.javaplus.netbeans.persistence.explorer.dialog.PersistenceUnitDialog;

public final class NewUnitAction extends BaseAction {

    private static final String KEY_NAME = "NewUnitAction.NAME";

    public NewUnitAction() {
        putValue(NAME, NbBundle.getMessage(NewUnitAction.class, KEY_NAME));
    }

    public void actionPerformed(ActionEvent e) {
        PersistenceUnitDialog.showDialog();
    }
}
