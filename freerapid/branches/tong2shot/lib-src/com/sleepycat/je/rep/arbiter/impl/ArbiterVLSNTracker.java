/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.arbiter.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.utilint.VLSN;

/**
 * This class is used to maintain two pieces of
 * persistent state. The replication group node identifier
 * of the Arbiter and a VLSN value that represents the
 * highest commit record VLSN the Arbiter has acknowledged.
 */
class ArbiterVLSNTracker {
    private final int VERSION = 1;
    private RandomAccessFile raf;
    private final File dataFile;
    private VLSN currentVLSN = VLSN.NULL_VLSN;
    private final int VERSION_OFFSET = 0;
    private final int NODEID_OFFSET = Integer.SIZE + VERSION_OFFSET;
    private final int DATA_OFFSET = Integer.SIZE + NODEID_OFFSET;
    private int nodeId = NameIdPair.NULL_NODE_ID;

    ArbiterVLSNTracker(File file) {
        dataFile = file;
        boolean fileExists = dataFile.exists();
        try {
            raf = new RandomAccessFile(dataFile, "rwd");
            if (fileExists) {
                if (readVersion() != VERSION) {
                    throw new RuntimeException(
                        "Arbiter data file does not have a supported " +
                        "version field " +
                        dataFile.getAbsolutePath());
                }
                nodeId = readNodeId();
                if (raf.length() > DATA_OFFSET) {
                    raf.seek(DATA_OFFSET);
                    currentVLSN = new VLSN(raf.readLong());
                }
            } else {
                writeVersion(VERSION);
                writeNodeIdInternal(nodeId);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to read the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
        catch (Exception e) {
            throw new RuntimeException(
                "Unable to open the Arbiter data file " +
                dataFile.getAbsolutePath() + " exception " + e.getMessage());
        }

    }

    public synchronized void writeNodeId(int id) {
        if (nodeId == id) {
            return;
        }
        writeNodeIdInternal(id);
    }

    public synchronized int getCachedNodeId() {
        return nodeId;
    }

    private void writeNodeIdInternal(int id) {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to write the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        try {
            raf.seek(NODEID_OFFSET);
            raf.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to write the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    private int readNodeId() {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to read the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        try {
            raf.seek(NODEID_OFFSET);
            return raf.readInt();
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to read the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    public synchronized void writeVersion(int id) {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to write the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }

        if (nodeId == id) {
            return;
        }
        try {
            raf.seek(VERSION_OFFSET);
            raf.writeInt(id);
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to write the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    private int readVersion() {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to read the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        try {
            raf.seek(VERSION_OFFSET);
            return raf.readInt();
        } catch (IOException e) {
            throw new RuntimeException(
                "Unable to write the Arbiter data file " +
                dataFile.getAbsolutePath());
        }
    }

    public synchronized void write(VLSN value) {
        if (raf == null) {
            throw new RuntimeException(
                "Internal error: Unable to write the Arbiter data file " +
                " because the file is not open." +
                dataFile.getAbsolutePath());
        }
        if (value.compareTo(currentVLSN) > 0) {
            currentVLSN = new VLSN(value.getSequence());
            try {
                raf.seek(DATA_OFFSET);
                raf.writeLong(currentVLSN.getSequence());
            } catch (IOException e) {
                throw new RuntimeException(
                    "Unable to write the Arbiter data file " +
                    dataFile.getAbsolutePath());
            }
        }
    }

    public synchronized void close() {
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException ignore) {
            } finally {
                raf = null;
            }
        }
    }

    public VLSN get() {
        return currentVLSN;
    }
}
