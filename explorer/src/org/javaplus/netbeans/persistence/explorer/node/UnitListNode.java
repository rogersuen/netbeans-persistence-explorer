/*
 * @(#)PersistenceProvidersNode.java   10/04/19
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
package org.javaplus.netbeans.persistence.explorer.node;

import org.javaplus.netbeans.api.persistence.explorer.node.FolderNodeBase;
import org.openide.util.NbBundle;

/**
 *
 * @author Roger Suen
 */
public class UnitListNode extends FolderNodeBase {

    public static final String LAYER_FOLDER = "UnitListNode";
    private static UnitListNode instance;

    private UnitListNode() {
        super();
        initProperties();
    }

    public static UnitListNode getInstance() {
        if (instance == null) {
            instance = new UnitListNode();
        }

        return instance;
    }

    private void initProperties() {
        // immutable properties
        setName("UnitListNode");
        setShortDescription(NbBundle.getMessage(UnitListNode.class,
                "UnitListNode.SHORT_DESCRIPTION"));

        // dynamic properties
        updateProperties();
    }

    @Override
    protected void updateProperties() {
        // update display name
        int childCount = getChildren().getNodesCount(true);
        String displayName = NbBundle.getMessage(UnitListNode.class,
                "UnitListNode.DISPLAY_NAME", childCount);
        setDisplayName(displayName);
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }
}
