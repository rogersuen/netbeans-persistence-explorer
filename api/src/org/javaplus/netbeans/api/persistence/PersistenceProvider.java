/*
 * @(#)PersistenceProvider.java   10/04/28
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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author roger
 */
public class PersistenceProvider {
    private String name;
    private String displayName;
    private String description;
    private final List<URL> urls = new LinkedList<URL>();

    public PersistenceProvider(String name, String displayName,
                               String description, List<String> urls)
            throws MalformedURLException {
        setName(name);
        setDisplayName(displayName);
        setDescription(description);
        addUrls(urls);
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("null name");
        }

        this.name = name;
    }

    /**
     * Get the value of displayName
     *
     * @return the value of displayName
     */
    public String getDisplayName() {
        if (displayName == null) {
            return name;
        } else {
            return displayName;
        }
    }

    /**
     * Set the value of displayName
     *
     * @param displayName new value of displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<URL> getUrls() {
        return urls;
    }

    public void addUrls(List<String> urlStrings) throws MalformedURLException {
        for (String urlString : urlStrings) {
            URL url = new URL(urlString);
            this.urls.add(url);
        }
    }
}
