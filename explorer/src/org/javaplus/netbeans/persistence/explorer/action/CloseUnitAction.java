package org.javaplus.netbeans.persistence.explorer.action;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.javaplus.netbeans.api.persistence.PersistenceUnit;
import org.javaplus.netbeans.persistence.connection.Connection;
import org.javaplus.netbeans.persistence.connection.ConnectionException;
import org.javaplus.netbeans.persistence.connection.ConnectionManager;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Roger Suen
 */
public class CloseUnitAction extends ActionBase
        implements LookupListener, ContextAwareAction {

    private static final String KEY_NAME = "CloseUnitAction.NAME";
    private static final Logger logger = Logger.getLogger(CloseUnitAction.class.getName());
    private final Lookup context;
    private Lookup.Result<PersistenceUnit> lookupResult;

    public CloseUnitAction() {
        this(Utilities.actionsGlobalContext());
    }

    private CloseUnitAction(Lookup context) {
        if (context == null) {
            throw new NullPointerException("null context");
        }
        this.context = context;
        putValue(NAME, NbBundle.getMessage(CloseUnitAction.class, KEY_NAME));
    }

    private void init() {
        assert SwingUtilities.isEventDispatchThread() : "this shall be called just from AWT thread";

        if (lookupResult != null) {
            return;
        }

        //The thing we want to listen for the presence or absence of
        //on the global selection
        lookupResult = context.lookupResult(PersistenceUnit.class);
        lookupResult.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    public void actionPerformed(ActionEvent e) {
        init();
        Collection<? extends PersistenceUnit> units =
                lookupResult.allInstances();
        if (units.size() != 1) {
            throw new IllegalStateException(
                    "One and only one unit node must be selected "
                    + "to perform close action.");
        }

        PersistenceUnit unit = units.iterator().next();
        try {
            Connection conn =
                    ConnectionManager.getDefault().getConnection(unit);
            conn.close();
        } catch (ConnectionException ex) {
            // TODO: handle exception
            Exceptions.printStackTrace(ex);
        }
    }

    public void resultChanged(LookupEvent ev) {
        // Determines the enable state of this action.
        // Enabled only when a single peristence unit node is selected,
        // and the corresponding persistence unit has an open connection
        // on it.
        boolean isEnabled = false;
        Collection<? extends PersistenceUnit> units =
                lookupResult.allInstances();
        if (units.size() == 1) {
            PersistenceUnit unit = units.iterator().next();
            Connection conn = ConnectionManager.getDefault().getConnection(unit);
            if (conn != null && conn.isOpen()) {
                isEnabled = true;
            }
        }

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                    "Lookup result changed on the global selection. "
                    + "The enabled state of the action {0} will set to {1}.",
                    new Object[]{this, isEnabled});
        }
        setEnabled(isEnabled);
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        if (logger.isLoggable(Level.FINER)) {
            Action action = new CloseUnitAction(context);
            logger.log(Level.FINER,
                    "Context aware action instance created: {0}",
                    action);
            return action;
        } else {
            return new CloseUnitAction(context);
        }
    }
}
