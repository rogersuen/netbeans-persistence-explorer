/*
 * @(#)UnitNodeProvider.java   10/04/20
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Metamodel;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;

import org.openide.nodes.Node;

import java.util.List;
import javax.persistence.metamodel.ManagedType;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderBase;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderFactory;
import org.openide.util.Lookup;

/**
 * TODO: javadoc
 * @author Roger Suen
 */
public class AttributeNodeProvider extends NodeProviderBase {

    /**
     * <tt>NodeProviderFactory</tt> implementation.
     */
    public static final class Factory implements NodeProviderFactory {

        private static final Factory DEFAULT = new Factory();

        /**
         * Returns the singleton provider of the node provider factory.
         * @return an provider of <tt>Factory</tt>.
         */
        public static NodeProviderFactory getInstance() {
            return DEFAULT;
        }

        /**
         * {@inheritDoc }
         * <p>
         * This method returns a newly created instance on each call.<p>
         * @param lookup {@inheritDoc }
         * @return a new instance of <tt>AttributeNodeProvider</tt>.
         */
        public NodeProvider createNodeProvider(Lookup lookup) {
            return new AttributeNodeProvider(lookup);
        }
    }

    /**
     * Constructor.
     */
    private AttributeNodeProvider(Lookup lookup) {
        super(lookup);
    }

    /**
     * {@inheritDoc }
     * @return an immutable list of <tt>AttributeNode</tt>
     */
    public List<Node> getNodes() {
        ManagedType managedType = lookup.lookup(ManagedType.class);
        if (managedType == null) {
            assert managedType != null; // should never happen
            return Collections.EMPTY_LIST;
        }
        // create one AttributeNode for each Attribute
        Set<Attribute> attributes = managedType.getAttributes();
        int size = attributes.size();
        if (size == 0) {
            return Collections.EMPTY_LIST;
        } else {
            ArrayList<Node> result = new ArrayList<Node>(size);
            for (Attribute attribute : attributes) {
                result.add(new AttributeNode(attribute));
            }
            return Collections.unmodifiableList(result);
        }
    }
}
