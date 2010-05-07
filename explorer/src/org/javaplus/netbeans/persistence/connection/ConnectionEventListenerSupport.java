/*
 * @(#)ConnectionEventListenerSupport.java   10/05/07
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
 *
 * @author Roger Suen
 */
public class ConnectionEventListenerSupport implements ConnectionEventListener {
    public void connectionOpened(ConnectionEvent event) {}

    public void connectionClosed(ConnectionEvent event) {}

    public void connectionErrorOccurred(ConnectionEvent event) {}
}
