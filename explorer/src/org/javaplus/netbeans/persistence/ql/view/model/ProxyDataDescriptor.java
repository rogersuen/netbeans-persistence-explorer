/*
 * @(#)ProxyDataDescriptor.java   10/05/26
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
public class ProxyDataDescriptor implements DataDescriptor {
    private final DataDescriptor dataDescriptor;

    protected ProxyDataDescriptor(DataDescriptor dataDescriptor) {
        if (dataDescriptor == null) {
            throw new NullPointerException("null data descriptor");
        }

        this.dataDescriptor = dataDescriptor;
    }

    public final DataDescriptor getDataDescriptor() {
        return dataDescriptor;
    }

    public Object getData() {
        return dataDescriptor.getData();
    }

    public Class getDataType() {
        return dataDescriptor.getDataType();
    }

    @Override
    public String getDisplayName() {
        return dataDescriptor.getDisplayName();
    }

    @Override
    public String getDisplayValue() {
        return dataDescriptor.getDisplayValue();
    }

    @Override
    public String getDisplayType() {
        return dataDescriptor.getDisplayType();
    }

    @Override
    public String getShortDescription() {
        return dataDescriptor.getShortDescription();
    }

    @Override
    public Icon getIcon() {
        return dataDescriptor.getIcon();
    }

    @Override
    public int getChildCount() {
        return dataDescriptor.getChildCount();
    }

    @Override
    public DataDescriptor getChild(int index) {
        return dataDescriptor.getChild(index);
    }

    @Override
    public int getIndexOfChild(DataDescriptor child) {
        return dataDescriptor.getIndexOfChild(child);
    }
}
