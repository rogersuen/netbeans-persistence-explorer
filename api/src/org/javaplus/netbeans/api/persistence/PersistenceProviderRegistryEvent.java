package org.javaplus.netbeans.api.persistence;

import java.util.EventObject;

/**
 * Defines an event that encapsulates changes to the persistence provider
 * registry.
 * @author Roger Suen
 */
public class PersistenceProviderRegistryEvent extends EventObject {

    /**
     * Constructs a new <tt>PersistenceProviderRegistryEvent</tt>.
     * @param source the non-null <tt>PersistenceProviderManager</tt>
     *               that originated the event
     * @see PersistenceProviderManager
     */
    public PersistenceProviderRegistryEvent(Object source) {
        super(source);
    }
}
