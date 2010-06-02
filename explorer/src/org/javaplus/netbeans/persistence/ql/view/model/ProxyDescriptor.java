/*
 * @(#)ProxyDescriptor.java   10/05/31
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
 * <p>
 * A <tt>ProxyDescriptor</tt> wraps another data descriptor, and delegates all
 * method calls to the corresponding methods of the wrapped one.</p>
 *<p>
 * Subclasses of this class typically override some of the methods of this
 * class to change some behavious of the wrapped data descriptor.</p>
 * 
 * @author Roger Suen
 */
public class ProxyDescriptor implements DataDescriptor {
    private final DataDescriptor dataDescriptor;

    /**
     * Constructs a new instance of <tt>ProxyDescriptor</tt> for the specified
     * data descriptor.
     * @param wrapped the data descriptor to proxy, cannot be <tt>null</tt>.
     * @throws NullPointerException if <tt>wrapped</tt> is <tt>null</tt>.
     */
    protected ProxyDescriptor(DataDescriptor wrapped) {
        if (wrapped == null) {
            throw new NullPointerException("null wrapped");
        }

        this.dataDescriptor = wrapped;
    }

    /**
     * Returns the wrapped data descriptor.
     * @return the wrapped data descriptor.
     */
    public final DataDescriptor getWrapped() {
        return dataDescriptor;
    }

    public DataDescriptor getParent() {
        return dataDescriptor.getParent();
    }

    public Object getData() {
        return dataDescriptor.getData();
    }

    public Class getDeclaredType() {
        return dataDescriptor.getDeclaredType();
    }

    public String getDisplayName() {
        return dataDescriptor.getDisplayName();
    }

    public String getDisplayValue() {
        return dataDescriptor.getDisplayValue();
    }

    public String getShortDescription() {
        return dataDescriptor.getShortDescription();
    }

    public Icon getIcon() {
        return dataDescriptor.getIcon();
    }

    public int getChildCount() {
        return dataDescriptor.getChildCount();
    }

    public DataDescriptor getChild(int index) {
        return dataDescriptor.getChild(index);
    }

    public int getIndexOfChild(DataDescriptor child) {
        return dataDescriptor.getIndexOfChild(child);
    }
}
