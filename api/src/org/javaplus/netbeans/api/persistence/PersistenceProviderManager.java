/*
 * @(#)PersistenceProviderManager.java   10/04/28
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

import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

import java.util.Collection;

/**
 * The basic service for managing the registry of persistence providers in
 * the Persistence Explorer.
 * @author Roger Suen
 */
public final class PersistenceProviderManager {
    public static final String PROVIDERS_PATH =
        "Persistence/PersistenceProviders";
    private static final PersistenceProviderManager DEFAULT =
        new PersistenceProviderManager();
    private Lookup.Result<PersistenceProvider> lookupResult;

    private PersistenceProviderManager() {
        lookupResult = Lookups.forPath(PROVIDERS_PATH).lookupResult(
            PersistenceProvider.class);
    }

    public static PersistenceProviderManager getDefault() {
        return DEFAULT;
    }

    public PersistenceProvider[] getProviders() {
        Collection<? extends PersistenceProvider> providers =
            lookupResult.allInstances();
        return providers.toArray(new PersistenceProvider[providers.size()]);
    }
}
