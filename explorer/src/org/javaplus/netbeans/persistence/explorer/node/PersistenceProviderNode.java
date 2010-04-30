/*
 * @(#)PersistenceProviderNode.java   10/04/20
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

import org.javaplus.netbeans.api.persistence.explorer.node.PersistenceExplorerNode;
import org.javaplus.netbeans.api.persistence.PersistenceProvider;

import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author roger
 */
public class PersistenceProviderNode extends PersistenceExplorerNode {

    public static final String LAYER_FOLDER = "PersistenceProviderNode";
    private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/resources/persistence.gif";

    private PersistenceProviderNode(Lookup lookup) {
        super();
        addLookup(lookup);
        initProperties();
    }

    public static Node getInstance(Lookup lookup) {
        return new PersistenceProviderNode(lookup);
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    private void initProperties() {
        PersistenceProvider provider =
                getLookup().lookup(PersistenceProvider.class);

        // init our properties
        setName(provider.getName());
        setDisplayName(provider.getDisplayName());
        setShortDescription(provider.getDescription());
        setIconBaseWithExtension(ICON_BASE);
    }
}
