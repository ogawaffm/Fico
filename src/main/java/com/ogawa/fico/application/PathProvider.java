package com.ogawa.fico.application;

import com.ogawa.fico.jdbc.RowIterator;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class PathProvider implements Iterator<Path>, AutoCloseable {

    private final PreparedStatement preparedStatement;

    private final RowIterator rowIterator;

    PathProvider(Connection connection, String sql) {
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
    public Path next() {
        Object[] row = rowIterator.next();
        return Path.of(row[4].toString(), row[3].toString());
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
