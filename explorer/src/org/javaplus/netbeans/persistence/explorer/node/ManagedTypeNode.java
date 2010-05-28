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

import javax.persistence.metamodel.ManagedType;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;


/**
 *
 * @author roger
 */
public class ManagedTypeNode extends NodeBase {

    public static final String LAYER_FOLDER = "ManagedTypeNode";
    private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/resources/entity.gif";
    private final ManagedType managedType;

    ManagedTypeNode(ManagedType managedType) {
        if (managedType == null) {
            throw new NullPointerException("null entityType");
        }

        this.managedType = managedType;
        lookup.getInstanceContent().add(managedType);
        initProperties();
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    private void initProperties() {
        Class javaType = managedType.getJavaType();
        setName(javaType.getName());
        setDisplayName(javaType.getSimpleName());
        setShortDescription(javaType.getName());
        setIconBaseWithExtension(ICON_BASE);
    }
}
