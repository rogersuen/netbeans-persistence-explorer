/*
 * @(#)NodeBase.java   10/06/07
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

package org.javaplus.netbeans.api.persistence.explorer.node;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <p>
 * The base class for all Persistence Explorer nodes.</p>
 * <p>
 * All persistence explorer nodes are registered in the system filesystem
 * via layer files. The parent folder (the base node folder) is defined by
 * the constant {@link #LAYER_FOLDER_BASE}. Each node should be registered
 * in a child folder under it with thier unique name returned from the
 * {@link #getLayerFolder() } method. </p>
 * <p>
 * For a specific node, its child node instances or provider factory instance
 * and actions are registered in the subfolders with the names defined by
 * {@link #LAYER_FOLDER_CHILDREN} and {@link #LAYER_FOLDER_ACTIONS}
 * respectively, under the node's folder. These child nodes are then
 * registered via their own folders under the base node folder.</p>
 *
 * @author Roger Suen
 */
public abstract class NodeBase extends AbstractNode {

    /**
     * The constant holding the name of the base folder in the system
     * filesystem in which all persistence explorer nodes are registered,
     * <tt>{@value #LAYER_FOLDER_BASE}</tt>.
     * @see #getLayerFolder()
     */
    public static final String LAYER_FOLDER_BASE =
        "Persistence/Explorer/Nodes/";

    /**
     * The constant holding the name of the subfolder, in which child nodes
     * of a node are registered, <tt>{@value #LAYER_FOLDER_CHILDREN}</tt>.
     * @see #getLayerFolder()
     */
    public static final String LAYER_FOLDER_CHILDREN = "/Children";

    /**
     * The constant holding the name of the subfolder, in which actions
     * of a node are registered, <tt>{@value #LAYER_FOLDER_ACTIONS}</tt>.
     * @see #getLayerFolder()
     */
    public static final String LAYER_FOLDER_ACTIONS = "/Actions";

    //
    // Private constants
    //
    private static final Logger logger =
        Logger.getLogger(NodeBase.class.getName());

    /**
     * The proxy lookup instance of this node.
     */
    protected final NodeLookup lookup;

    //
    // Private fields
    //
    private final ChildRegistry childRegistry;
    private final ActionRegistry actionRegistry;

    /**
     * Sole constructor.
     */
    protected NodeBase() {
        this(new NodeLookup());
    }

    /**
     * Uses protected/private pair of constructors so that we can initialize
     * lookup after construction.
     */
    private NodeBase(NodeLookup nodeLookup) {
        super(Children.LEAF, nodeLookup);
        lookup = nodeLookup;
        childRegistry = new ChildRegistry();
        actionRegistry = new ActionRegistry();
        setChildren(Children.create(childRegistry, true));
    }

    /**
     * Called when the child registry changed, or any child provider changed.
     */
    private void update() {
        childRegistry.refresh();
        updateProperties();
    }

    /**
     * <p>
     * Returns the name of the folder, in which this node is registered. </p>
     * <p>
     * All persistence explorer nodes are registered in the system filesystem
     * via layer files. The parent folder is defined by the constant
     * {@link #LAYER_FOLDER_BASE}. Each node should be registered in a child
     * folder under it with thier unique name returned from this method.</p>
     *
     * @return the relative layer folder name, without any leading or trailing
     *         '/' separator.
     */
    protected abstract String getLayerFolder();

    /**
     * This method is called whenever the child nodes are refreshed due to
     * the registry change.
     */
    protected void updateProperties() {}

    @Override
    public Action[] getActions(boolean context) {
        Action[] actions = actionRegistry.getActions();
        if (!context) {
            return actions;
        }

        // context actions
        // insert our actions before other actions returned from super
        Action[] contextActions = super.getActions(context);
        List<Action> list = new ArrayList<Action>(actions.length
                                + contextActions.length);
        Collections.addAll(list, actions);
        Collections.addAll(list, contextActions);
        return list.toArray(new Action[list.size()]);
    }

    /**
     * All persistence explorer nodes do not permit copying.
     * @return <tt>false</tt>
     */
    @Override
    public boolean canCopy() {
        return false;
    }

    /**
     * All persistence explorer nodes do not permit cutting.
     * @return <tt>false</tt>
     */
    @Override
    public boolean canCut() {
        return false;
    }

    /**
     * All persistence explorer nodes do not permit renaming.
     * @return <tt>false</tt>
     */
    @Override
    public boolean canRename() {
        return false;
    }

    /**
     * <p>
     * Factory used to create child nodes. This class manages a list of
     * instance of child nodes and child nodes providers registered
     * in the corresponding filesystem folder. The keys used to create
     * child nodes are nodes (registered directly or returned from registered
     * node providers) themselves.</p>
     * <p>
     * The following two sources of changes cause the node update itself:</p>
     * <ul>
     *  <li>Node/NodeProvider registry changes</li>
     *  <li>Any NodeProvider changes</li>
     * </ul>
     *
     *
     * @see #getLayerFolder()
     * @see #update()
     */
    private final class ChildRegistry extends ChildFactory<Object> {
        private final String childrenFolder = LAYER_FOLDER_BASE
                                              + getLayerFolder()
                                              + LAYER_FOLDER_CHILDREN;
        private final Lookup.Result<Object> childrenLookupResult;
        private final LookupListener lookupListener;

