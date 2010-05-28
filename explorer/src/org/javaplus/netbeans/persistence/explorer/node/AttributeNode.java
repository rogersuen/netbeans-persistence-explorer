/*
 * @(#)PersistenceProviderNode.java   10/04/20
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
package org.javaplus.netbeans.persistence.explorer.node;

import java.lang.reflect.InvocationTargetException;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.javaplus.netbeans.api.persistence.explorer.node.NodeBase;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author roger
 */
public class AttributeNode extends NodeBase {

    public static final String LAYER_FOLDER = "AttributeNode";
    private static final String ICON_BASE =
            "org/javaplus/netbeans/persistence/resources/attribute.gif";
    private final Attribute attribute;

    AttributeNode(Attribute attribute) {
        if (attribute == null) {
            throw new NullPointerException("null attribute");
        }

        this.attribute = attribute;
        lookup.getInstanceContent().add(attribute);
        setName(attribute.getName());
        setShortDescription(formatShortDescription());
        setIconBaseWithExtension(ICON_BASE);
    }

    @Override
    protected String getLayerFolder() {
        return LAYER_FOLDER;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        Property property = new PropertySupport.ReadOnly<String>("name", String.class, "displayName", "shortDescription") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return attribute.getName();
            }

        };

        set.put(property);
        sheet.put(set);
        return sheet;
    }

    @Override
    public String getHtmlDisplayName() {
        return formatDisplayName();
    }

    private String formatDisplayName() {
        // name
        StringBuilder sb = new StringBuilder(attribute.getName());

        sb.append("<font color='#666666'> : ");

        // type
        Type type = null;
        if (attribute instanceof PluralAttribute) {
            if (attribute instanceof CollectionAttribute) {
                sb.append("Collection&lt;");
            } else if (attribute instanceof ListAttribute) {
                sb.append("List&lt;");
            } else if (attribute instanceof SetAttribute) {
                sb.append("Set&lt;");
            } else if (attribute instanceof MapAttribute) {
                sb.append("Map&lt;");
                sb.append(formatTypeName(((MapAttribute) attribute).getKeyType()));
                sb.append(",");
            }
            type = ((PluralAttribute) attribute).getElementType();
        } else { // attribute instanceof SingularAttribute
            type = ((SingularAttribute) attribute).getType();
        }
        sb.append(formatTypeName(type));
        if (attribute instanceof PluralAttribute) {
            sb.append("&gt;");
        }

        sb.append("</font>");

        return sb.toString();
    }

    private String formatTypeName(Type type) {
        if (type instanceof EntityType) {
            return ((EntityType) type).getName();
        } else {
            return type.getJavaType().getSimpleName();
        }
    }

    private String formatShortDescription() {
        StringBuilder sb = new StringBuilder();
        PersistentAttributeType type = attribute.getPersistentAttributeType();
        if (type == PersistentAttributeType.BASIC) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_BASIC"));
        } else if (type == PersistentAttributeType.ELEMENT_COLLECTION) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_ELEMENT_COLLECTION"));
        } else if (type == PersistentAttributeType.EMBEDDED) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_EMBEDDED"));
        } else if (type == PersistentAttributeType.MANY_TO_MANY) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_MANY_TO_MANY"));
        } else if (type == PersistentAttributeType.MANY_TO_ONE) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_MANY_TO_ONE"));
        } else if (type == PersistentAttributeType.ONE_TO_MANY) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_ONE_TO_MANY"));
        } else if (type == PersistentAttributeType.ONE_TO_ONE) {
            sb.append(NbBundle.getMessage(AttributeNode.class,
                    "AttributeNode.PERSISTENT_ATTRIBUTE_TYPE_ONE_TO_ONE"));
        }

        if (attribute.isAssociation()) {
            sb.append(" ASSOCIATION");
        }

        if (attribute.isCollection()) {
            sb.append(" COLLECTION");
        }

        return sb.toString();
    }
}
