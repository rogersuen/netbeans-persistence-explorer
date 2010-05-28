/*
 * @(#)DataDescriptor.java   10/05/26
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
 *
 * @author Roger Suen
 */
public interface DataDescriptor {
    Object getData();

    Class getDataType();

    String getDisplayName();

    String getDisplayType();

    String getDisplayValue();

    String getShortDescription();

    Icon getIcon();

    int getChildCount();

    DataDescriptor getChild(int index);

    int getIndexOfChild(DataDescriptor child);
}