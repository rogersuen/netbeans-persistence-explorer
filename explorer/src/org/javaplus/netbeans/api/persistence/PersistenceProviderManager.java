/*
 * @(#)PersistenceProviderManager.java   10/06/02
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
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;

import java.io.IOException;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The basic service for managing the registry of persistence providers in
 * the Persistence Explorer.
 *
 * @author Roger Suen
 */
public final class PersistenceProviderManager {

    /**
     * The constant holds the name of layer folder in which all
     * persistence providers are registered.
     */
    public static final String LAYER_FOLDER = "Persistence/Providers";

    /**
     * The singleton default instance.
     */
    private static final PersistenceProviderManager DEFAULT =
        new PersistenceProviderManager();

    /**
     * The singleton instance of logger.
     */
    private static final Logger logger =
        Logger.getLogger(PersistenceProviderManager.class.getName());

    /**
     * The lookup result of <tt>PersistenceProvider</tt> instances.
     */
    private final Lookup.Result<PersistenceProvider> lookupResult;

    /**
     * The thread-safe set of listeners of the persistence provider registry.
     */
    private final Set<PersistenceProviderRegistryListener> registryListeners =
        new CopyOnWriteArraySet<PersistenceProviderRegistryListener>();

    /**
     * The singleton instance of registry change registryChangeEvent.
     */
    private final PersistenceProviderRegistryEvent registryChangeEvent =
        new PersistenceProviderRegistryEvent(this);

    /**
     * Constructor.
     */
    private PersistenceProviderManager() {
        lookupResult = Lookups.forPath(LAYER_FOLDER).lookupResult(
            PersistenceProvider.class);
        lookupResult.addLookupListener(new LookupListener() {
            public void resultChanged(LookupEvent ev) {
                fireChangeEvent();
            }
        });
    }

    /**
     * Returns the default instance of the <tt>PersistenceProviderManager</tt>.
     * @return the default instance
     */
    public static PersistenceProviderManager getDefault() {
        return DEFAULT;
    }

    /**
     * Returns all registered persistence providers as an array.
     * @return a non-null array of <tt>PersistenceProvider</tt> instances.
     */
    public PersistenceProvider[] getProviders() {
        Collection<? extends PersistenceProvider> providers =
            lookupResult.allInstances();
        return providers.toArray(new PersistenceProvider[providers.size()]);
    }

    /**
     * Adds the specified persistence provider to the registry.
     *
     * @param p the persistence provider to add, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>p</tt> is <tt>null</tt>
     * @throws PersistenceProviderException if unexpected error occurs
     */
    public void addProvider(PersistenceProvider p)
            throws PersistenceProviderException {
        if (p == null) {
            throw new NullPointerException("null persistence provider");
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Adding persistence provider: {0}", p);
        }

        // TODO: check duplicated name and display name
        try {
            PersistenceProviderConverter.writeToFileObject(p);
        } catch (IOException ex) {
            logger.log(Level.WARNING,
                       "Failed to write the peristence provider to "
                       + "the system filesystem: provider = [" + p
                       + "] message = " + ex.getMessage(), ex);
            throw new PersistenceProviderException(
                "Failed to write the persistence provider to the system "
                + "filesystem: " + ex.getMessage(), ex);
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "Successfully added persistence provider: {0}",
                       p.getName());
        }
    }

    /**
     * Removes the specified persistence provider from the registry.
     *
     * @param p the persistence provider to remove, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>p</tt> is <tt>null</tt>
     * @throws PersistenceProviderException if unexpected error occurs
     */
    public void removeProvider(PersistenceProvider p)
            throws PersistenceProviderException {
        if (p == null) {
            throw new NullPointerException("null persistence provider");
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Removing persistence provider: " + p);
        }

        try {
            PersistenceProviderConverter.remove(p);
        } catch (IOException ex) {
            logger.log(Level.WARNING,
                       "Failed to remove the peristence provider: " + p, ex);
            throw new PersistenceProviderException(
                "Failed to remove the persistence provider: " + p + ": "
                + ex.getMessage(), ex);
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                       "Successfully removed persistence provider: "
                       + p.getName());
        }
    }

    /**
     * Add a listener that is notified each time the persistence provider
     * registry changes.
     * @param l the listener to add, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>l</tt> is <tt>null</tt>
     */
    public void addRegistryListener(PersistenceProviderRegistryListener l) {
        if (l == null) {
            throw new NullPointerException("null listener");
        }

        registryListeners.add(l);
    }

    /**
     * Remove a listener that is notified each time the persistence provider
     * registry changes.
     * @param l the listener to remove, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>l</tt> is <tt>null</tt>
     */
    public void removeRegistryListener(PersistenceProviderRegistryListener l) {
        if (l == null) {
            throw new NullPointerException("null listener");
        }

        registryListeners.remove(l);
    }

    private void fireChangeEvent() {
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("The persistence provider registry changed, "
                         + "notifying all registered listeners");
        }

        for (PersistenceProviderRegistryListener l : registryListeners) {
            l.registryChanged(registryChangeEvent);
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(
                Level.FINER,
                "The persistence provider registry changed, "
                + "{0} listener(s) were notified.", registryListeners.size());
        }
    }
}
