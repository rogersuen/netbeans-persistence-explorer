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

import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.api.persistence.PersistenceUnitManager;

import org.openide.nodes.Node;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Roger Suen
 */
public class UnitNodeProvider implements NodeProvider {
    /**
     * The singleton instance.
     */
    private static final NodeProvider instance =
        new UnitNodeProvider();

    /**
     * Returns an instance of <tt>NodeProvider</tt>.
     * @return an instance of <tt>NodeProvider</tt>
     */
    public static NodeProvider getInstance() {
        return instance;
    }

    public Node[] getNodes() {
        List<Node> nodes = new LinkedList<Node>();
        PersistenceUnit[] units =
            PersistenceUnitManager.getDefault().getUnits();
        for (PersistenceUnit unit : units) {
            InstanceContent ic = new InstanceContent();
            ic.add(unit);    // PersistenceUnit
            nodes.add(
                UnitNode.getInstance(new AbstractLookup(ic)));
        }

        return nodes.toArray(new Node[nodes.size()]);
    }
}
