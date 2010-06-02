/*
 * @(#)PersistenceProviderException.java   10/06/02
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

package org.javaplus.netbeans.api.persistence;

/**
 *
 * @author Roger Suen
 */
public class PersistenceProviderException extends Exception {

    /**
     * Creates a new instance of <tt>PersistenceProviderException</tt>
     * without detail message.
     */
    public PersistenceProviderException() {}

    /**
     * Constructs an instance of <tt>PersistenceProviderException</tt>
     * with the specified detail message.
     * @param message the detail message.
     */
    public PersistenceProviderException(String message) {
        super(message);
    }

    public PersistenceProviderException(Throwable cause) {
        super(cause);
    }

    public PersistenceProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
