package org.javaplus.netbeans.api.persistence;

import java.util.EventObject;

/**
 * Defines an event that encapsulates changes to the persistence unit registry.
 * @author Roger Suen
 */
public class PersistenceUnitRegistryEvent extends EventObject {

    /**
     * Constructs a new <tt>PersistenceUnitRegistryEvent</tt>.
     * @param source the non-null <tt>PersistenceUnitManager</tt>
     *               that originated the event
     * @see PersistenceUnitManager
     */
    public PersistenceUnitRegistryEvent(Object source) {
        super(source);
    }
}
