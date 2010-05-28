/*
 * @(#)DataDescriptorBase.java   10/05/26
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

import javax.swing.Icon;

/**
 *
 * @author Roger Suen
 */
public abstract class DataDescriptorBase implements TypedDataDescriptor {
    protected final Object data;
    protected final Class declaredType;
    protected final DataDescriptorProvider provider;

    protected DataDescriptorBase(Object data, Class dataType,
                                 DataDescriptorProvider provider) {
        if (provider == null) {
            throw new NullPointerException("null provider");
        }

        if (dataType == null) {
            if (data == null) {
                throw new IllegalArgumentException(
                    "Must specify the data type if the data value is null");
            } else {
                dataType = data.getClass();
            }
        }

        this.data = data;
        this.declaredType = dataType;
        this.provider = provider;
    }

    public final Object getData() {
        return data;
    }

    public final Class getDataType() {
        return declaredType;
    }

    public String getDisplayName() {
        return "this";
    }

    public String getDisplayType() {
        return getDataType().getName();
    }

    public String getDisplayValue() {
        return String.valueOf(getData());
    }

    public String getShortDescription() {
        return null;
    }

    public Icon getIcon() {
        return null;
    }

    public DataDescriptor getChild(int index) {
        return null;
    }

    public int getChildCount() {
        return 0;
    }

    public int getIndexOfChild(DataDescriptor child) {
        return -1;
    }
}
