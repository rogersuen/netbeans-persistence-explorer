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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import org.javaplus.netbeans.api.persistence.PersistenceUnitRegistryEvent;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.api.persistence.PersistenceUnitManager;

import org.openide.nodes.Node;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.javaplus.netbeans.api.persistence.PersistenceUnitRegistryListener;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderBase;

/**
 *
 * @author Roger Suen
 */
public class UnitNodeProvider extends NodeProviderBase {

    /**
     * The singleton instance.
     */
    private static final NodeProvider instance =
            new UnitNodeProvider();
    /**
     * A map helps to ensure returning the same unit node for the same
     * unit. Both keys and values are weak references
     */
    private final Map<PersistenceUnit, Reference<UnitNode>> nodesMap =
            new WeakHashMap<PersistenceUnit, Reference<UnitNode>>();

    /**
     * Constructor.
     */
    private UnitNodeProvider() {
        PersistenceUnitManager puManager = PersistenceUnitManager.getDefault();
        puManager.addRegistryListener(new PersistenceUnitRegistryListener() {

            public void registryChanged(PersistenceUnitRegistryEvent event) {
                fireChange();
            }
        });

    }

    /**
     * Returns an instance of <tt>NodeProvider</tt>.
     * @return an instance of <tt>NodeProvider</tt>
     */
    public static NodeProvider getInstance() {
        return instance;
    }

    /**
     * {@inheritDoc }
     * @return an immutable list of <tt>UnitNode</tt>
     */
    public List<Node> getNodes() {
        PersistenceUnit[] units =
                PersistenceUnitManager.getDefault().getUnits();
        return getUnitNodes(units);
    }

    private synchronized List<Node> getUnitNodes(PersistenceUnit[] units) {
        ArrayList<Node> result = new ArrayList<Node>(units.length);
        for (int i = 0; i < units.length; i++) {
            result.add(getUnitNode(units[i]));
        }
        return Collections.unmodifiableList(result);

    }

    private Node getUnitNode(PersistenceUnit unit) {
        UnitNode result = null;
        Reference<UnitNode> ref = nodesMap.get(unit);
        if (ref != null) {
            result = ref.get();
            if (result != null) {
                return result;
            }
        }

        result = new UnitNode(unit);
        nodesMap.put(unit, new WeakReference<UnitNode>(result));
        return result;
    }
}
