/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package com.sleepycat.je.log.entry;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Arrays;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.dbi.DatabaseId;
import com.sleepycat.je.dbi.DatabaseImpl;
import com.sleepycat.je.dbi.DupKeyData;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.LogEntryHeader;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.LogUtils;
import com.sleepycat.je.tree.Key;
import com.sleepycat.je.tree.LN;
import com.sleepycat.je.tree.VersionedLN;
import com.sleepycat.je.txn.Txn;
import com.sleepycat.je.utilint.DbLsn;
import com.sleepycat.je.utilint.VLSN;

/**
 * An LNLogEntry is the in-memory image of an LN logrec describing a write op
 * (insertion, update, or deletion) performed by a locker T on a record R.
 * T always locks R in exclusive (WRITE or WRITE_RANGE) mode before performing
 * any write ops on it, and it retains its exclusive lock on R until it
 * terminates (commits or aborts). (Non-transactional lockers can be viewed as
 * "simple" transactions that perform at most one write op, and then
 * immediately commit).
 *
 * On disk, an LN logrec contains :
 *
 * 1 <= version <= 5
 *
 *   LN data
 *   databaseid
 *   key
 *   abortLsn             -- if transactional
 *   abortKnownDeleted    -- if transactional
 *   txn id               -- if transactional
 *   prev LSN of same txn -- if transactional
 *
 * 6 <= versions <= 10 :
 *
 *   databaseid
 *   abortLsn             -- if transactional
 *   abortKnownDeleted    -- if transactional
 *   txn id               -- if transactional
 *   prev LSN of same txn -- if transactional
 *   record data
 *   record key
 *
 * 11 <= version :
 *
 *   databaseid
 *   abortLsn               -- if transactional
 *   1-byte flags
 *     abortKnownDeleted
 *     embeddedLN
 *     haveAbortKey
 *     haveAbortData
 *     haveAbortVLSN
 *   txn id                 -- if transactional
 *   prev LSN of same txn   -- if transactional
 *   record abort key       -- if haveAbortKey is true
 *   record abort data      -- if haveAbortData is true
 *   record abort vlsn      -- if haveAbortVLSN is true
 *   record data
 *   record key
 *
 * NOTE: LNLogEntry is sub-classed by NameLNLogentry, which adds some extra
 * fields after the record key.
 *
 * Before version 6, a non-full-item read of a log entry only retrieved
 * the node ID. After version 6, the database id, transaction id and node ID
 * are all available.
 */
public class LNLogEntry<T extends LN> extends BaseReplicableEntry<T> {

    private static final byte ABORT_KD_MASK = (byte) 1;
    private static final byte EMBEDDED_LN_MASK = (byte) 2;
    private static final byte HAVE_ABORT_KEY_MASK = (byte) 4;
    private static final byte HAVE_ABORT_DATA_MASK = (byte) 8;
    private static final byte HAVE_ABORT_VLSN_MASK = (byte) 16;

    /**
     * Used for computing the minimum log space used by an LNLogEntry.
     */
    public static final int MIN_LOG_SIZE = 1 + // DatabaseId
                                           1 + // LN with zero-length data
                                           LogEntryHeader.MIN_HEADER_SIZE;

    /**
     * The log version when the most recent format change for this entry was
     * made (including any changes to the format of the underlying LN and other
     * loggables).  Note that this format change does not apply to the form of
     * LNs serialized for replication, which continue to be compatible with
     * format 8.
     *
     * @see #getLastFormatChange
     */
    public static final int LAST_FORMAT_CHANGE = 11;

    /**
     * The log version when the second most recent format change for this entry
     * was made. We need to be able to serialize LNLogEntry's into this format
     * to support the case where, during an upgrade, a replica node uses a log
     * version that is one earlier than the most recent log version.
     */
    public static final int PREV_FORMAT_CHANGE = 8;

    /*
     * Persistent fields.
     */

    /*
     * The id of the DB containing the record.
     */
    private DatabaseId dbId;

    /*
     * The Txn performing the write op. It is null for non-transactional DBs.
     * On disk we store only the txn id and the LSN of the previous logrec
     * (if any) generated by this txn.
     */
    private Txn txn;

