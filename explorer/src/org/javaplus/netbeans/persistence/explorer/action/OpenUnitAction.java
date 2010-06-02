package org.javaplus.netbeans.persistence.explorer.action;

import org.javaplus.netbeans.persistence.action.ActionBase;
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
public class OpenUnitAction extends ActionBase
        implements LookupListener, ContextAwareAction {

    private static final String KEY_NAME = "OpenUnitAction.NAME";
    private static final Logger logger = Logger.getLogger(OpenUnitAction.class.getName());
    private final Lookup context;
    private Lookup.Result<PersistenceUnit> lookupResult;

    public OpenUnitAction() {
        this(Utilities.actionsGlobalContext());
    }

    private OpenUnitAction(Lookup context) {
        if (context == null) {
            throw new NullPointerException("null context");
        }
        this.context = context;
        putValue(NAME, NbBundle.getMessage(OpenUnitAction.class, KEY_NAME));
    }

    public Action createContextAwareInstance(Lookup actionContext) {
        if (logger.isLoggable(Level.FINER)) {
            Action action = new OpenUnitAction(context);
            logger.log(Level.FINER,
                    "Context aware action instance created: {0}",
                    action);
            return action;
        } else {
            return new OpenUnitAction(context);
        }
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

    public void resultChanged(LookupEvent ev) {
        // Determines the enable state of this action.
        // Enabled only when a single peristence unit node is selected,
        // and the corresponding persistence unit has not an open connection
        // on it.
        boolean isEnabled = false;
        Collection<? extends PersistenceUnit> units =
                lookupResult.allInstances();
        if (units.size() == 1) {
            PersistenceUnit unit = units.iterator().next();
            if (ConnectionManager.getDefault().getConnection(unit) == null) {
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

    public void actionPerformed(ActionEvent e) {
        init();
        Collection<? extends PersistenceUnit> units =
                lookupResult.allInstances();
        if (units.size() != 1) {
            throw new IllegalStateException(
                    "One and only one unit node must be selected "
                    + "to perform open action.");
        }

        PersistenceUnit unit = units.iterator().next();
        openConnection(unit);
    }

    private void openConnection(PersistenceUnit unit) {
        try {
            ConnectionManager.getDefault().openConnection(unit);
        } catch (ConnectionException ex) {
            // TODO: handle exception
            Exceptions.printStackTrace(ex);
        }
    }
}
