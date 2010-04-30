/*
 * @(#)PersistenceProviderEnvironmentProvider.java   10/04/28
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

package org.javaplus.netbeans.api.persistence;

import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.Environment;
import org.openide.loaders.XMLDataObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.xml.EntityCatalog;
import org.openide.xml.XMLUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import java.net.MalformedURLException;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Roger Suen
 */
public class PersistenceProviderEnvironmentProvider
        implements Environment.Provider {
    private static final Environment.Provider instance =
        new PersistenceProviderEnvironmentProvider();

    public static Environment.Provider getInstance() {
        return instance;
    }

    //
    // Environment.Provider implementation
    //
    public Lookup getEnvironment(DataObject obj) {
        PersistenceProviderConverter converter =
            new PersistenceProviderConverter((XMLDataObject) obj);
        return converter.getLookup();
    }
}
