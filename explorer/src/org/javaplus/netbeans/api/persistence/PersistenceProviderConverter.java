/*
 * @(#)PersistenceProviderConverter.java   10/06/01
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
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
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

import java.io.CharConversionException;
import java.io.IOException;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Roger Suen
 */
class PersistenceProviderConverter implements Lookup.Provider {

    /**
     * The singleton logger
     */
    private static final Logger logger =
        Logger.getLogger(PersistenceProviderConverter.class.getName());
    private DataObject dataObject;
    private Lookup lookup;
    private Reference<PersistenceProvider> providerRef =
        new WeakReference<PersistenceProvider>(null);

    private PersistenceProviderConverter(DataObject dataObject) {
        this.dataObject = dataObject;
        InstanceContent ic = new InstanceContent();
        ic.add(new PeristenceProviderInstanceCookie());
        this.lookup = new AbstractLookup(ic);
    }

    public Lookup getLookup() {
        return lookup;
    }

    /**
     * Returns the data object this converter works for.
     * @return the data object this converter works for.
     */
    public DataObject getDataObject() {
        return dataObject;
    }

    /**
     * Returns the lookup provider for the specified data object. This method
     * is used by the
     * {@link PersistenceProviderEnvironmentProvider#getEnvironment(DataObject)}
     * method.
     *
     * @param dataObject the data object for which a lookup provider
     *                   is requested, cannot be <tt>null</tt>
     * @return an lookup provider instance
     * @throws NullPointerException if <tt>dataObject</tt> is <tt>null</tt>
     * @see PersistenceProviderEnvironmentProvider
     */
    public static Lookup.Provider getLookupProvider(DataObject dataObject) {

        // TODO: same instance for the same data object
        return new PersistenceProviderConverter(dataObject);
    }

    private static PersistenceProvider readFromFileObject(FileObject file) {
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

            // TODO: change to java logging
            ErrorManager.getDefault().notify(ErrorManager.WARNING, saxe);
            return null;
        } catch (IOException ioe) {

            // TODO: change to java logging
            ErrorManager.getDefault().notify(ErrorManager.WARNING, ioe);
            return null;
        }

        PersistenceProvider provider = null;

        // TODO: handle null name
        provider = new PersistenceProvider(handler.name, handler.displayName,
                                           handler.description,
                                           handler.urlSpecs);
        return provider;
    }

    public static void writeToFileObject(PersistenceProvider provider)
            throws IOException {
        if (provider == null) {
            throw new NullPointerException("null peristence provider");
        }

        // TODO: need a naming strategy to ensuse it is suitable for file name
        // NOTE: possible CharConversionException from toXMLString
        AtomicWrite atomicWrite =
            new AtomicWrite(PersistenceProviderManager.LAYER_FOLDER,
                            provider.getName(), "xml", toXMLString(provider));
        FileUtil.runAtomicAction(atomicWrite);
    }

    public static boolean remove(PersistenceProvider provider)
            throws IOException {
        boolean removed = false;
        String name = provider.getName();

        // TODO: remove dependency from converter to manager
        FileObject fo =
            FileUtil.getConfigFile(PersistenceProviderManager.LAYER_FOLDER);
        DataFolder folder = DataFolder.findFolder(fo);
        DataObject[] objects = folder.getChildren();
        for (int i = 0; i < objects.length; i++) {
            InstanceCookie ic = objects[i].getCookie(InstanceCookie.class);
            if (ic != null) {
                Object obj = null;
                try {
                    obj = ic.instanceCreate();
                } catch (ClassNotFoundException e) {
                    continue;
                }

                if (obj instanceof PersistenceProvider) {
                    PersistenceProvider p = (PersistenceProvider) obj;
                    if (p.getName().equals(name)) {
                        objects[i].delete();
                        removed = true;
                        break;
                    }
                }
            }
        }

        return removed;
    }

    private static String toXMLString(PersistenceProvider p)
            throws CharConversionException {
        StringBuilder sb = new StringBuilder(512);
        sb.append("<?xml version='1.0' encoding='UTF-8'?>\n");
        sb.append(
            "<!DOCTYPE provider "
            + "PUBLIC '-//JavaPlus//DTD Persistence Provider 1.0//EN' "
            + "'http://www.javaplus.org/dtds/persistence-provider-1_0.dtd'>\n");
        sb.append("<provider version='1.0'>\n");
        sb.append("<name value='");
        sb.append(XMLUtil.toAttributeValue(p.getName()));
        sb.append("'/>\n");
        sb.append("<display-name value='");
        sb.append(XMLUtil.toAttributeValue(p.getDisplayName()));
        sb.append("'/>\n");
        sb.append("<description value='");
        sb.append(XMLUtil.toAttributeValue(p.getDescription()));
        sb.append("'/>\n");
        sb.append("<urls>\n");
        List<UrlSpec> urls = p.getUrlSpecs();
        for (UrlSpec url : urls) {
            sb.append("<url value='");
            sb.append(XMLUtil.toAttributeValue(url.toString()));
            sb.append("'/>\n");
        }

        sb.append("</urls>\n");
        sb.append("</provider>\n");
        return sb.toString();
    }

    /**
     * Instance cookie for <tt>PersistenceProvider</tt>
     */
    private class PeristenceProviderInstanceCookie
            implements InstanceCookie.Of {
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

        public Object instanceCreate()
                throws IOException, ClassNotFoundException {
            PersistenceProvider result = providerRef.get();
            if (result != null) {
                return result;
            }

            DataObject data = getDataObject();
            if (data == null) {
                return null;
            }

            result = readFromFileObject(data.getPrimaryFile());
            providerRef = new WeakReference<PersistenceProvider>(result);
            return result;
        }
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
        private List<UrlSpec> urlSpecs = new ArrayList<UrlSpec>();

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
                urlSpecs.add(new UrlSpec(attributes.getValue(ATTRIBUTE_VALUE)));
            }
        }
    }
}
