package com.ogawa.fico.db;

import com.ogawa.fico.application.FileBean;
import java.sql.Connection;
import lombok.NonNull;

public class FileRowUpdater extends FileRowBatchAccess {

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

    public FileRowUpdater(@NonNull Connection connection) {

        super(connection, UPDATE_FILE_CHECKSUM, 1000, true);

    }

    public void update(@NonNull FileBean fileBean) {
        try {
            batchRowWriter.setRow(new Object[]{
                fileBean.getScanId(),
                fileBean.getDirId(),
                Util.getDirectoryName(fileBean.getFullFileName()),
                Util.getFilename(fileBean.getFullFileName()),
                fileBean.getSize(),
                Util.toTimestamp(fileBean.getLastWriteAccess()),
                fileBean.getChecksum(),
                Util.toTimestamp(fileBean.getCalcStarted()),
                Util.toTimestamp(fileBean.getCalcFinished()),

                fileBean.getFileId(),

            });
        } catch (Exception e) {
            System.err.println(fileBean);
            throw new RuntimeException(e);
        }
    }

}
