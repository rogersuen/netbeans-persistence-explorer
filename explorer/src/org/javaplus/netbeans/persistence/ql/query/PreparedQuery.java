/*
 * @(#)PreparedQuery.java   10/05/20
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

package org.javaplus.netbeans.persistence.ql.query;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Roger Suen
 */
public class PreparedQuery {

    /**
     * The query string.
     */
    private final String qlString;

    /**
     * The prepared query to be executed.
     */
    private final Query query;

    /**
     *
     */
    private final EntityManager entityManager;

    /**
     *
     * @param query
     * @param qlString
     */
    public PreparedQuery(Query query, String qlString,
                         EntityManager entityManager) {
        if (query == null) {
            throw new NullPointerException("null query");
        } else if (qlString == null) {
            throw new NullPointerException("null query string");
        } else if (entityManager == null) {
            throw new NullPointerException("null entity manager");
        }

        this.qlString = qlString;
        this.query = query;
        this.entityManager = entityManager;
    }

    public String getQueryString() {
        return qlString;
    }

    public Query getQuery() {
        return query;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
