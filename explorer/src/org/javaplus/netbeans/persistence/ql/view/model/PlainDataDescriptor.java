/*
 * @(#)PlainDataDescriptor.java   10/05/28
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

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 *
 * @author Roger Suen
 */
public class PlainDataDescriptor extends DataDescriptorBase {
    private static final String ICON_BASE_POJO =
        "org/javaplus/netbeans/persistence/ql/view/resources/pojo.gif";
    private static final String ICON_BASE_PRIMITIVE =
        "org/javaplus/netbeans/persistence/ql/view/resources/primitive.gif";
    private static final Icon ICON_POJO =
        ImageUtilities.loadImageIcon(ICON_BASE_POJO, true);
    private static final Icon ICON_PRIMITIVE =
        ImageUtilities.loadImageIcon(ICON_BASE_PRIMITIVE, true);

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

    private PlainDataDescriptor(DataDescriptor parent, Object data, Class type,
                                DataDescriptorBuilder provider) {
        super(parent, data, type, provider);
    }

    public static PlainDataDescriptor createPlainDataDescriptor(
            DataDescriptor parent, Object data, Class declaredType,
            DataDescriptorBuilder provider) {
        return new PlainDataDescriptor(parent, data, declaredType, provider);
    }

    /**
     * <p>
     * Checks for type compatibility that the actual type of the specified
     * data is compatible to the specified declared type.</p>
     * <p>
     * This method throws <tt>IllegalArgumentException</tt> if:</p>
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
     * @param data the data to check.
     * @param declaredType the declared type to check against.
     * @throws IllegalArgumentException if both <tt>data</tt> and
     *                                  <tt>declaredType</tt> are <tt>null</tt>,
     *                                  or the actual type of the specified
     *                                  data is not compatible to the specified
     *                                  declared type.
     */
    static void checkTypeCompatibility(Object data, Class<?> declaredType) {
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
                    "The actual type of the specified data is "
                    + actualType.getName() + " ,but the declared type is "
                    + declaredType.getName() + ".");
            }
        }
    }

    @Override
    public Icon getIcon() {
        if (getDeclaredType().isPrimitive()) {
            return ICON_PRIMITIVE;
        } else {
            return ICON_POJO;
        }
    }
}
