/*
 * @(#)ProviderListNode.java   10/06/07
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
public class ProviderListNode extends FolderNodeBase {
    public static final String LAYER_FOLDER = "ProviderListNode";
    private static ProviderListNode instance;

    private ProviderListNode() {
        super();
        initProperties();
    }

    public static ProviderListNode getInstance() {
        if (instance == null) {
            instance = new ProviderListNode();
        }

        return instance;
    }

    private void initProperties() {
        setName("ProviderListNode");
        setShortDescription(NbBundle.getMessage(ProviderListNode.class,
                "ProviderListNode.SHORT_DESCRIPTION"));

        // dynamic properties
        updateProperties();
    }

    @Override
    protected void updateProperties() {

        // update display name
        int childCount = getChildren().getNodesCount(true);
        String displayName = NbBundle.getMessage(ProviderListNode.class,
                                 "ProviderListNode.DISPLAY_NAME", childCount);
        setDisplayName(displayName);
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }
}
