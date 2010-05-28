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
import javax.persistence.metamodel.Metamodel;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.persistence.connection.ConnectionEvent;

import org.openide.nodes.Node;

import java.util.List;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderBase;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeProviderFactory;
import org.javaplus.netbeans.persistence.connection.Connection;
import org.javaplus.netbeans.persistence.connection.ConnectionEventListener;
import org.javaplus.netbeans.persistence.connection.ConnectionManager;
import org.openide.util.Lookup;

/**
 * TODO: javadoc
 * @author Roger Suen
 */
public class MetamodelNodeProvider extends NodeProviderBase
        implements ConnectionEventListener {

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
         * @return a new instance of <tt>MetamodelNodeProvider</tt>.
         */
        public NodeProvider createNodeProvider(Lookup lookup) {
            MetamodelNodeProvider provider = new MetamodelNodeProvider(lookup);
            ConnectionManager connMgr = ConnectionManager.getDefault();
            connMgr.addConnectionEventListener(provider);
            return provider;
        }
    }

    /**
     * Constructor.
     */
    private MetamodelNodeProvider(Lookup lookup) {
        super(lookup);
    }

    /**
     * {@inheritDoc }
     * @return an immutable list of <tt>UnitNode</tt>
     */
    public List<Node> getNodes() {
        if (lookup == null) {
            return Collections.EMPTY_LIST;
        }
        PersistenceUnit pu = lookup.lookup(PersistenceUnit.class);
        if (pu == null) {
            return Collections.EMPTY_LIST;
        }

        Connection conn = ConnectionManager.getDefault().getConnection(pu);
        if (conn == null) {
            return Collections.EMPTY_LIST;
        }

        Metamodel metamodel = conn.getMetamodel();
        ArrayList<Node> result = new ArrayList<Node>();
        result.add(new EntityTypeListNode(metamodel));
        result.add(new ManagedTypeListNode(metamodel));
        return Collections.unmodifiableList(result);
    }

    //
    // ConnectionEventListener interface
    //
    public void connectionClosed(ConnectionEvent event) {
        processConnectionEvent(event);
    }

    public void connectionErrorOccurred(ConnectionEvent event) {
        processConnectionEvent(event);
    }

    public void connectionOpened(ConnectionEvent event) {
        processConnectionEvent(event);
    }

    private void processConnectionEvent(ConnectionEvent event) {
        if (lookup != null
                && event.getConnection().getPersistenceUnit().equals(
                lookup.lookup(PersistenceUnit.class))) {
            fireChangeEvent();
        }
    }
}
