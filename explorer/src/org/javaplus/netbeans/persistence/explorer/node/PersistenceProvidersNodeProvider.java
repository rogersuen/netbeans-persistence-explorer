/*
 * @(#)PersistenceProvidersNodeProvider.java   10/04/16
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

import org.javaplus.netbeans.api.persistence.explorer.node.NodeProvider;
import org.openide.nodes.Node;

/**
 *
 * @author Roger Suen
 */
public class PersistenceProvidersNodeProvider implements NodeProvider {
    private static PersistenceProvidersNodeProvider instance;

    public static PersistenceProvidersNodeProvider getInstance() {
        if (instance == null) {
            instance = new PersistenceProvidersNodeProvider();
        }

        return instance;
    }

    public Node[] getNodes() {
        return new Node[] { PersistenceProvidersNode.getInstance() };
    }
}
