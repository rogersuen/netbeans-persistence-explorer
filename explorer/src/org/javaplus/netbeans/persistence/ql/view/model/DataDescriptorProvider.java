/*
 * @(#)DataDescriptorProvider.java   10/05/27
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

/**
 *
 * @author Roger Suen
 */
public abstract class DataDescriptorProvider {
    public static DataDescriptorProvider getInstance(Object environment) {
        return new DefaultProvider(environment);
    }

    /**
     *
     * @param data
     * @param declaredType
     * @return
     */
    public abstract DataDescriptor createDataDescriptor(Object data,
            Class dataType);

    /**
     * The default provider implementation.
     */
    private static final class DefaultProvider extends DataDescriptorProvider {

        /**
         * The singleton logger.
         */
        private static final Logger logger =
            Logger.getLogger(DefaultProvider.class.getName());

        /**
         * The entity manager instance provided as env object.
         */
        private final EntityManager entityManager;

        /**
         * Constructs a new instance of <tt>DefaultProvider</tt> with the
         * specified env.
         * @param env must be an instance of <tt>EntityManager</tt>
         * @throws NullPointerException if <tt>env</tt> is <tt>null</tt>.
         * @throws IllegalArgumentException if <tt>env</tt> is not an
         *                                  instance of <tt>EntityManager</tt>.
         */
        private DefaultProvider(Object env) {
            if (env == null) {
                throw new NullPointerException("null environment");
            } else if (!(env instanceof EntityManager)) {
                throw new IllegalArgumentException(
                    "environment must be an instance of EntityMananger");
            }

            this.entityManager = (EntityManager) env;
        }

        public DataDescriptor createDataDescriptor(Object data, Class type) {

            // check validity of arguments, determine the declaredType
            // if it was not provided
            PlainDataDescriptor.checkTypeCompatibility(data, type);
            if ((type == null) && (data != null)) {
                type = data.getClass();
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

            EntityType entityType = null;
            try {
                entityType = entityManager.getMetamodel().entity(type);
            } catch (IllegalArgumentException e) {    // not a entity
            }

            if (entityType != null) {
                return EntityDescriptor.createEntityDescriptor(data,
                        entityType, this);
            }

            if (type.isArray()) {
                return ArrayDescriptor.createArrayDescriptor(data, type, null,
                        this);
            } else if (Collection.class.isAssignableFrom(type)) {
                return CollectionDescriptor.createCollectionDescriptor(
                    (Collection) data, type, null, this);
            }

            return PlainDataDescriptor.createPlainDataDescriptor(data, type,
                    this);
        }
    }
}
