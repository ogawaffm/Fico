package com.ogawa.fico.db.persistence.beanreader;

import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.Connection;
import java.sql.SQLException;

public class SeekingBeanReader<B> extends BaseBeanReader<B> implements AutoCloseable {

    public SeekingBeanReader(Connection connection, String sqlWithBindVariables, int fetchSize,
        RowMapper<B> rowMapper) {
        super(connection, sqlWithBindVariables, fetchSize, rowMapper);
    }

    public void seek(Object[] seekKey) {
        try {
            JdbcTransferor.setBindVars(preparedStatement, seekKey);
            open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
