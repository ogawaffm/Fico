package com.ogawa.fico.db.persistence.beanreader;

import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.Connection;
import java.sql.SQLException;

public class StaticBeanReader<B> extends BaseBeanReader<B> {

    public StaticBeanReader(Connection connection, String selectSql, int fetchSize, RowMapper<B> rowMapper) {
        super(connection, selectSql, fetchSize, rowMapper);
        try {
            open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public StaticBeanReader(Connection connection, String sqlWithBindVariables, int fetchSize, RowMapper<B> rowMapper,
        Object[] bindVars) {
        super(connection, sqlWithBindVariables, fetchSize, rowMapper);
        try {
            JdbcTransferor.setBindVars(preparedStatement, bindVars);
            open();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
