/*
 * @(#)PersistenceUnitException.java   10/06/02
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
public class PersistenceUnitException extends Exception {

    /**
     * Creates a new instance of <code>PersistenceUnitException</code>
     * without detail message.
     */
    public PersistenceUnitException() {}

    /**
     * Constructs an instance of <code>PersistenceUnitException</code>
     * with the specified detail message.
     * @param message the detail message.
     */
    public PersistenceUnitException(String message) {
        super(message);
    }

    public PersistenceUnitException(Throwable cause) {
        super(cause);
    }

    public PersistenceUnitException(String message, Throwable cause) {
        super(message, cause);
    }
}
