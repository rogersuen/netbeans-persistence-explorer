/*
 * @(#)AtomicWrite.java   10/04/28
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

import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Roger Suen
 */
final class AtomicWrite implements FileSystem.AtomicAction {
    private FileObject folder;
    private String name;
    private String ext;
    private String content;
    private static final Logger logger =
        Logger.getLogger(AtomicWrite.class.getName());

    public AtomicWrite(String folder, String name, String ext, String content) {
        if (folder == null) {
            throw new NullPointerException("null folder");
        }

        if (name == null) {
            throw new NullPointerException("null name");
        }

        if (ext == null) {
            throw new NullPointerException("null ext");
        }

        if (content == null) {
            throw new NullPointerException("null content");
        }

        FileObject fo = FileUtil.getConfigFile(folder);
        if ((fo == null) ||!fo.isFolder()) {
            throw new IllegalArgumentException(
                "FileObject specified by the given folder argument "
                + "does not exist or is not a folder.");
        }

        this.folder = fo;
        this.name = name;
        this.ext = ext;
        this.content = content;
    }

    public void run() throws IOException {

        // create and lock the file object with retry
        FileObject fileObject = null;
        FileLock fileLock = null;
        for (int retry = 3; retry > 0; retry--) {
            String fn = FileUtil.findFreeFileName(folder, name, ext);
            try {
                fileObject = folder.createData(fn, ext);

                // NOTE:
                // If any possibility that we created the file object, but
                // failed to lock it, the file object will become waste.
                fileLock = fileObject.lock();
                break;
            } catch (IOException ex) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                               "Failed to create or lock the file object "
                               + folder.getPath() + "/" + fn + "." + ext, ex);
                }

                if (retry == 1) {
                    throw ex;
                }
            }
        }

        // write out the content
        try {
            PrintWriter writer = new PrintWriter(
                                     new OutputStreamWriter(
                                         fileObject.getOutputStream(fileLock),
                                         "UTF8"), true);
            writer.print(content);
            writer.close();
        } catch (IOException ex) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(
                    Level.WARNING,
                    "Failed to write content to the file object "
                    + fileObject.getPath()
                    + ". The file object will be removed automatically.", ex);
            }

            try {
                fileObject.delete(fileLock);
            } catch (IOException ex2) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING,
                               "Failed to remove the file object "
                               + fileObject.getPath(), ex);
                }
            }
        } finally {
            fileLock.releaseLock();
            fileLock = null;
        }
    }
}
