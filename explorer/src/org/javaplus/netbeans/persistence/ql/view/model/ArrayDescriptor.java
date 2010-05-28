/*
 * @(#)ArrayDescriptor.java   10/05/27
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

import java.lang.reflect.Array;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;

/**
 *
 * @author Roger Suen
 */
public class ArrayDescriptor extends AbstractCollectionDescriptor {
    private static final String ICON_BASE =
        "org/javaplus/netbeans/persistence/ql/view/resources/array.gif";
    private static final Icon ICON = ImageUtilities.loadImageIcon(ICON_BASE,
                                         true);

    public ArrayDescriptor(Object data, Class dataType, Class elementType,
                           DataDescriptorProvider provider) {
        super(data, dataType, elementType, provider);
    }

    public static ArrayDescriptor createArrayDescriptor(Object data,
            Class dataType, Class elementType,
            DataDescriptorProvider provider) {
        if ((dataType != null) &&!dataType.isArray()) {
            throw new IllegalArgumentException(
                "The specified dataType does not represent an array class: "
                + dataType.getName());
        } else if ((data != null) &&!data.getClass().isArray()) {
            throw new IllegalArgumentException(
                "The specified data is not an array: "
                + data.getClass().getName());
        }

        // no further argument check needed here
        // super constructor will ensure type compatibility between
        // the specified data and data type
        return new ArrayDescriptor(data, dataType, elementType, provider);
    }

    @Override
    protected int getElementCount() {
        return Array.getLength(data);
    }

    @Override
    protected Iterator getElementIterator() {
        return new ArrayIterator(data);
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }

    private static final class ArrayIterator implements Iterator {
        private final Object array;
        private final int length;
        private int index = 0;

        public ArrayIterator(Object array) {
            this.array = array;
            this.length = Array.getLength(array);
        }

        public boolean hasNext() {
            return index < length;
        }

        public Object next() {
            return Array.get(array, index++);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
