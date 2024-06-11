package com.ogawa.fico.db;

import com.ogawa.fico.application.FileBean;
import com.ogawa.fico.application.FileBeanFactory;
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

    public FileRowUpdater(@NonNull Connection connection, int scanId) {

        super(connection, scanId, UPDATE_FILE_CHECKSUM, 1000, true);

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

}
