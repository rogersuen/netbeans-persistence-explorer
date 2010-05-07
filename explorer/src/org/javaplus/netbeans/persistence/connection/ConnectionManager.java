/*
 * @(#)ConnectionManager.java   10/05/07
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

package org.javaplus.netbeans.persistence.connection;

import org.javaplus.netbeans.api.persistence.PersistenceUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 *
 * @author Roger Suen
 */
public class ConnectionManager extends ConnectionEventSupport {

    /**
     *
     */
    private static final ConnectionManager DEFAULT = new ConnectionManager();

    /**
     *
     */
    private static final Logger logger =
        Logger.getLogger(ConnectionManager.class.getName(),
                         ConnectionManager.class.getPackage().getName()
                         + ".Bundle");

    /**
     *
     */
    private final Map<PersistenceUnit, Connection> connectionsMap =
        new ConcurrentHashMap<PersistenceUnit, Connection>();

    /**
     *
     */
    private final ConnectionEventListener connectionListener =
        new ConnectionEventListenerSupport() {
        @Override
        public void connectionClosed(ConnectionEvent event) {
            Connection conn = event.getConnection();
            connectionsMap.remove(conn.getPersistenceUnit());
            fireConnectionClosedEvent(conn);
        }
    };

    /**
     *
     * @return
     */
    public static ConnectionManager getDefault() {
        return DEFAULT;
    }

    /**
     *
     * @param unit
     * @return
     */
    public Connection getConnection(PersistenceUnit unit) {
        if (unit == null) {
            throw new NullPointerException("null unit");
        }

        Connection conn = connectionsMap.get(unit);
        if ((conn != null) && conn.isOpen()) {
            return conn;
        }

        return null;
    }

    /**
     *
     * @param unit
     * @return
     * @throws ConnectionException
     */
    public Connection openConnection(PersistenceUnit unit)
            throws ConnectionException {
        if (unit == null) {
            throw new NullPointerException("null unit");
        }

        Connection conn = null;
        synchronized (connectionsMap) {
            conn = connectionsMap.get(unit);
            if ((conn == null) ||!conn.isOpen()) {
                conn = new Connection(this, unit);
                conn.addConnectionEventListener(connectionListener);
                connectionsMap.put(unit, conn);
            }
        }

        fireConnectionOpenedEvent(conn);
        return conn;
    }
}
