package com.ogawa.fico.db;

import com.ogawa.fico.application.FileBean;
import java.sql.Connection;
import lombok.NonNull;

public class FileRowCreator extends FileRowBatchAccess {

    private final static String INSERT_INTO_FILE =
        "INSERT INTO FILE (FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS, "
            + "CHECKSUM, CALC_STARTED, CALC_FINISHED)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private Sequence fileIdSequence;

    public FileRowCreator(@NonNull Connection connection, int scanId) {

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

}
