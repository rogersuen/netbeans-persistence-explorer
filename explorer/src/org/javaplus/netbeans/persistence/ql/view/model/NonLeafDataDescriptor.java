/*
 * @(#)NonLeafDataDescriptor.java   10/05/26
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

import java.util.ArrayList;

/**
 *
 * @author Roger Suen
 */
public abstract class NonLeafDataDescriptor extends DataDescriptorBase {
    protected final ArrayList<DataDescriptor> children;

    protected NonLeafDataDescriptor(Object data, Class declaredType,
                                    DataDescriptorProvider provider) {
        super(data, declaredType, provider);
        this.children = new ArrayList<DataDescriptor>();
    }

    @Override
    public DataDescriptor getChild(int index) {
        return children.get(index);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public int getIndexOfChild(DataDescriptor child) {
        return children.indexOf(child);
    }
}
