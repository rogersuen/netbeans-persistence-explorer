/*
 * @(#)ConnectionException.java   10/05/07
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
 * The connection exception is the general exception can be thrown by
 * operations in the <tt>Connection</tt> class.
 * @author Roger Suen
 */
public class ConnectionException extends Exception {

    /**
     * Creates a new instance of <code>ConnectionException</code>
     * without detail message.
     */
    public ConnectionException() {}

    /**
     * Constructs an instance of <code>ConnectionException</code>
     * with the specified detail message.
     * @param message the detail message.
     */
    public ConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of <tt>ConnectionException</tt>
     * with the specified cause.
     * @param cause the cause.
     */
    public ConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of <tt>ConnectionException</tt>
     * with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause.
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
