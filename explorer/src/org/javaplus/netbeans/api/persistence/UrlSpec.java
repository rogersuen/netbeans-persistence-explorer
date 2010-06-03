/*
 * @(#)UrlSpec.java   10/04/28
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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A wrapper of a string to be parsed as a <tt>URL</tt>.
 * @author Roger Suen
 */
public final class UrlSpec {
    private String spec;
    private URL url;

    /**
     * Constructs a new wrapper from the given <tt>String</tt> representation.
     * @param spec the <tt>String</tt> to parse as a <tt>URL</tt>, cannot
     *              be <tt>null</tt>
     * @throws NullPointerException if the <tt>spec</tt> is <tt>null</tt>
     */
    public UrlSpec(String spec) {
        setSpec(spec);
    }

    /**
     * Constructs a new wrapper from the given URL.
     * @param url the URL to wrap, cannot be <tt>null</tt>
     * @throws NullPointerException if the <tt>url</tt> is <tt>null</tt>
     * @see #setUrl(java.net.URL)
     */
    public UrlSpec(URL url) {
        setUrl(url);
    }

    /**
     * Returns the raw spec used to construct this object.
     * @return the raw spec.
     */
    public String getSpec() {
        return spec;
    }

    /**
     * Sets a new value of the wrapped string representation of a <tt>URL</tt>.
     * @param spec a new wrapped string representation, cannot be
     *              <tt>null</tt>
     * @throws NullPointerException if the <tt>spec</tt> is <tt>null</tt>
     */
    public void setSpec(String spec) {
        if (spec == null) {
            throw new NullPointerException("null spec");
        }

        this.spec = spec;
        try {
            this.url = new URL(spec);
        } catch (MalformedURLException ex) {}
    }

    /**
     * Sets a new value of the URL wrapped by this object. The string
     * representation of the URL will be created by calling the
     * <tt>URL.toExternalForm()</tt> method.
     *
     * @param url a new value of the URL, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>url</tt> is <tt>null</tt>
     */
    public void setUrl(URL url) {
        if (url == null) {
            throw new NullPointerException("null url");
        }

        this.url = url;
        this.spec = url.toExternalForm();
    }

    /**
     * Returns the URL wrapped by this object, or <tt>null</tt> if it is
     * malformed.
     * @return the URL wrapped by this object, or <tt>null</tt> if it is
     *          malformed
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns a string representation of this object. The string is created
     * by calling the {@link #getUrl() }<tt>.toExternalForm()</tt> method if
     * <tt>getUrl()</tt> returns a non-null value, or returns the value
     * returned from the method {@link #getSpec()}</tt>.
     *
     * @return a string representation of this object
     * @see #getUrl()
     * @see #getSpec()
     */
    @Override
    public String toString() {
        if (url != null) {
            return url.toExternalForm();
        } else {
            return spec;
        }
    }

    /**
     * <p>
     * Compares this <tt>UrlSpec</tt> for equality with another object.</p>
     * <p>
     * If the given object is <tt>null</tt>, or not a <tt>UrlSpec</tt>
     * then this method immediately returns <tt>false</tt>.
     * <p>
     * If the URLs wrapped in both <tt>UrlSpec</tt> objects are malformed,
     * these two <tt>UrlSpec</tt> objects are equal if they has the same spec,
     * otherwise, two <tt>UrlSpec</tt> objects are equal if they has the
     * same wrapped URL.</p>
     *
     * @param obj another <tt>UrlSpec</tt> to compare against.
     * @return <tt>true</tt> if the objects are the same; <tt>false</tt>
     *          otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final UrlSpec other = (UrlSpec) obj;
        if ((this.url == null) && (other.url == null)) {
            return (this.spec == null)
                   ? (other.spec == null)
                   : this.spec.equals(other.spec);
        }

        return (this.url == null)
               ? (other.url == null)
               : this.url.equals(other.url);
    }

    @Override
    public int hashCode() {
        Object obj = ((url != null)
                      ? url
                      : spec);
        int hash = 5;
        hash = 23 * hash + ((obj != null)
                            ? obj.hashCode()
                            : 0);
        return hash;
    }
}
