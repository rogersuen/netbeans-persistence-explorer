/*
 * @(#)NodeBase.java   10/04/28
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

import javax.swing.event.ChangeEvent;
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
import javax.swing.event.ChangeListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Roger Suen
 */
public abstract class NodeBase extends AbstractNode {

    public static final String LAYER_PATH_BASE = "Persistence/Explorer/Nodes/";
    public static final String FOLDER_CHILDREN = "/Children";
    public static final String FOLDER_ACTIONS = "/Actions";
    /**
     * The singleton logger
     */
    private static final Logger logger =
            Logger.getLogger(NodeBase.class.getName());
    private final String childrenLayerFolder = LAYER_PATH_BASE + getLayerFolder()
            + FOLDER_CHILDREN;
    private final String actionsLayerFolder = LAYER_PATH_BASE
            + getLayerFolder()
            + FOLDER_ACTIONS;
    private final ChildRegistry childRegistry;
    private final ActionRegistry actionRegistry;

    /**
     * 
     */
    protected final NodeLookup lookup;

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
     * 
     * @return
     */
    protected abstract String getLayerFolder();

    public void update() {
        childRegistry.refresh();
        updateProperties();
    }

    protected void updateProperties() {
    }

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

        private final Lookup.Result<Object> childrenLookupResult;
        /**
         * List of Node/NodeProvider instances registered in the XML
         * layer folder
         */
        private final List<Object> children = new CopyOnWriteArrayList<Object>();

        public ChildRegistry() {
            childrenLookupResult = Lookups.forPath(childrenLayerFolder).
                    lookupResult(Object.class);
            childrenLookupResult.addLookupListener(new LookupListener() {

                public void resultChanged(LookupEvent ev) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE,
                                "Lookup.Result(Object.class) change detected "
                                + "in the folder: {0}",
                                childrenLayerFolder);
                    }

                    // reload the registry and then update this node
                    loadRegistry();
                    update(); // NodeBase.update()
                }
            });

            loadRegistry();
        }

        private synchronized void loadRegistry() {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Loading instances of Node/NodeProvider from "
                        + "the folder {0}",
                        childrenLayerFolder);
            }

            // clear, and then iterate to add, so synchronized
            children.clear();

            // load all instances of Node and NodeProvider
            Collection<? extends Object> objects =
                    childrenLookupResult.allInstances();
            for (Object object : objects) {
                if (object instanceof Node) {
                    children.add(object);
                } else if (object instanceof NodeProvider) {
                    // listens to the provider for the change event
                    // update the node on changes
                    NodeProvider np = (NodeProvider) object;
                    np.addChangeListener(new ChangeListener() {

                        public void stateChanged(ChangeEvent e) {
                            update(); // NodeBase.update()
                        }
                    });
                    children.add(np);
                } else if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                            "Node/NodeProvider instance expected in the "
                            + "folder {0}, but {1} found there", new Object[]{
                                childrenLayerFolder,
                                object});
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "{0} instance(s) of Node/NodeProvider loaded "
                        + "from the folder {1}", new Object[]{
                            children.size(),
                            childrenLayerFolder});
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
                return new Node[]{(Node) key};
            } else {
                return null; // should never happen
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

    /**
     * 
     */
    public static final class NodeLookup extends ProxyLookup {

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