    /*
     * The LSN of the record's "abort" version, i.e., the version to revert to
     * if this logrec must be undone as a result of a txn abort. It is set to
     * the most recent version before the record was locked by the locker T
     * associated with this logrec. Because T locks R before it writes it, the
     * abort version is always a committed version.
     *
     * It is null for non-transactional lockers, because such lockers never
     * abort.
     */
    private long abortLsn = DbLsn.NULL_LSN;

    /*
     * Whether the record's abort version was a deleted version or not.
     */
    private boolean abortKnownDeleted;

    /*
     * The key of the record's abort version, if haveAbortKey is true;
     * null otherwise.
     */
    private byte[] abortKey = null;

    /*
     * The data portion of the record's abort version, if haveAbortData is
     * true; null otherwise.
     */
    private byte[] abortData = null;

    /*
     * The VLSN of the record's abort version, if haveAbortVLSN is true;
     * NULL_VLSN otherwise.
     */
    private long abortVLSN = VLSN.NULL_VLSN_SEQUENCE;

    /*
     * True if the logrec stores an abort key, which is the case only if
     * (a) this is a transactional logrec, (b) the record's abort version
     * was embedded in the BIN, and (c) the DB allows key updates.
     */
    private boolean haveAbortKey;

    /*
     * True if the logrec stores abort data, which is the case only if
     * (a) this is a transactional logrec and (b) the record's abort
     * version was embedded in the BIN.
     */
    private boolean haveAbortData;

    /*
     * True if the logrec stores and abort VLSN, which is the case only if
     * (a) this is a transactional logrec (b) the record's abort version
     * was embedded in the BIN, and (c) VLSN caching is enabled.
     */
    private boolean haveAbortVLSN;

    /*
     * Whether, after the write op described by this logrec, the record is
     * embedded in the BIN or not.
     */
    private boolean embeddedLN;

    /*
     * The LN storing the record's data, after the write op described by this
     * logrec. The ln has a null data value if the write op is a deletion. For
     * replicated DBs, the ln contains the record's VLSN as well.
     */
    private LN ln;

    /*
     * The value of the record's key, after the write op described by this
     * logrec.
     */
    private byte[] key;

    /*
     * Transient fields.
     */

    /* Transient field for duplicates conversion and user key/data methods. */
    enum DupStatus { UNKNOWN, NEED_CONVERSION, DUP_DB, NOT_DUP_DB }
    private DupStatus dupStatus;

    /* For construction of VersionedLN, when VLSN is preserved. */
    private final Constructor<VersionedLN> versionedLNConstructor;

    /**
     * Creates an instance to read an entry.
     *
     * @param <T> the type of the contained LN
     * @param cls the class of the contained LN
     * @return the log entry
     */
    public static <T extends LN> LNLogEntry<T> create(final Class<T> cls) {
        return new LNLogEntry<T>(cls);
    }

    /* Constructor to read an entry. */
    LNLogEntry(final Class<T> cls) {
        super(cls);
        if (cls == LN.class) {
            versionedLNConstructor = getNoArgsConstructor(VersionedLN.class);
        } else {
            versionedLNConstructor = null;
        }
    }

    /* Constructor to write an entry. */
    public LNLogEntry(
        LogEntryType entryType,
        DatabaseId dbId,
        Txn txn,
        long abortLsn,
        boolean abortKD,
        byte[] abortKey,
        byte[] abortData,
        long abortVLSN,
        byte[] key,
        T ln,
        boolean embeddedLN) {

        setLogType(entryType);

        this.dbId = dbId;

        this.txn = txn;
        this.abortLsn = abortLsn;
        this.abortKnownDeleted = abortKD;
        this.abortKey = abortKey;
        this.abortData = abortData;
        this.abortVLSN = abortVLSN;

        this.haveAbortKey = (abortKey != null);
        this.haveAbortData = (abortData != null);
        this.haveAbortVLSN = !VLSN.isNull(abortVLSN);

        this.embeddedLN = embeddedLN;
        this.key = key;
        this.ln = ln;

        versionedLNConstructor = null;

        /* A txn should only be provided for transactional entry types. */
        assert(entryType.isTransactional() == (txn != null));
    }

    private void reset() {
        txn = null;
        abortLsn = DbLsn.NULL_LSN;
        abortKey = null;
        abortData = null;
        abortVLSN = VLSN.NULL_VLSN_SEQUENCE;
        ln = null;
        key = null;
    }

