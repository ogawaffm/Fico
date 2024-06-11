package com.ogawa.fico.db;

import com.ogawa.fico.application.FileBean;
import com.ogawa.fico.application.FileBeanFactory;
import java.sql.Connection;
import lombok.NonNull;

public class FileRowReader extends FileRowBatchAccess {

    private final static String SELECT_FROM_FILE =
        "SELECT FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS, "
            + "CHECKSUM, CALC_STARTED, CALC_FINISHED\n"
            + "FROM FILE\n";

    public FileRowReader(@NonNull Connection connection, int scanId) {

        super(connection, scanId, SELECT_FROM_FILE, 1000, true);

    }

    static public FileBean read(@NonNull Object[] row) {

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

}
