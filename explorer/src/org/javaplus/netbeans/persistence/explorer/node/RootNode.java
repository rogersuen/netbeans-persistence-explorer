/*
 * @(#)RootNode.java   10/04/15
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

import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;
import org.openide.util.NbBundle;


/**
 *
 * @author Roger Suen
 */
public class RootNode extends NodeBase {
    private static final String LAYER_FOLDER = "RootNode";
    private static final String ICON_BASE =
        "org/javaplus/netbeans/persistence/resources/persistence.gif";
    private static RootNode instance;

    private RootNode() {
        initProperties();
    }

    public static RootNode getInstance() {
        if (instance == null) {
            instance = new RootNode();
        }

        return instance;
    }

    private void initProperties() {
        setName("Persistence");
        setDisplayName(NbBundle.getMessage(RootNode.class,
                                           "RootNode.DISPLAY_NAME"));
        setShortDescription(NbBundle.getMessage(RootNode.class,
                "RootNode.SHORT_DESCRIPTION"));
        setIconBaseWithExtension(ICON_BASE);
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }
}
