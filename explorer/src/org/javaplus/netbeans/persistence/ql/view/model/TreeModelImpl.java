/*
 * @(#)TreeModelImpl.java   10/05/26
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

package org.javaplus.netbeans.persistence.ql.view.model;

import javax.persistence.EntityManager;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Roger Suen
 */
public class TreeModelImpl implements TreeModel {
    private final DataDescriptor root;

    public TreeModelImpl(Object root, EntityManager entityManager) {
        if (root == null) {
            throw new NullPointerException("null root data");
        } else if (entityManager == null) {
            throw new NullPointerException("null entity manager");
        }

        DataDescriptorProvider ddp =
            DataDescriptorProvider.getInstance(entityManager);
        this.root = ddp.createDataDescriptor(root, root.getClass());
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int index) {
        return ((DataDescriptor) parent).getChild(index);
    }

    public int getChildCount(Object parent) {
        return ((DataDescriptor) parent).getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return ((DataDescriptor) parent).getIndexOfChild(
            (DataDescriptor) child);
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {}

    public void addTreeModelListener(TreeModelListener l) {}

    public void removeTreeModelListener(TreeModelListener l) {}
}
