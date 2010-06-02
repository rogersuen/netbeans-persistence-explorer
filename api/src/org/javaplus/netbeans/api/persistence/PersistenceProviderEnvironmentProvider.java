/*
 * @(#)PersistenceProviderEnvironmentProvider.java   10/06/01
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

import org.openide.loaders.DataObject;
import org.openide.loaders.Environment;
import org.openide.util.Lookup;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Environment provider implementation for persistence provider definition
 * files.
 * @author Roger Suen
 */
public class PersistenceProviderEnvironmentProvider
        implements Environment.Provider {
    private static final Logger logger =
        Logger.getLogger(
            PersistenceProviderEnvironmentProvider.class.getName());

    /**
     * Singleton instance.
     */
    private static final Environment.Provider instance =
        new PersistenceProviderEnvironmentProvider();

    /**
     * Returns an instance of <tt>Environment.Provider</tt>.
     * @return an instance of <tt>Environment.Provider</tt>
     */
    public static Environment.Provider getInstance() {
        return instance;
    }

    public Lookup getEnvironment(DataObject obj) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "getting environment for the data object: {0}",
                       obj.getPrimaryFile().getPath());
        }

        return PersistenceProviderConverter.getLookupProvider(obj).getLookup();
    }
}