    @Override
    public void readEntry(
        EnvironmentImpl envImpl,
        LogEntryHeader header,
        ByteBuffer entryBuffer) {

        /* Subclasses must call readBaseLNEntry. */
        assert getClass() == LNLogEntry.class;

        /*
         * Prior to version 8, the optimization to omit the key size was
         * mistakenly not applied to internal LN types such as FileSummaryLN
         * and MapLN, and was only applied to user LN types.  The optimization
         * should be applicable whenever LNLogEntry is not subclassed to add
         * additional fields. [#18055]
         */
        final boolean keyIsLastSerializedField =
            header.getVersion() >= 8 || entryType.isUserLNType();

        readBaseLNEntry(envImpl, header, entryBuffer,
                        keyIsLastSerializedField);
    }

    /**
     * Method shared by LNLogEntry subclasses.
     *
     * @param keyIsLastSerializedField specifies whether the key length can be
     * omitted because the key is the last field.  This should be false when
     * an LNLogEntry subclass adds fields to the serialized format.
     */
    final void readBaseLNEntry(
        EnvironmentImpl envImpl,
        LogEntryHeader header,
        ByteBuffer entryBuffer,
        boolean keyIsLastSerializedField) {

        reset();

        int logVersion = header.getVersion();
        boolean unpacked = (logVersion < 6);
        int recStartPosition = entryBuffer.position();

        /*
         * For log version 6 and above we store the key last so that we can
         * avoid storing the key size. Instead, we derive it from the LN size
         * and the total entry size. The DatabaseId is also packed.
         */
        if (logVersion < 6) {
            /* LN is first for log versions prior to 6. */
            ln = newLNInstance(envImpl);
            ln.readFromLog(entryBuffer, logVersion);
        }

        /* DatabaseImpl Id. */
        dbId = new DatabaseId();
        dbId.readFromLog(entryBuffer, logVersion);

        /* Key. */
        if (logVersion < 6) {
            key = LogUtils.readByteArray(entryBuffer, true/*unpacked*/);
        }

        byte flags = 0;

        if (entryType.isTransactional()) {

            /*
             * AbortLsn. If it was a marker LSN that was used to fill in a
             * create, mark it null.
             */
            abortLsn = LogUtils.readLong(entryBuffer, unpacked);
            if (DbLsn.getFileNumber(abortLsn) ==
                DbLsn.getFileNumber(DbLsn.NULL_LSN)) {
                abortLsn = DbLsn.NULL_LSN;
            }

            flags = entryBuffer.get();

            /* txn id and prev LSN by same txn. */
            txn = new Txn();
            txn.readFromLog(entryBuffer, logVersion);

        } else if (logVersion >= 11) {
            flags = entryBuffer.get();
        }

        embeddedLN = ((flags & EMBEDDED_LN_MASK) != 0 ? true : false);
        abortKnownDeleted = ((flags & ABORT_KD_MASK) != 0 ? true : false);
        haveAbortKey = ((flags & HAVE_ABORT_KEY_MASK) != 0 ? true : false);
        haveAbortData = ((flags & HAVE_ABORT_DATA_MASK) != 0 ? true : false);
        haveAbortVLSN = ((flags & HAVE_ABORT_VLSN_MASK) != 0 ? true : false);

        if (logVersion >= 11) {
            if (haveAbortKey) {
                abortKey = LogUtils.readByteArray(entryBuffer, false);
            }
            if (haveAbortData) {
                abortData = LogUtils.readByteArray(entryBuffer, false);
            }
            if (haveAbortVLSN) {
                abortVLSN = LogUtils.readPackedLong(entryBuffer);
            }
        }

        if (logVersion >= 6) {

            ln = newLNInstance(envImpl);
            ln.readFromLog(entryBuffer, logVersion);

            int keySize;
            if (keyIsLastSerializedField) {
                int bytesWritten = entryBuffer.position() - recStartPosition;
                keySize = header.getItemSize() - bytesWritten;
            } else {
                keySize = LogUtils.readPackedInt(entryBuffer);
            }
            key = LogUtils.readBytesNoLength(entryBuffer, keySize);
        }

        /* Save transient fields after read. */

        if (header.getVLSN() != null) {
            ln.setVLSNSequence(header.getVLSN().getSequence());
        }

        /* Dup conversion will be done by postFetchInit. */
        dupStatus =
            (logVersion < 8) ? DupStatus.NEED_CONVERSION : DupStatus.UNKNOWN;
    }

