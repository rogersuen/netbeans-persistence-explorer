package org.javaplus.netbeans.api.persistence;

import java.util.EventListener;

/**
 * The listener that's notified when the persistence provider registry changes.
 * @author Roger Suen
 */
public interface PersistenceProviderRegistryListener extends EventListener {

    /**
     * Called whenever the persistence provider registry changes.
     * @param event the event object encapsulates the change
     */
    void registryChanged(PersistenceProviderRegistryEvent event);
}
