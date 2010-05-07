package org.javaplus.netbeans.api.persistence.explorer.node;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;

/**
 * TODO: javadoc
 * @author Roger Suen
 */
public abstract class NodeProviderBase implements NodeProvider {

    /**
     * Singleton logger instance for this class.
     */
    private static final Logger logger =
            Logger.getLogger(NodeProviderBase.class.getName());
    /**
     * All <tt>ChangeListener</tt>s registered.
     * We don't use <tt>org.openide.util.ChangeSupport</tt> because in our
     * case, duplication is not allowed, and <tt>NullPointerException</tt>
     * will throw on <tt>null</tt> listeners to ease bug tracing.
     */
    private final Set<ChangeListener> changeListeners =
            new CopyOnWriteArraySet<ChangeListener>();
    /**
     * Singleton <tt>ChangeEvent</tt> instance for simple use cases.
     * @see #fireChangeEvent()
     */
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    /**
     * The lookup object passed from the node as the parent of this
     * node provider.
     */
    protected final Lookup lookup;

    /**
     * Constructs a new instance of <tt>NodeProviderBase</tt> with
     * the specified lookup passed from the parent node.
     * 
     * @param lookup the lookup object passed from the parent node. cannot be
     *               <tt>null</tt>.
     * @throws NullPointerException if <tt>lookup</tt> is <tt>null</tt>.
     */
    protected NodeProviderBase(Lookup lookup) {
        if (lookup == null) {
            throw new NullPointerException("null lookup");
        }
        this.lookup = lookup;
    }

    /**
     * TODO: javadoc
     */
    protected final void fireChangeEvent() {
        fireChangeEvent(changeEvent);
    }

    /**
     * TODO: javadoc
     * @param event
     */
    protected final void fireChangeEvent(ChangeEvent event) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                    "NodeProvider {0} is notifying change event to listeners",
                    this);
        }

        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                    "NodeProvider {0} notified change event to {1} listeners",
                    new Object[]{this, changeListeners.size()});
        }
    }

    /**
     * Adds the specified <tt>listener</tt> if it was not added before.
     * @param listener the listener to add, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>listener</tt> is <tt>null</tt>
     */
    public void addChangeListener(ChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("null listener");
        }

        if (logger.isLoggable(Level.WARNING) && changeListeners.contains(listener)) {
            logger.log(Level.WARNING,
                    "Attempting to add duplicated change listener {0}. "
                    + "It will be ignored.",
                    listener);
        }

        changeListeners.add(listener);

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                    "ChangeListener {0} has been added to NodeProvider {1}",
                    new Object[]{listener, this});
        }
    }

    /**
     * Removes the specified <tt>listener</tt> if it exists.
     * @param listener the listener to remove, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>listener</tt> is <tt>null</tt>
     */
    public void removeChangeListener(ChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("null listener");
        }

        boolean removed = changeListeners.remove(listener);

        if (!removed && logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING,
                    "Attempting to remove non-existed change listener {0}",
                    listener);
        } else if (logger.isLoggable(Level.FINER) && removed) {
            logger.log(Level.FINER,
                    "ChangeListener {0} has been removed from NodeProvider {1}",
                    new Object[]{listener, this});
        }
    }
}