        /**
         * List of Node/NodeProviderFactory instances registered in the XML
         * layer folder.
         */
        private final List<Object> children =
            new CopyOnWriteArrayList<Object>();

        private ChildRegistry() {
            childrenLookupResult =
                Lookups.forPath(childrenFolder).lookupResult(Object.class);
            lookupListener = new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.log(
                            Level.FINER,
                            "Lookup.Result(Object.class) change detected "
                            + "in the folder: [{0}]", childrenFolder);
                    }

                    // reload the registry
                    loadRegistry();

                    // update this node
                    update();
                }
            };
            childrenLookupResult.addLookupListener(lookupListener);

            // constructing, need not update
            loadRegistry();
        }

        private synchronized void loadRegistry() {
            if (logger.isLoggable(Level.FINER)) {
                logger.log(
                    Level.FINER,
                    "Loading Node/NodeProviderFactory from the folder [{0}]",
                    childrenFolder);
            }

            // clear, and then iterate to add, so synchronized
            children.clear();

            // load all instances of Node and NodeProviderFactory
            Collection<? extends Object> objects =
                childrenLookupResult.allInstances();
            for (Object object : objects) {
                if (object instanceof Node) {
                    children.add(object);
                } else if (object instanceof NodeProviderFactory) {
                    NodeProviderFactory factory = (NodeProviderFactory) object;
                    NodeProvider provider = factory.createNodeProvider(lookup);

                    // listens to the provider for the change event
                    // update the node on changes
                    provider.addChangeListener(new ChangeListener() {
                        public void stateChanged(ChangeEvent e) {
                            update();    // NodeBase.update()
                        }
                    });
                    children.add(provider);
                } else if (logger.isLoggable(Level.WARNING)) {
                    logger.log(
                        Level.WARNING,
                        "Node/NodeProviderFactory instances expected in "
                        + "the folder [{0}], but an instance [{1}] "
                        + "found there.", new Object[] { childrenFolder,
                            object });
                }
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER,
                           "{0} Node/NodeProviderFactory instance(s) loaded "
                           + "from the folder [{1}]", new Object[] {
                               children.size(),
                               childrenFolder });
            }
        }

        /**
         * Called only by NodeBase.update().
         * Always asynchronized
         */
        private void refresh() {
            refresh(false);
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            for (Object object : children) {
                if (object instanceof Node) {
                    toPopulate.add(object);
                } else if (object instanceof NodeProvider) {
                    toPopulate.addAll(((NodeProvider) object).getNodes());
                }
            }

            return true;
        }

        @Override
        protected Node[] createNodesForKey(Object key) {
            if (key instanceof Node) {
                return new Node[] { (Node) key };
            } else {
                return null;    // should never happen
            }
        }
    }


    /**
     * Helper used to access actions registered in corresponding layer folder
     * of the node.
     */
    private final class ActionRegistry {
        private final String actionsFolder = LAYER_FOLDER_BASE
                                             + getLayerFolder()
                                             + LAYER_FOLDER_ACTIONS;
        private final Lookup.Result<Object> lookupResult;
        private final LookupListener lookupListener;
        private final List<Action> actions = new CopyOnWriteArrayList<Action>();

        /**
         * Constructor
         */
        public ActionRegistry() {
            lookupResult =
                Lookups.forPath(actionsFolder).lookupResult(Object.class);
            lookupListener = new LookupListener() {
                public void resultChanged(LookupEvent ev) {
                    loadActions();
                }
            };
            lookupResult.addLookupListener(lookupListener);
            loadActions();
        }

        /**
         * Returns all actions registered in layer folder as an array.
         * @return an array of action
         */
        public Action[] getActions() {
            return actions.toArray(new Action[actions.size()]);
        }

        /**
         * Clear the internal action list, and then reload it from the layer
         */
        private synchronized void loadActions() {
            actions.clear();
            Collection<? extends Object> objects = lookupResult.allInstances();
            for (Object object : objects) {
                if (object instanceof Action) {
                    actions.add((Action) object);
                } else if (object instanceof javax.swing.JSeparator) {
                    actions.add(null);
                } else if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Action/JSeparator instance expected in the layer "
                            + "folder {0}, but {1} found there", new Object[] {
                                actionsFolder,
                                object });
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                           "{0} instance(s) of Action/JSeparator loaded "
                           + "from layer folder {1}", new Object[] {
                               actions.size(),
                               actionsFolder });
            }
        }
    }


    /**
     * A proxy lookup implementation used by persistence explorer nodes.
     */
    protected static final class NodeLookup extends ProxyLookup {
        private final InstanceContent instanceContent = new InstanceContent();

        public NodeLookup() {
            setLookups(new AbstractLookup(instanceContent));
        }

        public InstanceContent getInstanceContent() {
            return instanceContent;
        }

        public void addLookup(Lookup... lookups) {
            Lookup[] originalLookups = getLookups();
            Lookup[] newLookups =
                new Lookup[originalLookups.length + lookups.length];
            System.arraycopy(originalLookups, 0, newLookups, 0,
                             originalLookups.length);
            System.arraycopy(lookups, 0, newLookups, originalLookups.length,
                             lookups.length);
            setLookups(newLookups);
        }
    }
}
