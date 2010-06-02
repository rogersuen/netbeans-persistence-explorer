/*
 * @(#)CollectionDescriptor.java   10/05/28
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

import org.openide.util.ImageUtilities;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;

/**
 *
 * @author Roger Suen
 */
public class CollectionDescriptor extends AbstractCollectionDescriptor {
    private static final String ICON_BASE =
        "org/javaplus/netbeans/persistence/ql/view/resources/collection.gif";
    private static final Icon ICON = ImageUtilities.loadImageIcon(ICON_BASE,
                                         true);

    public CollectionDescriptor(DataDescriptor parent, Collection data,
                                Class dataType, Class elementType,
                                DataDescriptorBuilder provider) {
        super(parent, data, dataType, elementType, provider);
    }

    public static CollectionDescriptor createCollectionDescriptor(
            DataDescriptor parent, Collection data, Class dataType,
            Class elementType, DataDescriptorBuilder provider) {
        return new CollectionDescriptor(parent, data, dataType, elementType,
                                        provider);
    }

    @Override
    protected int getElementCount() {
        Collection coll = (Collection) data;
        if (coll == null) {
            return 0;
        } else {
            return coll.size();
        }
    }

    @Override
    protected Iterator getElementIterator() {
        Collection coll = (Collection) data;
        if (coll == null) {
            List emptyList = Collections.emptyList();
            return emptyList.iterator();
        } else {
            return coll.iterator();
        }
    }

    @Override
    public String getDisplayValue() {
        return "<" + ((Collection) data).size() + " item(s)>";
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }
}
