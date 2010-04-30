/*
 * @(#)PersistenceExplorerNode.java   10/04/28
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

/**
 *
 * @author Roger Suen
 */
public abstract class PersistenceExplorerNode extends AbstractNode {

    public static final String LAYER_PATH_BASE = "Persistence/Explorer/Nodes/";
    public static final String FOLDER_CHILDREN = "/Children";
    public static final String FOLDER_ACTIONS = "/Actions";
    /**
     * The singleton logger
     */
    private static final Logger logger =
            Logger.getLogger(PersistenceExplorerNode.class.getName());
    private final String childrenLayerFolder = LAYER_PATH_BASE + getLayerFolder()
            + FOLDER_CHILDREN;
    private final String actionsLayerFolder = LAYER_PATH_BASE
            + getLayerFolder()
            + FOLDER_ACTIONS;
    
    private final ChildRegistry childRegistry;
    private final ActionRegistry actionRegistry;


    // TODO: pending removal
    private final NodeLookup lookup;

    /**
     * Sole constructor.
     */
    protected PersistenceExplorerNode() {
        this(new NodeLookup());
    }

    /**
     * Uses protected/private pair of constructors so that we can initialize
     * lookup after construction.
     */
    private PersistenceExplorerNode(NodeLookup nodeLookup) {
        super(Children.LEAF, nodeLookup);

        childRegistry = new ChildRegistry();
        actionRegistry = new ActionRegistry();
        setChildren(Children.create(childRegistry, true));

    // TODO: pending removal
        lookup = nodeLookup;
    }


    /**
     * 
     * @return
     */
    protected abstract String getLayerFolder();

    /**
     * 
     * @param lookup
     */
    @Deprecated
    protected void addLookup(Lookup lookup) {
        this.lookup.addLookup(lookup);
    }

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
     * 
     */
    private final class ChildRegistry extends ChildFactory<Object> {

        private final Lookup.Result<Object> childrenLookupResult;
        private final List<Object> keys = new CopyOnWriteArrayList<Object>();

        public ChildRegistry() {
            childrenLookupResult = Lookups.forPath(childrenLayerFolder).lookupResult(Object.class);
            childrenLookupResult.addLookupListener(new LookupListener() {

                public void resultChanged(LookupEvent ev) {
                    loadKeys();
                }
            });

            loadKeys();
        }

        private void loadKeys() {
            keys.clear();

            // load all instances of Node and NodeProvider
            Collection<? extends Object> objects = childrenLookupResult.allInstances();
            for (Object object : objects) {
                if (object instanceof Node || object instanceof NodeProvider) {
                    keys.add(object);
                } else if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Node/NodeProvider instance expected in the layer "
                            + "folder {0}, but {1} found there", new Object[]{
                                childrenLayerFolder,
                                object});
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "{0} instance(s) of Node/NodeProvider loaded "
                        + "from the layer folder {1}", new Object[]{
                            keys.size(),
                            childrenLayerFolder});
            }
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            toPopulate.addAll(keys);
            return true;
        }

        @Override
        protected Node[] createNodesForKey(Object key) {
            if (key instanceof Node) {
                return new Node[]{(Node) key};
            } else if (key instanceof NodeProvider) {
                return ((NodeProvider) key).getNodes();
            } else {
                return null;
            }
        }
    }

    /**
     * Helper used to access actions registered in corresponding layer folder
     * of the node.
     */
    private final class ActionRegistry {

        private final Lookup.Result<Object> lookupResult;
        private final List<Action> actions = new CopyOnWriteArrayList<Action>();

        /**
         * Constructor
         */
        public ActionRegistry() {
            lookupResult = Lookups.forPath(actionsLayerFolder).lookupResult(Object.class);
            lookupResult.addLookupListener(new LookupListener() {

                public void resultChanged(LookupEvent ev) {
                    loadActions();
                }
            });
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
        private void loadActions() {
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
                            + "folder {0}, but {1} found there", new Object[]{
                                actionsLayerFolder,
                                object});
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "{0} instance(s) of Action/JSeparator loaded "
                        + "from layer folder {1}", new Object[]{
                            actions.size(),
                            actionsLayerFolder});
            }
        }
    }


    // TODO: pending removal
    public static final class NodeLookup extends ProxyLookup {

        public NodeLookup() {
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
