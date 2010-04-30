/*
 * @(#)PersistenceProviderConverter.java   10/04/28
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
class PersistenceProviderConverter
        implements InstanceCookie.Of, Lookup.Provider {
    private XMLDataObject dataObject;
    private Lookup lookup;
    private Reference<PersistenceProvider> providerRef =
        new WeakReference<PersistenceProvider>(null);

    public PersistenceProviderConverter(XMLDataObject dataObject) {
        this.dataObject = dataObject;
        InstanceContent ic = new InstanceContent();
        ic.add(this);    // InstanceCookie.Of interface
        this.lookup = new AbstractLookup(ic);
    }

    public Lookup getLookup() {
        return lookup;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    //
    // InstanceCookie.Of implementation
    //
    public boolean instanceOf(Class<?> type) {
        return type.isAssignableFrom(PersistenceProvider.class);
    }

    public String instanceName() {
        DataObject obj = getDataObject();
        if (obj == null) {
            return PersistenceProvider.class.getName();
        } else {
            return obj.getPrimaryFile().getName();
        }
    }

    public Class<?> instanceClass() {
        return PersistenceProvider.class;
    }

    public Object instanceCreate() throws IOException, ClassNotFoundException {
        PersistenceProvider result = providerRef.get();
        if (result != null) {
            return result;
        }

        DataObject data = getDataObject();
        if (data == null) {
            return null;
        }

        result = parseFileObject(data.getPrimaryFile());
        providerRef = new WeakReference<PersistenceProvider>(result);
        return result;
    }

    private static PersistenceProvider parseFileObject(FileObject file) {
        Handler handler = new Handler();
        try {
            XMLReader reader = XMLUtil.createXMLReader();
            reader.setContentHandler(handler);
            reader.setEntityResolver(EntityCatalog.getDefault());
            InputSource is = new InputSource(file.getInputStream());
            String urlString = file.getURL().toExternalForm();
            is.setSystemId(urlString);
            reader.parse(is);
        } catch (SAXException saxe) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, saxe);
            return null;
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ioe);
            return null;
        }

        PersistenceProvider provider = null;
        try {
            provider = new PersistenceProvider(handler.name,
                                               handler.displayName,
                                               handler.description,
                                               handler.urlStrings);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ex);
        }

        return provider;
    }

    /**
     * Default handler for parsing <tt>PersistenceProvider</tt> from
     * XML.
     */
    private static final class Handler extends DefaultHandler {
        private static final String ELEMENT_PROVIDER = "provider";
        private static final String ELEMENT_NAME = "name";
        private static final String ELEMENT_DISPLAY_NAME = "display-name";
        private static final String ELEMENT_DESCRIPTION = "description";
        private static final String ELEMENT_URLS = "urls";
        private static final String ELEMENT_URL = "url";
        private static final String ATTRIBUTE_VALUE = "value";
        private String name;
        private String displayName;
        private String description;
        private List<String> urlStrings = new LinkedList<String>();

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes)
                throws SAXException {
            if (ELEMENT_PROVIDER.equals(qName)) {}
            else if (ELEMENT_NAME.equals(qName)) {
                name = attributes.getValue(ATTRIBUTE_VALUE);
            } else if (ELEMENT_DISPLAY_NAME.equals(qName)) {
                displayName = attributes.getValue(ATTRIBUTE_VALUE);
            } else if (ELEMENT_DESCRIPTION.equals(qName)) {
                description = attributes.getValue(ATTRIBUTE_VALUE);
            } else if (ELEMENT_URLS.equals(qName)) {}
            else if (ELEMENT_URL.equals(qName)) {
                urlStrings.add(attributes.getValue(ATTRIBUTE_VALUE));
            }
        }
    }
}
