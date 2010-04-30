package org.javaplus.netbeans.api.persistence;

import java.util.EventListener;

/**
 * The listener that's notified when the persistence unit registry changes.
 * @author Roger Suen
 */
public interface PersistenceUnitRegistryListener extends EventListener {

    /**
     * Called whenever the persistence unit registry changes.
     * @param event the event object encapsulates the change
     */
    void registryChanged(PersistenceUnitRegistryEvent event);
}
