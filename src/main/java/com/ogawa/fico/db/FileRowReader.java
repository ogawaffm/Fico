package com.ogawa.fico.db;

import static com.ogawa.fico.db.Util.getFullPath;
import static com.ogawa.fico.db.Util.toLocalDateTime;
import static com.ogawa.fico.jdbc.JdbcTransferor.resultSetToObjectArray;

import com.ogawa.fico.application.FileBean;
import com.ogawa.fico.application.FileBeanFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;

public class FileRowReader implements ResultSetRowReader {

    private final ResultSet resultSet;

    private final static String SELECT_FROM_FILE =
        "SELECT FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS, "
            + "CHECKSUM, CALC_STARTED, CALC_FINISHED\n"
            + "FROM FILE\n";

    public FileRowReader(@NonNull Connection connection, String sql) {

        try {
            resultSet = connection.prepareStatement(sql).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FileBean read(@NonNull Object[] row) {
        try {
            return createFromRow(getRow());
        } catch (SQLException e) {
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

    @Override
    public ResultSet getResultSet() {
        return resultSet;
    }
}
