package com.ogawa.fico.db.persistence.beanreader;

import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;

public class ByPrimaryKeySeekingBeanReader<B> extends BaseBeanReader<B> implements AutoCloseable {

    public ByPrimaryKeySeekingBeanReader(Connection connection, String sqlWithBindVariables, int fetchSize,
        RowMapper<B> rowMapper) {
        super(connection, sqlWithBindVariables, fetchSize, rowMapper);
    }

    public void seek(@NonNull B bean) throws SQLException {
        Object[] primaryKey = rowMapper.getPrimaryKeyValues(bean);
        seek(primaryKey);
    }

    void seek(@NonNull Object[] seekKey) throws SQLException {
        JdbcTransferor.setBindVars(preparedStatement, seekKey);
        open();
    }

}
