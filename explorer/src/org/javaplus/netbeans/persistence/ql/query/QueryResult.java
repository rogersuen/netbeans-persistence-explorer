/*
 * @(#)QueryResult.java   10/05/20
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

/**
 *
 * @author Roger Suen
 */
public class QueryResult {
    private PreparedQuery preparedQuery;
    private Object result;

    public QueryResult(PreparedQuery preparedQuery, Object result) {
        if (preparedQuery == null) {
            throw new NullPointerException("null prepared query");
        } else if (result == null) {
            throw new NullPointerException("null result");
        }

        this.preparedQuery = preparedQuery;
        this.result = result;
    }

    public PreparedQuery getPreparedQuery() {
        return preparedQuery;
    }

    public Object getResult() {
        return result;
    }

    public EntityManager getEntityManager() {
        return preparedQuery.getEntityManager();
    }
}
