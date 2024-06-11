package com.ogawa.fico.application;

import static com.ogawa.fico.db.FileRowMapper.createFromRow;

import com.ogawa.fico.jdbc.RowIterator;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Iterator;

public class FileBeanProvider implements Iterator<FileBean>, AutoCloseable {

    private final PreparedStatement preparedStatement;

    private final RowIterator rowIterator;

    FileBeanProvider(Connection connection, String sql) {
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
    public FileBean next() {
        Object[] row = rowIterator.next();
        return createFromRow(row);
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
