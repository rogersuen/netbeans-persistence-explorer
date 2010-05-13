/*
 * @(#)UnitNode.java   10/05/13
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

import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;
import org.javaplus.netbeans.persistence.connection.Connection;
import org.javaplus.netbeans.persistence.connection.ConnectionEvent;
import org.javaplus.netbeans.persistence.connection.ConnectionEventListener;
import org.javaplus.netbeans.persistence.connection.ConnectionManager;

/**
 * This class defines the node that represents an entry in the persistence unit
 * registry.
 * @author Roger Suen
 */
public class UnitNode extends NodeBase implements ConnectionEventListener {
    private static final String LAYER_FOLDER = "UnitNode";
    private static final String ICON_BASE =
        "org/javaplus/netbeans/persistence/resources/unit.gif";
    private final PersistenceUnit unit;

    /**
     * Constructs a new instance of <tt>UnitNode</tt> with the specified
     * <tt>PersistenceUnit</tt> object.
     * @param unit the persistence unit object which this node delegates to,
     *             cannot be <tt>null</tt>.
     * @throws NullPointerException if <tt>unit</tt> is null.
     */
    UnitNode(PersistenceUnit unit) {
        if (unit == null) {
            throw new NullPointerException("null unit");
        }

        // add PersistenceUnit to lookup
        this.unit = unit;
        this.lookup.getInstanceContent().add(unit);

        // listen to the ConnectionEvent, so we can add the Connection to,
        // and remove Connection from the lookup.
        ConnectionManager.getDefault().addConnectionEventListener(this);
        initProperties();
    }

    /**
     * {@inheritDoc }
     * @return {@value #LAYER_FOLDER}
     */
    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    private void initProperties() {
        setName(unit.getName());
        setDisplayName(unit.getDisplayName());
        setShortDescription(unit.getDescription());
        setIconBaseWithExtension(ICON_BASE);
    }

    /**
     * {@inheritDoc }
     * <p>
     * If the connection is of the persistence unit delegated by this node,
     * add the connection to the lookup of this node.</p>
     * @param event {@inheritDoc }
     */
    public void connectionOpened(ConnectionEvent event) {
        Connection conn = event.getConnection();
        if (unit.equals(conn.getPersistenceUnit())) {
            UnitNode.this.lookup.getInstanceContent().add(conn);
        }
    }

    /**
     * {@inheritDoc }
     * <p>
     * If the connection is of the persistence unit delegated by this node,
     * remove the connection from the lookup of this node.</p>
     * @param event {@inheritDoc }
     */
    public void connectionClosed(ConnectionEvent event) {
        Connection conn = event.getConnection();
        if (unit.equals(conn.getPersistenceUnit())) {
            UnitNode.this.lookup.getInstanceContent().remove(conn);
        }
    }

    /**
     * {@inheritDoc }
     * <p>
     * If the connection is of the persistence unit delegated by this node,
     * remove the connection from the lookup of this node.</p>
     * @param event {@inheritDoc }
     */
    public void connectionErrorOccurred(ConnectionEvent event) {

        // according to the spec of the ConnectionEventListener,
        // the connection with errors can no longer be used,
        // so remove it from the lookup of this node.
        Connection conn = event.getConnection();
        if (unit.equals(conn.getPersistenceUnit())) {
            UnitNode.this.lookup.getInstanceContent().remove(conn);
        }
    }
}
