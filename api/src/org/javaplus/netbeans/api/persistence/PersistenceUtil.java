/*
 * @(#)PersistenceUtil.java   10/06/01
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

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: consider to make PeristenceUnit class immutable
 * @author Roger Suen
 */
public class PersistenceUtil {

    /**
     * Returns the classpath entry of the specified persistence unit
     * as a list of URL.
     * @param unit the persistence unit to query, cannot be <tt>null</tt>.
     * @return a list of URL.
     * @throws NullPointerException if <tt>unit</tt> is <tt>null</tt>.
     * @throws IllegalStateException if any <tt>UrlSpec</tt> of this
     *         <tt>PersistenceUnit</tt> returns <tt>null</tt> URL.
     */
    public static List<URL> getUrls(PersistenceUnit unit) {
        if (unit == null) {
            throw new NullPointerException("null unit");
        }

        List<UrlSpec> urlSpecs = unit.getUrlSpecs();
        List<URL> result = new ArrayList<URL>(urlSpecs.size());
        for (UrlSpec spec : urlSpecs) {
            URL url = spec.getUrl();
            if (url == null) {
                throw new IllegalStateException(
                    "A null URL returned from one of UrlSpecs of the "
                    + "PeristenceUnit object: PersistenceUnit = " + unit
                    + ", UrlSpec = " + spec);
            }

            result.add(url);
        }

        return result;
    }

    /**
     * Returns the classpath entry of the specified persistence provider
     * as a list of URL.
     * @param provider the persistence provider to query, cannot be
     *                 <tt>null</tt>.
     * @return a list of URL.
     * @throws NullPointerException if <tt>provider</tt> is <tt>null</tt>.
     * @throws IllegalStateException if any <tt>UrlSpec</tt> of this
     *         <tt>PersistenceProvider</tt> returns <tt>null</tt> URL.
     */
    public static List<URL> getUrls(PersistenceProvider provider) {
        if (provider == null) {
            throw new NullPointerException("null provider");
        }

        List<UrlSpec> urlSpecs = provider.getUrlSpecs();
        List<URL> result = new ArrayList<URL>(urlSpecs.size());
        for (UrlSpec spec : urlSpecs) {
            URL url = spec.getUrl();
            if (url == null) {
                throw new IllegalStateException(
                    "A null URL returned from one of UrlSpecs of the "
                    + "PeristenceProvider: PersistenceProvider = " + provider
                    + ", UrlSpec = " + spec);
            }

            result.add(url);
        }

        return result;
    }
}
