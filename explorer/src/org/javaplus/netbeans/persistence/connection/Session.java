/*
 * @(#)Session.java   10/05/17
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

package org.javaplus.netbeans.persistence.connection;

import java.util.List;
import javax.swing.text.BadLocationException;
import org.javaplus.netbeans.persistence.ql.editor.QLEditorSupport;
import org.openide.util.Exceptions;

import org.openide.util.NbBundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.swing.text.Document;
import org.javaplus.netbeans.persistence.ql.query.PreparedQuery;
import org.javaplus.netbeans.persistence.ql.query.QueryResult;

/**
 *
 * @author Roger Suen
 */
public class Session {

    /**
     * The singleton logger instance.
     */
    private static final Logger logger =
        Logger.getLogger(Session.class.getName());

    /**
     *
     */
    private static final String BUNDLE_KEY_SESSION_NAME = "Session.NAME";

    /**
     *
     */
    private final Connection connection;

    /**
     *
     */
    private final Object id;

    /**
     *
     */
    private final String name;

    /**
     *
     */
    private ThreadFactory threadFactory;

    /**
     *
     */
    private ExecutorService executor;

    /**
     *
     */
    private EntityManager entityManager;

    /**
     *
     */
    private QLEditorSupport editorSupport;

    /**
     *
     */
    private boolean isOpen = true;

    Session(Connection connection, Object id) throws ConnectionException {
        this.connection = connection;
        this.id = id;

        // set the name of this session
        String puName = connection.getPersistenceUnit().getDisplayName();
        this.name = NbBundle.getMessage(Session.class, BUNDLE_KEY_SESSION_NAME,
                                        new Object[] { id,
                puName });
        doOpen();
    }

    public Object getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    private PreparedQuery getPreparedQuery() {
        String qlString = null;
        Document doc = editorSupport.getDocument();
        try {
            qlString = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
        }
        Query query = entityManager.createQuery(qlString);
        return new PreparedQuery(query, qlString, entityManager);
    }

    public void executeQuery() {
        PreparedQuery preparedQuery = getPreparedQuery();
        Query query = preparedQuery.getQuery();
        List result = query.getResultList();
        QueryResult queryResult = new QueryResult(preparedQuery, result);
        editorSupport.showQueryResult(queryResult);
    }

    private void doOpen() throws ConnectionException {

        // init entity manager
        EntityManagerFactory emf = connection.getEntityManagerFactory();
        entityManager = emf.createEntityManager();

        // init executor
        threadFactory = new SessionThreadFactory();
        executor = Executors.newSingleThreadExecutor(threadFactory);

        // init and open editor
        editorSupport = QLEditorSupport.create(this);
        editorSupport.open();
    }

    /**
     * Indicates whether this session is open. Returns <tt>true</tt> until
     * the session has been closed.
     * @return <tt>true</tt> if this session is open; <tt>false</tt>
     *         otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    public void close() throws ConnectionException {
        verifyOpen();
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Session {0} closing...", getName());
        }

        // must set open status to false first
        // NOTE:
        // editorSupport.close() will eventually call the QLEditor.closeLast()
        // method, which calls close method of the session if the session is
        // open. Set the flag of the open status to false to avaid recursion.
        isOpen = false;
        editorSupport.close();
        entityManager.close();
        executor.shutdownNow();
        connection.removeSession(this);

        // done
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Session {0} closed successfully.",
                       getName());
        }
    }

    private void verifyOpen() {
        if (!isOpen) {
            throw new IllegalStateException(
                "Attempting to execute an operation on a closed "
                + "Session instance.");
        }
    }

    private class SessionThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable target) {
            Thread t = new Thread(target);

            // thread name for debug purpose only, no i18n
            t.setName("SessionExecutorThread (" + getId() + ")");
            return t;
        }
    }
}
