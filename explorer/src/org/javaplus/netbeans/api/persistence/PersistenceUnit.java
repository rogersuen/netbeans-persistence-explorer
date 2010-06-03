/*
 * @(#)PersistenceUnit.java   10/06/02
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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Roger Suen
 */
public final class PersistenceUnit {
    private String name;
    private String displayName;
    private String description;

    // TODO: CopyOnWriteArrayList?
    private final List<UrlSpec> urlSpecs = new ArrayList<UrlSpec>();

    public PersistenceUnit(String name, String displayName, String description,
                           List<UrlSpec> urls) {
        setName(name);
        setDisplayName(displayName);
        setDescription(description);
        addUrlSpecs(urls);
    }

    /**
     * Returns the name of this persistence unit.
     * @return the name of this persistence unit
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the new value of the name of this persistence unit.
     * @param name new value of the name, cannot be <tt>null</tt>
     * @throws NullPointerException if <tt>name</tt> is <tt>null</tt>
     */
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("null name");
        }

        this.name = name;
    }

    /**
     * Returns the display name of this persistence unit.
     * @return the display name of this persistence unit
     */
    public String getDisplayName() {
        if (displayName == null) {
            return name;
        } else {
            return displayName;
        }
    }

    /**
     * Sets the new value of the display name of this persistence unit.
     * @param displayName new value of the display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the short description of this persistence unit.
     * @return the short description of this persistence unit
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the new value of the short description of this persistence unit.
     * @param description new value of the short description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the class path URLs of this persistence unit as a mutable list.
     * Each entry of class path URLs is wrapped in a {@link UrlSpec} object.
     * @return a mutable list of <tt>UrlSpec</tt>
     */
    public List<UrlSpec> getUrlSpecs() {
        return urlSpecs;
    }

    /**
     * Adds a list of <tt>UrlSpec</tt>s.
     * @param urlSpecs a list of <tt>UrlSpec</tt>s to add
     */
    public void addUrlSpecs(List<UrlSpec> urlSpecs) {
        if (urlSpecs == null) {
            throw new NullPointerException("null spec list");
        }

        this.urlSpecs.addAll(urlSpecs);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + "name=" + name + ", displayName="
               + displayName + ", description=" + description + ", urlSpecs="
               + urlSpecs + ']';
    }
}
