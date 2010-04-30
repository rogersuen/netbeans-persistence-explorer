package org.javaplus.netbeans.api.persistence;

/**
 *
 * @author Roger Suen
 */
public class PersistenceUnitManagerException extends Exception {

    /**
     * Creates a new instance of <code>PersistenceUnitManagerException</code>
     * without detail message.
     */
    public PersistenceUnitManagerException() {
    }


    /**
     * Constructs an instance of <code>PersistenceUnitManagerException</code>
     * with the specified detail message.
     * @param message the detail message.
     */
    public PersistenceUnitManagerException(String message) {
        super(message);
    }

    public PersistenceUnitManagerException(Throwable cause) {
        super(cause);
    }

    public PersistenceUnitManagerException(String message, Throwable cause) {
        super(message, cause);
    }


}
