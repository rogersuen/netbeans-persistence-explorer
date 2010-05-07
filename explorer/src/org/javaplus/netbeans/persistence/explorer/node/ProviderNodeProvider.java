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
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderFactory;
import org.openide.util.Lookup;

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
        public NodeProvider createNodeProvider(Lookup lookup) {
            return new ProviderNodeProvider(lookup);
        }
    }

    /**
     * Constructor.
     */
    private ProviderNodeProvider(Lookup lookup) {
        super(lookup);
    }

    /**
     * {@inheritDoc }
     * @return an immutable list of <tt>ProviderNode</tt>.
     */
    public List<Node> getNodes() {

        // TODO: fixed this according to UnitNodeProvider.
        
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
