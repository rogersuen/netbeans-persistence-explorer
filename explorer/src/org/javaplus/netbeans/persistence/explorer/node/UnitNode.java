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
import org.javaplus.netbeans.api.persistence.PersistenceUnit;

import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author roger
 */
public class UnitNode extends PersistenceExplorerNode {
    public static final String LAYER_FOLDER = "UnitNode";
    private static final String ICON_BASE =
        "org/javaplus/netbeans/persistence/resources/persistence.gif";

    private UnitNode(Lookup lookup) {
        super();

        addLookup(lookup);
        initProperties();
    }

    private void initProperties() {
        PersistenceUnit unit = getLookup().lookup(PersistenceUnit.class);
        // init our properties
        setName(unit.getName());
        setDisplayName(unit.getDisplayName());
        setShortDescription(unit.getDescription());
        setIconBaseWithExtension(ICON_BASE);
    }

    public static Node getInstance(Lookup lookup) {
        return new UnitNode(lookup);
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

}
