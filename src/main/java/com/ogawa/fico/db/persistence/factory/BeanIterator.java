package com.ogawa.fico.db.persistence.factory;

import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import com.ogawa.fico.jdbc.RowIterator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class BeanIterator<B> implements Iterator<B>, AutoCloseable {

    private final PreparedStatement preparedStatement;

    private final RowIterator rowIterator;

    private final RowMapper<B> rowMapper;

    static private final String SELECT_MARKED_DUPLICATE_CANDIDATES = "SelectMarkedDuplicateCandidates";

    public BeanIterator(Connection connection, RowMapper<B> rowMapper, String sql, int fetchSize) {

        this.rowMapper = rowMapper;

        String wrappedSql = new RowMapperSql(rowMapper).getSelectFromSubSelectSql(sql);

        try {
            this.preparedStatement = connection.prepareStatement(
                wrappedSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY
            );
            preparedStatement.setFetchSize(fetchSize);
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
    public B next() {
        Object[] row = rowIterator.next();
        return rowMapper.toObject(row);
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
