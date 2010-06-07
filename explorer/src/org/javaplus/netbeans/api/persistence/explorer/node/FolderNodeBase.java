/*
 * @(#)FolderNodeBase.java   10/06/07
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

package org.javaplus.netbeans.api.persistence.explorer.node;

import org.openide.util.ImageUtilities;

import java.awt.Image;

import java.beans.BeanInfo;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * The base class for all persistence explorer nodes representing folders.
 * This class adds support to folder icons to the base class.
 * @author Roger Suen
 */
public abstract class FolderNodeBase extends NodeBase {
    private static final String DEFAULT_FOLDER_ICON_BASE =
        "org/javaplus/netbeans/api/persistence/explorer/node/resources/"
        + "defaultFolder.gif";

    public FolderNodeBase() {
        super();
        setIconBaseWithExtension(DEFAULT_FOLDER_ICON_BASE);
    }

    //
    // The following code comes from
    // org.netbeans.modules.db.explorer.node.DriverListNode
    //
    @Override
    public Image getIcon(int type) {
        Image result = null;
        if (type == BeanInfo.ICON_COLOR_16x16) {
            result = icon2Image("Nb.Explorer.Folder.icon");
        }

        if (result == null) {
            result = icon2Image("Tree.closedIcon");
        }

        if (result == null) {
            result = super.getIcon(type);
        }

        return result;
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image result = null;
        if (type == BeanInfo.ICON_COLOR_16x16) {
            result = icon2Image("Nb.Explorer.Folder.openedIcon");
        }

        if (result == null) {
            result = icon2Image("Tree.openIcon");
        }

        if (result == null) {
            result = super.getOpenedIcon(type);
        }

        return result;
    }

    private static Image icon2Image(String key) {
        Object obj = UIManager.get(key);
        if (obj instanceof Image) {
            return (Image) obj;
        }

        if (obj instanceof Icon) {
            Icon icon = (Icon) obj;
            return ImageUtilities.icon2Image(icon);
        }

        return null;
    }
}
