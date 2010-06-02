/*
 * @(#)DataDescriptorBase.java   10/05/31
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

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 * This is the super class for all data descriptor classes except
 * the {@link ProxyDescriptor}.
 * @author Roger Suen
 */
public abstract class DataDescriptorBase implements TypedDataDescriptor {

    /**
     * The parent descriptor of this descriptor.
     */
    protected final DataDescriptor parent;

    /**
     * The data to describe.
     */
    protected final Object data;

    /**
     * The declared java type of the data.
     */
    protected final Class declaredType;

    /**
     * The builder used to create this descriptor.
     */
    protected final DataDescriptorBuilder builder;

    /**
     * A map which maps the classes of primitive types to thier corresponding
     * classes of wrapper types. Void is not used because we are working on
     * data.
     */
    private static final Map<Class, Class> primitive2Wrapper;

    static {
        primitive2Wrapper = new HashMap<Class, Class>(8);
        primitive2Wrapper.put(Boolean.TYPE, Boolean.class);
        primitive2Wrapper.put(Byte.TYPE, Byte.class);
        primitive2Wrapper.put(Character.TYPE, Character.class);
        primitive2Wrapper.put(Integer.TYPE, Integer.class);
        primitive2Wrapper.put(Long.TYPE, Long.class);
        primitive2Wrapper.put(Float.TYPE, Float.class);
        primitive2Wrapper.put(Double.TYPE, Double.class);

        //primitive2Wrapper.put(Void.TYPE, Void.class);
    }

    /**
     * <p>
     * Constructs a new instance of <tt>DataDescriptor</tt>.</p>
     * <p>
     * A parent descriptor can be specified when creating the new descriptor.
     * If the specified parent is <tt>null</tt>, means the descriptor is
     * the "root" descriptor.</p>
     * <p>
     * The specified data could be <tt>null</tt>. In that case, a non-null
     * desclared java type must be specified to tell the data type. This method
     * throws <tt>IllegalArgumentException</tt> if: </p>
     * <ul>
     * <li>
     *  both <tt>data</tt> and <tt>declaredType</tt> are <tt>null</tt>, or
     * </li>
     * <li>
     *  <tt>declaredType</tt> is primitive, but the actual type of
     *  <tt>data</tt> is not the corresponding wrapper type, or
     * </li>
     * <li>
     *  <tt>declaredType</tt> is not primitive, and is not assignable from
     *  the actual type of <tt>data</tt>.
     * </li>
     * </ul>
     *
     * @param parent        the parent descriptor of this descriptor.
     * @param data          the data to describe.
     * @param declaredType  the desclared type of the data.
     * @param builder       the builder used to create this descriptor, cannot
     *                      be <tt>null</tt>.
     * @throws NullPointerException if <tt>builder</tt> is <tt>null</tt>.
     * @throws IllegalArgumentException if both <tt>data</tt> and
     *                                  <tt>decalredType</tt> are <tt>null</tt>,
     *                                  or <tt>declaredType</tt> is not
     *                                  assignable from the actual type of
     *                                  <tt>data</tt>.
     */
    protected DataDescriptorBase(DataDescriptor parent, Object data,
                                 Class declaredType,
                                 DataDescriptorBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("null builder");
        }

        checkTypeCompatibility(data, declaredType);
        this.parent = parent;
        this.builder = builder;
        this.data = data;
        if (declaredType != null) {
            this.declaredType = declaredType;
        } else {
            this.declaredType = this.data.getClass();
        }
    }

    /**
     * Checks for type compatibility that the actual type of the specified
     * data is compatible to the specified declared type.
     */
    private static void checkTypeCompatibility(Object data,
            Class<?> declaredType) {
        if ((data == null) && (declaredType == null)) {
            throw new IllegalArgumentException(
                "Must specify the type if the given data value is null");
        }

        if ((data != null) && (declaredType != null)) {
            Class<?> actualType = data.getClass();
            if ((declaredType.isPrimitive()
                    && (primitive2Wrapper.get(declaredType)
                        != actualType)) || (!declaredType.isPrimitive()
                                            &&!declaredType.isAssignableFrom(
                                                actualType))) {
                throw new IllegalArgumentException(
                    "The actual type of the specified data is ["
                    + actualType.getName() + "] ,but the declared type is ["
                    + declaredType.getName() + "].");
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    public DataDescriptor getParent() {
        return parent;
    }

    /**
     * {@inheritDoc }
     */
    public final Object getData() {
        return data;
    }

    /**
     * {@inheritDoc }
     */
    public final Class getDeclaredType() {
        return declaredType;
    }

    /**
     * {@inheritDoc }
     * @return <tt>"this"</tt>.
     */
    public String getDisplayName() {
        return "this";
    }

    /**
     * {@inheritDoc }
     * <p>
     * The default implementation of this method returns the value from
     * <tt>String.valueOf(getData())</tt>.</p>
     * @return {@inheritDoc }
     */
    public String getDisplayValue() {
        return String.valueOf(getData());
    }

    /**
     * {@inheritDoc }
     * @return <tt>null</tt>.
     */
    public String getShortDescription() {
        return null;
    }

    /**
     * {@inheritDoc }
     * @return <tt>null</tt>.
     */
    public Icon getIcon() {
        return null;
    }

    /**
     * {@inheritDoc }
     * @return <tt>null</tt>.
     */
    public DataDescriptor getChild(int index) {
        return null;
    }

    /**
     * {@inheritDoc }
     * @return <tt>0</tt>.
     */
    public int getChildCount() {
        return 0;
    }

    /**
     * {@inheritDoc }
     * @return <tt>-1</tt>.
     */
    public int getIndexOfChild(DataDescriptor child) {
        return -1;
    }
}
