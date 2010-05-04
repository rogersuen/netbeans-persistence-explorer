/*
 * @(#)ProviderNodeProvider.java   10/04/20
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

import java.util.Collections;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;
import org.javaplus.netbeans.api.persistence.PersistenceProvider;
import org.javaplus.netbeans.api.persistence.PersistenceProviderManager;

import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import java.util.LinkedList;
import java.util.List;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderBase;

/**
 *
 * @author Roger Suen
 */
public class ProviderNodeProvider extends NodeProviderBase {

    /**
     * The singleton instance
     */
    private static final NodeProvider instance =
            new ProviderNodeProvider();

    /**
     * Constructor.
     */
    private ProviderNodeProvider() {
    }

    /**
     * Returns an instance of <tt>NodeProvider</tt>.
     * @return an instance of <tt>NodeProvider</tt>
     */
    public static NodeProvider getInstance() {
        return instance;
    }

    public List<Node> getNodes() {
        List<Node> nodes = new LinkedList<Node>();
        PersistenceProvider[] providers =
                PersistenceProviderManager.getDefault().getProviders();
        for (PersistenceProvider provider : providers) {
            InstanceContent ic = new InstanceContent();
            ic.add(provider);    // PeristenceProvider
            nodes.add(
                    ProviderNode.getInstance(new AbstractLookup(ic)));
        }

        return Collections.unmodifiableList(nodes);
    }
}
