/*
 * @(#)MapDescriptor.java   10/05/28
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
import java.util.Map;
import java.util.Map.Entry;

/**
 * A <tt>MapDescriptor</tt> describes one {@link java.util.Map} instance.
 * @author Roger Suen
 */
public class MapDescriptor extends AbstractCollectionDescriptor {
    private final Class declaredKeyType;
    private final Class declaredValueType;

    /**
     * Constructs a new instance of <tt>MapDescriptor</tt>.
     * @param parent            the parent descriptor
     * @param data              a <tt>Map</tt> instance to describe
     * @param declaredType      declared type of the <tt>Map</tt>
     * @param declaredKeyType   declared type of the map keys
     * @param declaredValueType declared type of the map values
     * @param builder          the descriptor builder
     */
    private MapDescriptor(DataDescriptor parent, Map data, Class declaredType,
                          Class declaredKeyType, Class declaredValueType,
                          DataDescriptorBuilder provider) {
        super(parent, data, declaredType, null, provider);
        this.declaredKeyType = declaredKeyType;
        this.declaredValueType = declaredValueType;
    }

    public static MapDescriptor createMapDescriptor(DataDescriptor parent,
            Map data, Class declaredType, Class declaredKeyType,
            Class declaredValueType, DataDescriptorBuilder provider) {
        return new MapDescriptor(parent, data, declaredType, declaredKeyType,
                                 declaredValueType, provider);
    }

    public Class getDeclaredKeyType() {
        return declaredKeyType;
    }

    public Class getDeclaredValueType() {
        return declaredValueType;
    }

    protected int getElementCount() {
        return ((Map) data).size();
    }

    protected Iterator getElementIterator() {
        return ((Map) data).entrySet().iterator();
    }

    @Override
    protected DataDescriptor createElementDescriptor(Object element) {
        return new EntryDescriptor((Entry) element);
    }

    /**
     * A <tt>EntryDescriptor</tt> describes one {@link java.util.Map.Entry}
     * instance.
     */
    private final class EntryDescriptor extends NonLeafDataDescriptor {
        private EntryDescriptor(Entry entry) {
            super(MapDescriptor.this, entry, null, MapDescriptor.this.builder);
            initChildren();
        }

        private void initChildren() {
            DataDescriptor key = builder.createDataDescriptor(this,
                                     ((Entry) data).getKey(), declaredKeyType);
            DataDescriptor value = builder.createDataDescriptor(this,
                                       ((Entry) data).getValue(),
                                       declaredValueType);
            children.add(new EntryKeyDescriptor(key));
            children.add(new EntryValueDescriptor(value));
        }
    }


    private final class EntryKeyDescriptor extends ProxyDescriptor {
        public EntryKeyDescriptor(DataDescriptor dataDescriptor) {
            super(dataDescriptor);
        }

        @Override
        public String getDisplayName() {
            return "key";
        }
    }


    private final class EntryValueDescriptor extends ProxyDescriptor {
        public EntryValueDescriptor(DataDescriptor dataDescriptor) {
            super(dataDescriptor);
        }

        @Override
        public String getDisplayName() {
            return "value";
        }
    }
}
