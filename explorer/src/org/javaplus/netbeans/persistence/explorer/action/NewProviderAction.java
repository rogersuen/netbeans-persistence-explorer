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

import org.javaplus.netbeans.persistence.action.ActionBase;
import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;
import org.javaplus.netbeans.persistence.explorer.dialog.ProviderDialog;

public final class NewProviderAction extends ActionBase {

    private static final String KEY_NAME = "NewProviderAction.NAME";

    public NewProviderAction() {
        putValue(NAME, NbBundle.getMessage(NewProviderAction.class, KEY_NAME));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProviderDialog.showDialog();
    }
}
