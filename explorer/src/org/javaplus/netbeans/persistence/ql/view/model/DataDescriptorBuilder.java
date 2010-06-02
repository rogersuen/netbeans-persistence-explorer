/*
 * @(#)DataDescriptorBuilder.java   10/05/31
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

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;

/**
 *
 * @author Roger Suen
 */
public abstract class DataDescriptorBuilder {
    protected final Object environment;

    /**
     * Sole constructor.
     * @param environment the object encapsulate the environment.
     */
    protected DataDescriptorBuilder(Object environment) {
        this.environment = environment;
    }

    public static DataDescriptorBuilder getInstance(Object environment) {
        return DefaultBuilder.createInstance(environment);
    }

    public Object getEnvironment() {
        return environment;
    }

    /**
     *
     * @param data
     * @param declaredType
     * @return
     */
    public abstract DataDescriptor createDataDescriptor(DataDescriptor parent,
            Object data, Class dataType);

    /**
     * The default provider implementation.
     */
    private static final class DefaultBuilder extends DataDescriptorBuilder {

        /**
         * The singleton logger.
         */
        private static final Logger logger =
            Logger.getLogger(DefaultBuilder.class.getName());

        /**
         * Constructs a new instance of <tt>DefaultBuilder</tt> with the
         * specified env.
         * @param env must be an instance of <tt>EntityManager</tt>
         * @throws NullPointerException if <tt>env</tt> is <tt>null</tt>.
         * @throws IllegalArgumentException if <tt>env</tt> is not an
         *                                  instance of <tt>EntityManager</tt>.
         */
        private DefaultBuilder(EntityManager entityManager) {
            super(entityManager);
        }

        public static DefaultBuilder createInstance(Object environment) {
            if (environment == null) {
                throw new NullPointerException("null environment");
            } else if (!(environment instanceof EntityManager)) {
                throw new IllegalArgumentException(
                    "environment must be an instance of EntityMananger");
            }

            return new DefaultBuilder((EntityManager) environment);
        }

        public EntityManager getEntityManager() {
            return (EntityManager) environment;
        }

        public DataDescriptor createDataDescriptor(DataDescriptor parent,
                Object data, Class type) {

            // check validity of arguments, determine the declaredType
            // if it was not provided
            PlainDataDescriptor.checkTypeCompatibility(data, type);
            if ((type == null) && (data != null)) {
                type = data.getClass();
            }

            // for loop reference, create special loop reference proxy
            if (data != null) {
                DataDescriptor p = parent;
                while (p != null) {
                    if (p.getData() == data) {
                        return new LoopReferenceDataDescriptor(p);
                    }

                    p = p.getParent();
                }
            }

            //
            // create descriptor according to declaredType
            //
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                           "Creating DataDescriptor for the specified data "
                           + "({0}) and the data type ({1}).", new Object[] {
                               data,
                               type });
            }

            ManagedType managedType = null;
            try {
                managedType =
                    getEntityManager().getMetamodel().managedType(type);
            } catch (IllegalArgumentException e) {    // not a managed type
            }

            if (managedType != null) {
                return ManagedTypeDescriptor.createManagedTypeDescriptor(
                    parent, data, managedType, this);
            }

            if (type.isArray()) {
                return ArrayDescriptor.createArrayDescriptor(parent, data,
                        type, null, this);
            } else if (Collection.class.isAssignableFrom(type)) {
                return CollectionDescriptor.createCollectionDescriptor(parent,
                        (Collection) data, type, null, this);
            } else if (Map.class.isAssignableFrom(type)) {
                return MapDescriptor.createMapDescriptor(parent, (Map) data,
                        type, null, null, this);
            }

            return PlainDataDescriptor.createPlainDataDescriptor(parent, data,
                    type, this);
        }
    }
}
