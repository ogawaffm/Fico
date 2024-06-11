package com.ogawa.fico.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public abstract class FileRowBatchAccess {

    final protected Connection connection;
    final protected int scanId;

    final protected BatchRowWriter batchRowWriter;

    protected FileRowBatchAccess(Connection connection, int scanId,
        String sqlWithBindVariables, int batchSize, boolean commitAfterBatch) {

        this.connection = connection;
        this.scanId = scanId;

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlWithBindVariables);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        batchRowWriter = new BatchRowWriter(preparedStatement, batchSize, commitAfterBatch);

    }

    public void close() {
        batchRowWriter.close();
    }

    /**
     * Map of scanId to max file id for the scanId
     */
    static final private Map<Integer, AtomicLong> scanIdFileId = new ConcurrentHashMap<>();


    static String getDirectoryName(Path path) {
        if (path == null || path.getParent() == null) {
            return "";
        } else {
            return path.getParent().toString();
        }
    }

    static String getFilename(Path path) {
        if (path == null || path.getFileName() == null) {
            return "";
        } else {
            return path.getFileName().toString();
        }
    }

    static Path getFullPath(String path, String name) {
        if (path == null || name == null) {
            return null;
        } else {
            return Path.of(path, name);
        }
    }

    static Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return timestamp.toLocalDateTime();
        }
    }


}
