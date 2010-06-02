/*
 * @(#)DataDescriptor.java   10/05/31
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
 * A <tt>DataDescriptor</tt> describes a piece of runtime data, and works as
 * the model used to render the data in a GUI view.
 * @author Roger Suen
 */
public interface DataDescriptor {

    /**
     * Returns the parent data descriptor if any.
     * @return the parent data descriptor, or <tt>null</tt> if there is no
     *         parent.
     */
    DataDescriptor getParent();

    /**
     * Returns the data described by this descriptor.
     * @return the data.
     */
    Object getData();

    /**
     * Returns the declared java type of the data.
     * @return the declared java type, never be <tt>null</tt>.
     */
    Class getDeclaredType();

    /**
     * Returns the display name of this descriptor.
     * @return the display name.
     */
    String getDisplayName();

    /**
     * Returns the short description of this descriptor.
     * @return a short description.
     */
    String getShortDescription();

    /**
     * Returns a string representation of the value of the data.
     * @return a string representation of th e value of the data.
     */
    String getDisplayValue();

    /**
     * Returns the icon of this descriptor.
     * @return an icon.
     */
    Icon getIcon();

    /**
     * Returns the number of child descriptor of this descriptor.
     * @return the number of child descriptor.
     */
    int getChildCount();

    /**
     * Returns the child descriptor at the specified index.
     * @param index the index of the child to return.
     * @return the child descriptor at the specified index, or <tt>null</tt>
     *         if no child descriptor found at the index.
     */
    DataDescriptor getChild(int index);

    /**
     * Returns the index of the specified child descirptor.
     * @param child the child descriptor, cannot be <tt>null</tt>.
     * @return the index of the specified child descirptor, or <tt>null</tt>
     *         if the child not found.
     * @throws NullPointerException if <tt>child</tt> is <tt>null</tt>.
     */
    int getIndexOfChild(DataDescriptor child);
}
