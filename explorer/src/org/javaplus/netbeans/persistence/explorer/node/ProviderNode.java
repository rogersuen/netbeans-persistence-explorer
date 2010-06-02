/*
 * @(#)ProviderNode.java   10/04/20
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
import org.javaplus.netbeans.api.persistence.PersistenceProvider;


/**
 *
 * @author roger
 */
public class ProviderNode extends NodeBase {

    public static final String LAYER_FOLDER = "ProviderNode";
    private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/resources/persistence.gif";
            private final PersistenceProvider provider;
    public ProviderNode(PersistenceProvider provider) {
        if (provider == null) {
            throw new NullPointerException("null provider");
        }

        this.provider = provider;
        this.lookup.getInstanceContent().add(provider);
        initProperties();
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    private void initProperties() {
        // init our properties
        setName(provider.getName());
        setDisplayName(provider.getDisplayName());
        setShortDescription(provider.getDescription());
        setIconBaseWithExtension(ICON_BASE);
    }
}
