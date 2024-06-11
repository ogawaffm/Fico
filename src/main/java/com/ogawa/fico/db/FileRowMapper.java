package com.ogawa.fico.db;

import com.ogawa.fico.application.FileBean;
import com.ogawa.fico.application.FileBeanFactory;
import java.nio.file.Path;
import java.sql.Connection;
import lombok.NonNull;

public class FileRowMapper extends FileRowBatchAccess {

    private final static String INSERT_INTO_FILE =
        "INSERT INTO FILE (FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS, "
            + "CHECKSUM, CALC_STARTED, CALC_FINISHED)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static String UPDATE_FILE_CHECKSUM =
        "UPDATE FILE SET "
            + "SCAN_ID = ?, "
            + "DIR_ID = ?, "
            + "PATH = ?, "
            + "NAME = ?, "
            + "SIZE = ?, "
            + "LAST_WRITE_ACCESS = ?, "
            + "CHECKSUM = ?, "
            + "CALC_STARTED = ?, "
            + "CALC_FINISHED = ? "
            + "WHERE FILE_ID = ?";

    private Sequence fileIdSequence;

    public FileRowMapper(@NonNull Connection connection, int scanId) {

        super(connection, scanId, INSERT_INTO_FILE, 1000, true);

        fileIdSequence = FileIdSequenceFactory.getFileIdSequence(connection, scanId);

    }

    public long create(@NonNull FileBean fileBean) {

        long newFileId = fileIdSequence.next();
        fileBean.setFileId(newFileId);

        try {

            batchRowWriter.setRow(new Object[]{

                fileBean.getFileId(),
                fileBean.getScanId(),
                fileBean.getDirId(),
                getDirectoryName(fileBean.getFullFileName()),
                getFilename(fileBean.getFullFileName()),
                fileBean.getSize(),
                toTimestamp(fileBean.getLastWriteAccess()),
                fileBean.getChecksum(),
                toTimestamp(fileBean.getCalcStarted()),
                toTimestamp(fileBean.getCalcFinished())
            });

            return newFileId;

        } catch (Exception e) {
            System.err.println(fileBean);
            throw new RuntimeException(e);
        }

    }

    public void update(@NonNull FileBean fileBean) {
        try {
            batchRowWriter.setRow(new Object[]{
                fileBean.getScanId(),
                fileBean.getDirId(),
                getDirectoryName(fileBean.getFullFileName()),
                getFilename(fileBean.getFullFileName()),
                fileBean.getSize(),
                toTimestamp(fileBean.getLastWriteAccess()),
                fileBean.getChecksum(),
                toTimestamp(fileBean.getCalcStarted()),
                toTimestamp(fileBean.getCalcFinished()),

                fileBean.getFileId(),

            });
        } catch (Exception e) {
            System.err.println(fileBean);
            throw new RuntimeException(e);
        }
    }

    static public FileBean createFromRow(@NonNull Object[] row) {
        FileBean fileBean = FileBeanFactory.create(
            row[0] == null ? null : ((Number) row[0]).longValue(),
            row[1] == null ? null : ((Number) row[1]).longValue(),
            row[2] == null ? null : ((Number) row[2]).longValue(),
            getFullPath((String) row[3], (String) row[4]),
            row[5] == null ? null : ((Number) row[5]).longValue(),
            toLocalDateTime((java.sql.Timestamp) row[6]),
            (byte[]) row[7],
            toLocalDateTime((java.sql.Timestamp) row[8]),
            toLocalDateTime((java.sql.Timestamp) row[9])
        );
        return fileBean;

    }

    static String getDirectoryName(Path path) {
        if (path == null || path.getParent() == null) {
            return null;
        } else {
            return path.getParent().toString();
        }
    }

}
