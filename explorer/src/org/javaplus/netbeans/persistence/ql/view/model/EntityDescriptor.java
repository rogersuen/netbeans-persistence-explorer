/*
 * @(#)EntityDescriptor.java   10/05/26
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

import javax.persistence.metamodel.EntityType;

import javax.swing.Icon;

/**
 * @author Roger Suen
 */
public class EntityDescriptor extends ManagedTypeDescriptor {
    private static final String ICON_BASE =
        "org/javaplus/netbeans/persistence/ql/view/resources/entity.gif";
    private static final Icon ICON = ImageUtilities.loadImageIcon(ICON_BASE,
                                         true);

    private EntityDescriptor(Object data, EntityType entityType,
                             DataDescriptorProvider provider) {
        super(data, entityType, provider);
    }

    public static EntityDescriptor createEntityDescriptor(Object data,
            EntityType entityType, DataDescriptorProvider provider) {

        // NOTE:
        // data could be entity reference with null value,
        // so we need entityType here
        return new EntityDescriptor(data, entityType, provider);
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }
}