    /**
     * newLNInstance usually returns exactly the type of LN of the type that
     * was contained in in the log. For example, if a LNLogEntry holds a MapLN,
     * newLNInstance will return that MapLN. There is one extra possibility for
     * vanilla (data record) LNs. In that case, this method may either return a
     * LN or a generated type, the VersionedLN, which adds the vlsn information
     * from the log header to the LN object.
     */
    LN newLNInstance(EnvironmentImpl envImpl) {
        if (versionedLNConstructor != null && envImpl.getPreserveVLSN()) {
            return newInstanceOfType(versionedLNConstructor);
        }
        return newInstanceOfType();
    }

    @Override
    public StringBuilder dumpEntry(StringBuilder sb, boolean verbose) {

        dbId.dumpLog(sb, verbose);

        ln.dumpKey(sb, key);
        ln.dumpLog(sb, verbose);

        sb.append("<embeddedLN val=\"");
        sb.append(embeddedLN);
        sb.append("\"/>");

        if (entryType.isTransactional()) {

            txn.dumpLog(sb, verbose);

            sb.append("<abortLSN val=\"");
            sb.append(DbLsn.getNoFormatString(abortLsn));
            sb.append("\"/>");

            sb.append("<abortKD val=\"");
            sb.append(abortKnownDeleted ? "true" : "false");
            sb.append("\"/>");

            if (haveAbortKey) {
                sb.append(Key.dumpString(abortKey, "abortKey", 0));
            }
            if (haveAbortData) {
                sb.append(Key.dumpString(abortData, "abortData", 0));
            }
            if (haveAbortVLSN) {
                sb.append("<abortVLSN v=\"");
                sb.append(abortVLSN);
                sb.append("\"/>");
            }
        }

        return sb;
    }

    @Override
    public void dumpRep(StringBuilder sb) {
        if (entryType.isTransactional()) {
            sb.append(" txn=").append(txn.getId());
        }
    }

    @Override
    public LN getMainItem() {
        return ln;
    }

    @Override
    public long getTransactionId() {
        if (entryType.isTransactional()) {
            return txn.getId();
        }
        return 0;
    }

    /*
     * Writing support.
     */

    @Override
    public int getLastFormatChange() {
        return LAST_FORMAT_CHANGE;
    }

    private int getPrevFormatChange() {
        return PREV_FORMAT_CHANGE;
    }


    @Override
    public int getSize() {

        /* Subclasses must call getBaseLNEntrySize. */
        assert getClass() == LNLogEntry.class;

        return getBaseLNEntrySize(LogEntryType.LOG_VERSION, true, false);
    }

    @Override
    public int getSize(int logVersion) {

        assert getClass() == LNLogEntry.class;

        return getBaseLNEntrySize(logVersion, true, true);
    }


    /**
     * Method shared by LNLogEntry subclasses.
     *
     * @param keyIsLastSerializedField specifies whether the key length can be
     * omitted because the key is the last field.  This should be false when
     * an LNLogEntry subclass adds fields to the serialized format.
     */
    final int getBaseLNEntrySize(
        int logVersion,
        boolean keyIsLastSerializedField,
        boolean forReplication) {

        int prevFormatChange = getPrevFormatChange();

        if (logVersion < prevFormatChange) {
            throw new IllegalArgumentException(
                "The requested log version, " + logVersion +
                ", is older than the previous format change, " +
                prevFormatChange + ", for class " + getClass().getName());
        }

        int len = key.length;
        int size = ln.getLogSize() + dbId.getLogSize() + len;

        if (!keyIsLastSerializedField) {
            size += LogUtils.getPackedIntLogSize(len);
        }

        if (entryType.isTransactional()) {
            size += LogUtils.getPackedLongLogSize(abortLsn);
            size += txn.getLogSize();
            size++;   // 1-byte flags
        } else if (!forReplication) {
            size++;   // 1-byte flags
        }

        if (!forReplication) {
            if (haveAbortKey) {
                size += LogUtils.getByteArrayLogSize(abortKey);
            }
            if (haveAbortData) {
                size += LogUtils.getByteArrayLogSize(abortData);
            }
            if (haveAbortVLSN) {
                size += LogUtils.getPackedLongLogSize(abortVLSN);
            }
        }

        return size;
    }

