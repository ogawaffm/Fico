package com.ogawa.fico.db;

import com.ogawa.fico.application.FileBean;
import java.sql.Connection;
import lombok.NonNull;

public class FileRowCreator extends FileRowBatchAccess {

    private final static String INSERT_INTO_FILE =
        "INSERT INTO FILE (FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS, "
            + "CHECKSUM, CALC_STARTED, CALC_FINISHED)\n"
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public FileRowCreator(@NonNull Connection connection) {

        super(connection, INSERT_INTO_FILE, 1000, true);

    }

    public void create(@NonNull FileBean fileBean) {

        try {

            batchRowWriter.setRow(new Object[]{

                fileBean.getFileId(),
                fileBean.getScanId(),
                fileBean.getDirId(),
                Util.getDirectoryName(fileBean.getFullFileName()),
                Util.getFilename(fileBean.getFullFileName()),
                fileBean.getSize(),
                Util.toTimestamp(fileBean.getLastWriteAccess()),
                fileBean.getChecksum(),
                Util.toTimestamp(fileBean.getCalcStarted()),
                Util.toTimestamp(fileBean.getCalcFinished())
            });

        } catch (Exception e) {
            System.err.println(fileBean);
            throw new RuntimeException(e);
        }

    }

}
