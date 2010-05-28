/*
 * @(#)ManagedTypeDescriptor.java   10/05/26
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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;

/**
 *
 * @author Roger Suen
 */
public abstract class ManagedTypeDescriptor extends NonLeafDataDescriptor {
    private static final Logger logger =
        Logger.getLogger(ManagedTypeDescriptor.class.getName());

    /**
     * Managed type of the data.
     */
    private final ManagedType managedType;

    /**
     * Sole constrcutor.
     * @param data
     * @param managedType
     * @param entityManager
     */
    protected ManagedTypeDescriptor(Object data, ManagedType managedType,
                                    DataDescriptorProvider provider) {
        super(data, managedType.getJavaType(), provider);
        this.managedType = managedType;
        initChildren();
    }

    private void initChildren() {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                       "Creating attribute descriptors for managed type: "
                       + "managedType = {0} data = {1}", new Object[] {
                           managedType.getPersistenceType(),
                           data });
        }

        // create children for attribute if the data is not null
        if (data != null) {
            Set<Attribute> attributes = managedType.getAttributes();
            children.ensureCapacity(attributes.size());
            for (Attribute attribute : attributes) {
                children.add(createAttributeDescriptor(data, attribute));
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                       "{0} attribute descriptors created: "
                       + "managedType = {1} data = {2}", new Object[] {
                           children.size(),
                           managedType.getPersistenceType(), data });
        }
    }

    public ManagedType getManagedType() {
        return managedType;
    }

    private AttributeDescriptor createAttributeDescriptor(Object data,
            Attribute attribute) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Creating AttributeDescriptor: name = {0}",
                       attribute.getName());
        }

        Class type = null;
        if (attribute.isCollection()) {
            if (attribute instanceof CollectionAttribute) {
                type = Collection.class;
            } else if (attribute instanceof SetAttribute) {
                type = Set.class;
            } else if (attribute instanceof ListAttribute) {
                type = List.class;
            } else if (attribute instanceof MapAttribute) {
                type = Map.class;
            }
        } else {
            type = attribute.getJavaType();
        }

        Member member = attribute.getJavaMember();

        // the attribute corresponds to either java field or getter
        assert member instanceof AccessibleObject;

        // make member accessible by reflection
        AccessibleObject accessibleMember = (AccessibleObject) member;
        accessibleMember.setAccessible(true);

        // load attribute value
        Object value = null;
        if (accessibleMember instanceof Field) {
            Field field = (Field) accessibleMember;
            try {
                value = field.get(data);
            } catch (IllegalArgumentException ex) {}
            catch (IllegalAccessException ex) {}
        } else if (accessibleMember instanceof Method) {
            Method method = (Method) accessibleMember;
            try {
                value = method.invoke(data, (Object[]) null);
            } catch (IllegalAccessException ex) {}
            catch (IllegalArgumentException ex) {}
            catch (InvocationTargetException ex) {}
        } else {    // should never happen
            assert false;
        }

        DataDescriptor wrapped = provider.createDataDescriptor(value, type);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                       "AttributeDescriptor created: "
                       + "name = {0} type = {1} value = {2}", new Object[] {
                           attribute.getName(),
                           value });
        }

        return new AttributeDescriptor(wrapped, attribute);
    }

    private final class AttributeDescriptor extends ProxyDataDescriptor {
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
}
