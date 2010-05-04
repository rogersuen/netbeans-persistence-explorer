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

import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;


/**
 *
 * @author roger
 */
public class UnitNode extends NodeBase {

    public static final String LAYER_FOLDER = "UnitNode";
    private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/resources/persistence.gif";
    private final PersistenceUnit unit;

    UnitNode(PersistenceUnit unit) {
        super();

        this.unit = unit;
        initProperties();
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    public PersistenceUnit getUnit() {
        return unit;
    }

    private void initProperties() {
        setName(unit.getName());
        setDisplayName(unit.getDisplayName());
        setShortDescription(unit.getDescription());
        setIconBaseWithExtension(ICON_BASE);
    }
}
