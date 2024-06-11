package com.ogawa.fico.db;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;

public class FileRowWriter extends FileRowBatchAccess {

    private final static String INSERT_INTO_FILE =
        "INSERT INTO FILE (FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS, " +
            "CHECKSUM, CALC_STARTED, CALC_FINISHED)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static String UPDATE_FILE_CHECKSUM =
        "UPDATE FILE SET "
            + "FILE_ID = ?, "
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

    public FileRowWriter(Connection connection, int scanId) {

        super(connection, scanId, INSERT_INTO_FILE, 1000, true);

        fileIdSequence = FileIdSequenceFactory.getFileIdSequence(connection, scanId);

    }

    public long create(Long dirId, String path, String name, long size, Date lastWriteAccess) {

        long newFileId = fileIdSequence.next();

        try {

            batchRowWriter.setRow(new Object[]{
                newFileId,
                scanId,
                dirId == null ? -1 : dirId,
                path,
                name == null ? "" : name,
                size,
                new Timestamp(lastWriteAccess.getTime())
            });

            return newFileId;

        } catch (Exception e) {
            System.out.println(
                "fileId: " + newFileId + "dirId: " + dirId + " path: " + path + " name: " + name + " size: " + size +
                    " lastWriteAccess: " + lastWriteAccess);
            throw new RuntimeException(e);
        }

    }

}
