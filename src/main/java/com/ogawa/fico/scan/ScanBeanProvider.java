package com.ogawa.fico.scan;

import com.ogawa.fico.db.persistence.rowmapper.ScanRowMapper;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.jdbc.RowIterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ScanBeanProvider implements Iterator<ScanBean>, AutoCloseable {

    private final PreparedStatement preparedStatement;

    private final RowIterator rowIterator;

    static private final String SELECT_MARKED_DUPLICATE_CANDIDATES = "SelectMarkedDuplicateCandidates";

    static private final ScanRowMapper scanRowMapper = new ScanRowMapper();

    public ScanBeanProvider(Connection connection, boolean markedFiledOnly) {

        String sql;

        if (markedFiledOnly) {
            sql = Util.getSql(SELECT_MARKED_DUPLICATE_CANDIDATES);
        } else {
            sql = "SELECT FILE_ID, SCAN_ID, DIR_ID, PATH, NAME, SIZE, "
                + "IS_DIR, FILES_CONTAINED, DIRS_CONTAINED, MODIFICATION_TIME, CREATION_TIME, "
                + "CHECKSUM, CALC_STARTED, CALC_FINISHED\n"
                + "FROM FILE\n";
        }

        try {
            this.preparedStatement = connection.prepareStatement(
                sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
            );
            this.rowIterator = new RowIterator(preparedStatement.executeQuery(), true, 0L);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasNext() {
        return rowIterator.hasNext();
    }

    @Override
    public ScanBean next() {
        Object[] row = rowIterator.next();
        return scanRowMapper.toObject(row);
    }

    public void close() {
        rowIterator.close();
        try {
            preparedStatement.close();
        } catch (SQLException cause) {
            throw new RuntimeException(cause);
        }
    }
}
