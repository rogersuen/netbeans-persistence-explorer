/*
 * @(#)AbstractCollectionDescriptor.java   10/05/28
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

import java.util.Iterator;

/**
 *
 * @author Roger Suen
 */
public abstract class AbstractCollectionDescriptor
        extends NonLeafDataDescriptor {
    private final Class elementType;

    protected AbstractCollectionDescriptor(DataDescriptor parent, Object data,
            Class dataType, Class elementType,
            DataDescriptorBuilder provider) {
        super(parent, data, dataType, provider);
        this.elementType = elementType;

        // populate children
        int size = getElementCount();
        if (size > 0) {
            children.ensureCapacity(size);
            int index = 0;
            Iterator iterator = getElementIterator();
            while (iterator.hasNext()) {
                DataDescriptor dataDescriptor =
                    createElementDescriptor(iterator.next());
                children.add(new ElementDescriptor(dataDescriptor, index++));
            }
        }
    }

    protected abstract int getElementCount();

    protected abstract Iterator getElementIterator();

    protected DataDescriptor createElementDescriptor(Object element) {
        DataDescriptor wrapped = builder.createDataDescriptor(this, element,
                                     elementType);
        return wrapped;
    }

    @Override
    public String getDisplayValue() {
        return "<" + getElementCount() + " item(s)>";
    }

    private class ElementDescriptor extends ProxyDescriptor {
        private final int index;

        private ElementDescriptor(DataDescriptor dataDescriptor, int index) {
            super(dataDescriptor);
            this.index = index;
        }

        @Override
        public String getDisplayName() {
            return "[" + index + "]";
        }
    }
}
