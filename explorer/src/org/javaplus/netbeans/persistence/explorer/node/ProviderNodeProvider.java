/*
 * @(#)ProviderNodeProvider.java   10/06/01
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

import org.javaplus.netbeans.api.persistence.PersistenceProvider;
import org.javaplus.netbeans.api.persistence.PersistenceProviderManager;
import org.javaplus.netbeans.api.persistence.PersistenceProviderRegistryEvent;
import org.javaplus.netbeans.api.persistence
    .PersistenceProviderRegistryListener;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderBase;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderFactory;

import org.openide.nodes.Node;
import org.openide.util.Lookup;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * TODO: javadoc
 * @author Roger Suen
 */
public class ProviderNodeProvider extends NodeProviderBase {


    /**
     * <tt>NodeProviderFactory</tt> implementation.
     */
    public static final class Factory implements NodeProviderFactory {
        private static final Factory DEFAULT = new Factory();

        /**
         * Returns the singleton instance of the node provider factory.
         * @return an instance of <tt>Factory</tt>.
         */
        public static NodeProviderFactory getInstance() {
            return DEFAULT;
        }

        /**
         * {@inheritDoc }
         * <p>
         * This method returns a new instance on each call.<p>
         * @param lookup {@inheritDoc }
         * @return a new instance of <tt>UnitNodeProvider</tt>.
         */
        @Override
        public NodeProvider createNodeProvider(Lookup lookup) {
            return new ProviderNodeProvider(lookup);
        }
    }


    /**
     * A map helps to ensure returning the same provider node for the same
     * provider. Both keys and values are weak references
     */
    private final Map<PersistenceProvider, Reference<ProviderNode>> nodesMap =
        new WeakHashMap<PersistenceProvider, Reference<ProviderNode>>();

    /**
     * Constructor.
     */
    private ProviderNodeProvider(Lookup lookup) {
        super(lookup);
        PersistenceProviderManager manager =
            PersistenceProviderManager.getDefault();
        manager.addRegistryListener(new PersistenceProviderRegistryListener() {
            @Override
            public void registryChanged(
                    PersistenceProviderRegistryEvent event) {
                fireChangeEvent();
            }
        });
    }

    /**
     * {@inheritDoc }
     * @return an immutable list of <tt>ProviderNode</tt>.
     */
    @Override
    public List<Node> getNodes() {
        PersistenceProvider[] providers =
            PersistenceProviderManager.getDefault().getProviders();
        return getProviderNodes(providers);
    }

    private synchronized List<Node> getProviderNodes(PersistenceProvider[] providers) {
        ArrayList<Node> result = new ArrayList<Node>(providers.length);
        for (int i = 0; i < providers.length; i++) {
            result.add(getProviderNode(providers[i]));
        }
        return Collections.unmodifiableList(result);
    }

    private Node getProviderNode(PersistenceProvider provider) {
        ProviderNode result = null;
        Reference<ProviderNode> ref = nodesMap.get(provider);
        if (ref != null) {
            result = ref.get();
            if (result != null) {
                return result;
            }
        }
        result = new ProviderNode(provider);
        nodesMap.put(provider, new WeakReference<ProviderNode>(result));
        return result;
    }
}
