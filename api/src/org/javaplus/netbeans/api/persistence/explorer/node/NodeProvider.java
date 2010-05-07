/*
 * @(#)NodeProvider.java   10/04/28
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
package org.javaplus.netbeans.api.persistence.explorer.node;

import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Node;

/**
 * TODO: javadoc
 * @author Roger Suen
 */
public interface NodeProvider {

    List<Node> getNodes();

    void addChangeListener(ChangeListener listener);

    void removeChangeListener(ChangeListener listener);
}
