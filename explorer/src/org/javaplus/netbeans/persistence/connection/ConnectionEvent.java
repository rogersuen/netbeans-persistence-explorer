/*
 * @(#)ConnectionEvent.java   10/05/07
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

import java.util.EventObject;

/**
 * An event object that provides information about the source of a
 * connection-related event. <tt>ConnectionEvent</tt> objects are generated
 * when a connection to a persistence unit is open, closed, and when an error
 * occurs. The <tt>ConnectionEvent</tt> object contains two kinds of
 * information:
 * <ul>
 *  <li>The connection as the source of the event.</li>
 *  <li>In the case of an error event, the <tt>ConnectionException</tt> object
 *      containing information about the error. </li>
 * </ul>
 *
 * @author Roger Suen
 * @see ConnectionManager
 * @see ConnectionEventListener
 */
public class ConnectionEvent extends EventObject {
    private ConnectionException exception;

    /**
     * Constructs a new <tt>ConnectionEvent</tt> object with the specified
     * <tt>Connection</tt> object as the source. <tt>ConnectionException</tt>
     * object defaults to <tt>null</tt>;
     * @param source    <tt>Connection</tt> object as the source.
     * @throws NullPointerException if <tt>source</tt> is <tt>null</tt>.
     */
    public ConnectionEvent(Connection source) {
        this(source, null);
    }

    /**
     * Constructs a new <tt>ConnectionEvent</tt> object with the specified
     * <tt>Connection</tt> object as the source, and the specified
     * <tt>ConnectionException</tt> object containing information about
     * the error.
     * @param source    <tt>Connection</tt> object as the source.
     * @param exception <tt>ConnectionException</tt> object containing
     *                  information about the error.
     * @throws NullPointerException if <tt>source</tt> is <tt>null</tt>.
     */
    public ConnectionEvent(Connection source, ConnectionException exception) {
        super(source);
        this.exception = exception;
    }

    /**
     * Returns the <tt>Connection</tt> object as the source of this event.
     * @return the <tt>Connection</tt> object as the source of this event.
     */
    public Connection getConnection() {
        return (Connection) getSource();
    }

    /**
     * Returns the <tt>ConnectionException</tt> object of this event. May be
     * <tt>null</tt>.
     * @return the <tt>ConnectionException</tt> object of this event
     */
    public ConnectionException getConnectionException() {
        return exception;
    }
}
