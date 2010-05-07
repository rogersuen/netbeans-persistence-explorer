/*
 * @(#)ConnectionEventSupport.java   10/05/07
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

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author Roger Suen
 */
public abstract class ConnectionEventSupport {
    private final Set<ConnectionEventListener> listeners =
        new CopyOnWriteArraySet<ConnectionEventListener>();

    protected ConnectionEventSupport() {}

    /**
     * Adds a <tt>ConnectionEventListener</tt> object that is notified
     * about <tt>ConnectionEvent</tt>s. If the specified listener object
     * has already been added, no duplication will be added.
     *
     * @param listener the listener object to add, cannot be <tt>null</tt>.
     * @throws NullPointerException if <tt>listener</tt> is <tt>null</tt>.
     */
    public void addConnectionEventListener(ConnectionEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("null listener");
        }

        listeners.add(listener);
    }

    /**
     * Removes the specified <tt>ConnectionEventListener</tt> object if
     * it was added before.
     * @param listener the listener object to remove, cannot be <tt>null</tt>.
     * @throws NullPointerException if <tt>listener</tt> is <tt>null</tt>.
     */
    public void removeConnectionEventListener(
            ConnectionEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("null listener");
        }

        listeners.remove(listener);
    }

    protected void fireConnectionOpenedEvent(Connection connection) {
        ConnectionEvent event = new ConnectionEvent(connection);
        for (ConnectionEventListener listener : listeners) {
            listener.connectionOpened(event);
        }
    }

    protected void fireConnectionClosedEvent(Connection connection) {
        ConnectionEvent event = new ConnectionEvent(connection);
        for (ConnectionEventListener listener : listeners) {
            listener.connectionClosed(event);
        }
    }

    protected void fireConnectionErrorOccurredEvent(Connection connection,
            ConnectionException exception) {
        ConnectionEvent event = new ConnectionEvent(connection, exception);
        for (ConnectionEventListener listener : listeners) {
            listener.connectionErrorOccurred(event);
        }
    }
}