    @Override
    public void writeEntry(final ByteBuffer destBuffer) {

        writeBaseLNEntry(destBuffer, LogEntryType.LOG_VERSION, true, false);
    }


    @Override
    public void writeEntry(final ByteBuffer destBuffer, final int logVersion) {

        /* Subclasses must call writeBaseLNEntry. */
        assert getClass() == LNLogEntry.class;

        writeBaseLNEntry(destBuffer, logVersion, true, true);
    }

    /**
     * Method shared by LNLogEntry subclasses.
     *
     * @param keyIsLastSerializedField specifies whether the key length can be
     * omitted because the key is the last field.  This should be false when
     * an LNLogEntry subclass adds fields to the serialized format.
     */
    final void writeBaseLNEntry(
        final ByteBuffer destBuffer,
        final int logVersion,
        final boolean keyIsLastSerializedField,
        boolean forReplication) {

        int prevFormatChange = getPrevFormatChange();

        if (logVersion < prevFormatChange) {
            throw new IllegalArgumentException(
                "The requested log version, " + logVersion +
                ", is older than the previous format change, " +
                prevFormatChange + ", for class " + getClass().getName());
        }

        assert ln.getLastFormatChange() <= LAST_FORMAT_CHANGE &&
            dbId.getLastFormatChange() <= LAST_FORMAT_CHANGE
            : "Format of loggable newer than format of entry";

        dbId.writeToLog(destBuffer, logVersion);

        byte flags = 0;

        if (!forReplication) {
            if (embeddedLN) {
                flags |= EMBEDDED_LN_MASK;
            }
            if (haveAbortKey) {
                flags |= HAVE_ABORT_KEY_MASK;
            }
            if (haveAbortData) {
                flags |= HAVE_ABORT_DATA_MASK;
            }
            if (haveAbortVLSN) {
                flags |= HAVE_ABORT_VLSN_MASK;
            }
        }

        if (entryType.isTransactional()) {

            LogUtils.writePackedLong(destBuffer, abortLsn);

            if (abortKnownDeleted) {
                flags |= ABORT_KD_MASK;
            }

            destBuffer.put(flags);

            assert txn.getLastFormatChange() <= LAST_FORMAT_CHANGE
                : "Format of loggable newer than format of entry";

            txn.writeToLog(destBuffer, logVersion);

        } else if (!forReplication) {
            destBuffer.put(flags);

        } else {
            assert(false);
            destBuffer.put(flags);
        }

        if (!forReplication) {
            if (haveAbortKey) {
                LogUtils.writeByteArray(destBuffer, abortKey);
            }
            if (haveAbortData) {
                LogUtils.writeByteArray(destBuffer, abortData);
            }
            if (haveAbortVLSN) {
                LogUtils.writePackedLong(destBuffer, abortVLSN);
            }
        }

        ln.writeToLog(destBuffer, logVersion);

        if (!keyIsLastSerializedField) {
            LogUtils.writePackedInt(destBuffer, key.length);
        }
        LogUtils.writeBytesNoLength(destBuffer, key);
    }

    @Override
    public boolean isImmediatelyObsolete(DatabaseImpl dbImpl) {
        return (ln.isDeleted() ||
                embeddedLN ||
                dbImpl.isLNImmediatelyObsolete());
    }

    @Override
    public boolean isDeleted() {
        return ln.isDeleted();
    }

    /**
     * For LN entries, we need to record the latest LSN for that node with the
     * owning transaction, within the protection of the log latch. This is a
     * callback for the log manager to do that recording.
     */
    @Override
    public void postLogWork(
        LogEntryHeader header,
        long justLoggedLsn,
        VLSN vlsn) {

        if (entryType.isTransactional()) {
            txn.addLogInfo(justLoggedLsn);
        }

        /* Save transient fields after write. */
        if (vlsn != null) {
            ln.setVLSNSequence(vlsn.getSequence());
        }
    }

