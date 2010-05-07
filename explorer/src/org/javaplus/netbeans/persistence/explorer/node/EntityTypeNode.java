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

import javax.persistence.metamodel.EntityType;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;


/**
 *
 * @author roger
 */
public class EntityTypeNode extends NodeBase {

    public static final String LAYER_FOLDER = "EntityTypeNode";
    private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/resources/persistence.gif";
    private final EntityType entityType;

    EntityTypeNode(EntityType entityType) {
        if (entityType == null) {
            throw new NullPointerException("null entityType");
        }

        this.entityType = entityType;
        lookup.getInstanceContent().add(entityType);
        initProperties();
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    public EntityType getUnit() {
        return entityType;
    }

    private void initProperties() {
        setName(entityType.getName());
        setDisplayName(entityType.getName());
        setShortDescription(entityType.getName());
        setIconBaseWithExtension(ICON_BASE);
    }
}
