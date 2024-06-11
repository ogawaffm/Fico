package com.ogawa.fico.db;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class FileIdSequenceFactory {

    /**
     * Map of scanId to max file id for the scanId
     */
    static final private Map<Integer, AtomicLong> scanIdFileId = new ConcurrentHashMap<>();

    static public Sequence getFileIdSequence(Connection connection, int scanId) {

        AtomicLong fileId = scanIdFileId.get(scanId);

        // no file id for the current scanId?
        if (fileId == null) {
            // getFromSupplier max file id for the current scanId from the database
            long maxUsedFileId = getMaxUsedFileId(connection, scanId);
            // was there no file for the current scanId?
            if (maxUsedFileId == 0) {
                // then start with relative 0 for the current scanId
                maxUsedFileId = getFileIdZero(scanId);
            }
            // plus a new entry for the current scanId
            fileId = scanIdFileId.putIfAbsent(scanId, new AtomicLong(maxUsedFileId));
            // if there was already an entry for the current scanId (because meanwhile another thread added an entry)
            if (fileId == null) {
                // then invoke the existing entry (else stick with the new entry)
                fileId = scanIdFileId.get(scanId);
            }
        }
        return new Sequence(fileId);

    }

    /**
     * Returns the max file id for the current scanId or 0 if no file exists for the current scanId
     *
     * @param connection
     * @return max file id for the current scanId or 0 if no file exists for the current scanId
     */
    static private long getMaxUsedFileId(Connection connection, int scanId) {
        return Util.getValue(connection,
            "SELECT COALESCE(MAX(FILE_ID), 0) FROM FILE WHERE SCAN_ID = " + scanId,
            0L);
    }

    /**
     * Returns the relative zero file id for the scanId
     *
     * @param scanId
     * @return
     */
    static private long getFileIdZero(int scanId) {
        // long 9.223.372.036.854.775.807 (~ 19 digits)
        // => 922.337 + 2.036.854.775.807 scanId (~ 6 digits) + fileId (~ 13 digits)
        return (scanId - 1) * 10_000_000_000_000L;
    }

}
