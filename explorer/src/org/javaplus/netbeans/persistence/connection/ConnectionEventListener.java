/*
 * @(#)ConnectionEventListener.java   10/05/07
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

/**
 * An object that registers to be notified of {@link ConnectionEvent}.
 *
 * @author Roger Suen
 * @see ConnectionEvent
 * @see ConnectionManager
 */
public interface ConnectionEventListener {

    /**
     * Notifies this <tt>ConnectionEventListener</tt> that a connection has
     * been opened.
     * @param event the event object.
     */
    void connectionOpened(ConnectionEvent event);

    /**
     * Notifies this <tt>ConnectionEventListener</tt> that a connection has
     * been closed.
     * @param event the event object.
     */
    void connectionClosed(ConnectionEvent event);

    /**
     * Notifies this <tt>ConnectionEventListener</tt> that a fatal error has
     * occurred and the connection can no longer be used.
     * @param event the event object.
     */
    void connectionErrorOccurred(ConnectionEvent event);
}
