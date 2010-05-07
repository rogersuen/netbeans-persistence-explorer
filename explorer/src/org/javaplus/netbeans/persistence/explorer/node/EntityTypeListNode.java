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

import javax.persistence.metamodel.Metamodel;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;
import org.openide.util.NbBundle;


/**
 *
 * @author roger
 */
public class EntityTypeListNode extends NodeBase {

    public static final String LAYER_FOLDER = "EntityTypeListNode";
    private final Metamodel metamodel;

    public EntityTypeListNode(Metamodel metamodel) {
        if (metamodel == null) {
            throw new NullPointerException("null metamodel");
        }
        this.metamodel = metamodel;
        this.lookup.getInstanceContent().add(metamodel);
        initProperties();
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    private void initProperties() {
        // immutable properties
        setName("EntityTypeListNode");
        setShortDescription(NbBundle.getMessage(UnitListNode.class,
                "EntityTypeListNode.SHORT_DESCRIPTION"));
        setIconBaseWithExtension(FOLDER_ICON_BASE);

        // dynamic properties
        updateProperties();
    }

    @Override
    protected void updateProperties() {
        // update display name
        int childCount = getChildren().getNodesCount(true);
        String displayName = NbBundle.getMessage(UnitListNode.class,
                "EntityTypeListNode.DISPLAY_NAME", childCount);
        setDisplayName(displayName);
    }
}