    @Override
    public void postFetchInit(DatabaseImpl dbImpl) {
        postFetchInit(dbImpl.getSortedDuplicates());
    }

    /**
     * Converts the key/data for old format LNs in a duplicates DB.
     *
     * This method MUST be called before calling any of the following methods:
     *  getLN
     *  getKey
     *  getUserKeyData
     */
    public void postFetchInit(boolean isDupDb) {

        final boolean needConversion =
            (dupStatus == DupStatus.NEED_CONVERSION);

        dupStatus = isDupDb ? DupStatus.DUP_DB : DupStatus.NOT_DUP_DB;

        /* Do not convert more than once. */
        if (!needConversion) {
            return;
        }

        /* Nothing to convert for non-duplicates DB. */
        if (dupStatus == DupStatus.NOT_DUP_DB) {
            return;
        }

        key = combineDupKeyData();
    }

    /**
     * Combine old key and old LN's data into a new key, and set the LN's data
     * to empty.
     */
    byte[] combineDupKeyData() {
        assert !ln.isDeleted(); // DeletedLNLogEntry overrides this method.
        return DupKeyData.combine(key, ln.setEmpty());
    }

    /**
     * Translates two-part keys in duplicate DBs back to the original user
     * operation params.  postFetchInit must be called before calling this
     * method.
     */
    public void getUserKeyData(
        DatabaseEntry keyParam,
        DatabaseEntry dataParam) {

        requireKnownDupStatus();

        if (dupStatus == DupStatus.DUP_DB) {
            DupKeyData.split(new DatabaseEntry(key), keyParam, dataParam);
        } else {
            if (keyParam != null) {
                keyParam.setData(key);
            }
            if (dataParam != null) {
                dataParam.setData(ln.getData());
            }
        }
    }

    /*
     * Accessors.
     */
    public boolean isEmbeddedLN() {
        return embeddedLN;
    }

    public LN getLN() {
        requireKnownDupStatus();
        return ln;
    }

    public byte[] getKey() {
        requireKnownDupStatus();
        return key;
    }

    public byte[] getData() {
        return ln.getData();
    }

    public byte[] getEmbeddedData() {

        if (!isEmbeddedLN()) {
            return null;
        }

        if (ln.isDeleted()) {
            return Key.EMPTY_KEY;
        }

        return ln.getData();
    }

    private void requireKnownDupStatus() {
        if (dupStatus != DupStatus.DUP_DB &&
            dupStatus != DupStatus.NOT_DUP_DB) {
            throw EnvironmentFailureException.unexpectedState(
                "postFetchInit was not called");
        }
    }

    /**
     * This method is only used when the converted length is not needed, for
     * example by StatsFileReader.
     */
    public int getUnconvertedDataLength() {
        return ln.getData().length;
    }

    /**
     * This method is only used when the converted length is not needed, for
     * example by StatsFileReader.
     */
    public int getUnconvertedKeyLength() {
        return key.length;
    }

    @Override
    public DatabaseId getDbId() {
        return dbId;
    }

    public long getAbortLsn() {
        return abortLsn;
    }

    public boolean getAbortKnownDeleted() {
        return abortKnownDeleted;
    }

    public byte[] getAbortKey() {
        return abortKey;
    }

    public byte[] getAbortData() {
        return abortData;
    }

    public long getAbortVLSN() {
        return abortVLSN;
    }

    public Long getTxnId() {
        if (entryType.isTransactional()) {
            return Long.valueOf(txn.getId());
        }
        return null;
    }

    public Txn getUserTxn() {
        if (entryType.isTransactional()) {
            return txn;
        }
        return null;
    }

    @Override
    public boolean logicalEquals(LogEntry other) {
        if (!(other instanceof LNLogEntry)) {
            return false;
        }

        LNLogEntry<?> otherEntry = (LNLogEntry<?>) other;

        if (!dbId.logicalEquals(otherEntry.dbId)) {
            return false;
        }

        if (txn != null) {
            if (!txn.logicalEquals(otherEntry.txn)) {
                return false;
            }
        } else {
            if (otherEntry.txn != null) {
                return false;
            }
        }

        if (!Arrays.equals(key, otherEntry.key)) {
            return false;
        }

        if (!ln.logicalEquals(otherEntry.ln)) {
            return false;
        }

        return true;
    }
}
