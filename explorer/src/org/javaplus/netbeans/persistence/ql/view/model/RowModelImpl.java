/*
 * @(#)RowModelImpl.java   10/05/21
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

import org.netbeans.swing.outline.RowModel;

/**
 *
 * @author Roger Suen
 */
public class RowModelImpl implements RowModel {
    private static final String[] columnNames = { "Java Type", "Value" };
    private static final Class[] columnClasses = { String.class, String.class };

    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public Class getColumnClass(int column) {
        return columnClasses[column];
    }

    public Object getValueFor(Object node, int column) {
        DataDescriptor descriptor = (DataDescriptor) node;
        switch (column) {
        case 0 :
            return descriptor.getDeclaredType().getName();

        case 1 :
            return descriptor.getDisplayValue();

        default :
            assert false;
        }

        return null;
    }

    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    public void setValueFor(Object node, int column, Object value) {}
}
