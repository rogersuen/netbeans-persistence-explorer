/*
 * @(#)ManagedTypeDescriptor.java   10/05/31
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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.Type.PersistenceType;

import javax.swing.Icon;

/**
 * A <tt>ManagedTypeDescriptor</tt> describes one object whose class is
 * represented by a metamodel embeddable type, or an entity type.
 * @author Roger Suen
 */
public abstract class ManagedTypeDescriptor extends NonLeafDataDescriptor {

    /**
     * The singleton logger.
     */
    private static final Logger logger =
        Logger.getLogger(ManagedTypeDescriptor.class.getName());

    /**
     * Managed attributeType of the data.
     */
    private final ManagedType managedType;

    /**
     * Sole constrcutor.
     */
    private ManagedTypeDescriptor(DataDescriptor parent, Object data,
                                  ManagedType managedType,
                                  DataDescriptorBuilder builder) {
        super(parent, data, managedType.getJavaType(), builder);
        this.managedType = managedType;
        initChildren();
    }

    /**
     * Creates and returns a new instance of <tt>ManagedTypeDescriptor</tt>
     * for the specified <tt>data</tt> whose java type is represented by the
     * specified metamodel entity or embeddable type.
     *
     * @param parent        the parent descriptor of the newly created
     *                      descriptor.
     * @param data          the data to describe, whose java type must be the
     *                      type that <tt>managedType</tt> represents.
     * @param managedType   the metamodel managed type represents the java type
     *                      of <tt>data</tt>.
     * @param builder       the builder used to build descirptors, cannot be
     *                      <tt>null</tt>.
     * @return the newly created descriptor.
     * @throws IllegalArgumentException if something wrong with arguments.
     */
    public static ManagedTypeDescriptor createManagedTypeDescriptor(
            DataDescriptor parent, Object data, ManagedType managedType,
            DataDescriptorBuilder builder) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                       "Creating managed type descriptor for data [{0}] with "
                       + "the persistence type [{1}]", new Object[] { data,
                    managedType });
        }

        ManagedTypeDescriptor descriptor = null;
        PersistenceType persistenceType = null;

        // validate arguments
        if (data == null) {
            if (managedType == null) {
                throw new IllegalArgumentException(
                    "Must specify the managed type in case the data is null.");
            } else {
                persistenceType = managedType.getPersistenceType();
                if ((PersistenceType.EMBEDDABLE != persistenceType)
                        && (PersistenceType.ENTITY != persistenceType)) {
                    throw new IllegalArgumentException(
                        "Unexpected managed type " + managedType
                        + ". Must be an entity, or embeddale type.");
                }
            }
        } else {    // data != null
            EntityManager em = (EntityManager) builder.getEnvironment();
            Metamodel mm = em.getMetamodel();
            if (managedType != null) {
                if (managedType != mm.managedType(data.getClass())) {
                    throw new IllegalArgumentException(
                        "The specified data [" + data + "] is not of "
                        + "the specified managed type [" + managedType + "]");
                }
            } else {    // managedType == null

                // determine the managed type
                // NOTE:
                // the following call will throw IllegalArgumentException
                // if data is not of managed type
                managedType = mm.managedType(data.getClass());
            }

            persistenceType = managedType.getPersistenceType();
        }

        // create enity or embeddable descriptor
        if (PersistenceType.EMBEDDABLE == persistenceType) {
            descriptor = new EmbeddableDescriptor(parent, data,
                    (EmbeddableType) managedType, builder);
        } else /* if (PersistenceType.ENTITY == persistenceType) */ {
            descriptor = new EntityDescriptor(parent, data,
                                              (EntityType) managedType,
                                              builder);
        }

        // done
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                       "Managed type descriptor created successfully: {0}",
                       descriptor);
        }

        return descriptor;
    }

    /**
     * Returns the managed type of the data this descriptor describes.
     * @return the managed type.
     */
    public ManagedType getManagedType() {
        return managedType;
    }

    //
    // private methods
    //
    private void initChildren() {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                Level.FINE,
                "Creating attribute descriptors for the data [{0}] "
                + "represented by the managed type [{1}].", new Object[] { data,
                    managedType });
        }

        // create children for attribute if the data is not null
        if (data != null) {
            managedType.getAttributes();
            Set<Attribute> attributes = managedType.getAttributes();
            children.ensureCapacity(attributes.size());
            for (Attribute attribute : attributes) {
                children.add(createAttributeDescriptor(data, attribute));
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "{0} Attribute descriptors created for the data [{1}] "
                    + "represented by the managed type [{2}].", new Object[] {
                        getChildCount(),
                        data, managedType });
        }
    }

    private AttributeDescriptor createAttributeDescriptor(Object data,
            Attribute attribute) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                       "Creating attribute descriptor for the attribute [{0}]",
                       attribute.getName());
        }

        DataDescriptor wrappedDescriptor = null;
        AttributeDescriptor attributeDescriptor = null;

        // determine the attribute value
        // TODO: create some special descriptor for attribute whose value
        // cannot be loaded.
        Object value = null;
        Member member = attribute.getJavaMember();
        if (member instanceof Field) {
            Field field = (Field) member;
            try {
                field.setAccessible(true);
                value = field.get(data);
            } catch (Exception ex) {
                throw new RuntimeException(
                    "Unexpected exception occurred when trying to "
                    + "reflectively get the value of the field: "
                    + field.getName(), ex);
            }
        } else if (member instanceof Method) {
            Method method = (Method) member;
            try {
                method.setAccessible(true);
                value = method.invoke(data, (Object[]) null);
            } catch (Exception ex) {
                throw new RuntimeException(
                    "Unexpected exception occurred when trying to "
                    + "reflectively invoke the method: "
                    + method.toGenericString(), ex);
            }
        } else {
            logger.log(
                Level.SEVERE,
                "Invalid java member [{0}] corresponding to "
                + "the attribute [{1}] of the data [{2}] represented by the "
                + "managed type [{3}]. The attribute value will be "
                + "unavaliable for viewing."
                + "This is possibly an issue of the persistence provider "
                + "implementation. By far, eclipselink 2.0.2 is known to "
                + "return null member for attributes of embeddable type. "
                + "Please check the documentation of the provider for "
                + "more information. ", new Object[] { member,
                    attribute.getName(), data, managedType });
        }

        Class attributeType = attribute.getJavaType();
        Class elementType = null;
        Class keyType = null;
        if (attribute instanceof PluralAttribute) {
            elementType =
                ((PluralAttribute) attribute).getElementType().getJavaType();

            // hack for eclipselink 2.0.2 implementation
            // eclipselink 2.0.2 returns the element type from
            // PluralAttribute.getJavaType() instead of the collection type.
            if (attribute instanceof CollectionAttribute) {
                attributeType = Collection.class;
            } else if (attribute instanceof SetAttribute) {
                attributeType = Set.class;
            } else if (attribute instanceof ListAttribute) {
                attributeType = List.class;
            } else if (attribute instanceof MapAttribute) {
                attributeType = Map.class;
                keyType = ((MapAttribute) attribute).getKeyJavaType();
            }
        }

        // TODO: add directly support to create collection, map, array
        // descriptor to the builder, so we can explicitly specify the
        // known element and key type.
        wrappedDescriptor = builder.createDataDescriptor(this, value,
                attributeType);
        attributeDescriptor = new AttributeDescriptor(wrappedDescriptor,
                attribute);

        // done
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Attribute descriptor created: {0} ",
                       attributeDescriptor);
        }

        return attributeDescriptor;
    }

    /**
     * A <tt>AttributeDescriptor</tt> describes one attribute of a managed
     * type, including entity and embeddable type.
     */
    private final class AttributeDescriptor extends ProxyDescriptor {
        private final Attribute attribute;

        private AttributeDescriptor(DataDescriptor valueDescriptor,
                                    Attribute attribute) {
            super(valueDescriptor);
            this.attribute = attribute;
        }

        @Override
        public String getDisplayName() {
            return attribute.getName();
        }

        @Override
        public String getShortDescription() {
            String result = null;
            PersistentAttributeType type =
                attribute.getPersistentAttributeType();
            switch (type) {
            case BASIC :
                result = "Basic";
                break;

            case ELEMENT_COLLECTION :
                result = "Element Collection";
                break;

            case EMBEDDED :
                result = "Embedded";
                break;

            case MANY_TO_MANY :
                result = "ManyToMany relationship";
                break;

            case MANY_TO_ONE :
                result = "ManyToOne relationship";
                break;

            case ONE_TO_MANY :
                result = "OneToMany relationship";
                break;

            case ONE_TO_ONE :
                result = "OneToOne relationship";
                break;

            default :
                result = null;
            }

            return result;
        }
    }


    /**
     * A <tt>EntityDescriptor</tt> describes one object of a entity type.
     */
    private static class EntityDescriptor extends ManagedTypeDescriptor {
        private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/ql/view/resources/entity.gif";
        private static final Icon ICON =
            ImageUtilities.loadImageIcon(ICON_BASE, true);

        private EntityDescriptor(DataDescriptor parent, Object data,
                                 EntityType entityType,
                                 DataDescriptorBuilder provider) {
            super(parent, data, entityType, provider);
        }

        @Override
        public Icon getIcon() {
            return ICON;
        }
    }


    /**
     * A <tt>EmbeddableDescriptor</tt> describes one object of a embeddable
     * type.
     */
    private static class EmbeddableDescriptor extends ManagedTypeDescriptor {
        private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/ql/view/resources/entity.gif";
        private static final Icon ICON =
            ImageUtilities.loadImageIcon(ICON_BASE, true);

        private EmbeddableDescriptor(DataDescriptor parent, Object data,
                                     EmbeddableType embeddableType,
                                     DataDescriptorBuilder provider) {
            super(parent, data, embeddableType, provider);
        }

        @Override
        public Icon getIcon() {
            return ICON;
        }
    }
}
