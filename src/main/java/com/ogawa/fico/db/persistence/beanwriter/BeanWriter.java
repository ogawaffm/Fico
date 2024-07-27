package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.BindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;

/**
 * Write is a neutral term for creating or updating a row for a bean in a database.
 *
 * @param <B>
 */
public abstract class BeanWriter<B> implements AutoCloseable {

    final RowMapper<B> rowMapper;
    final BindVarWriter bindVarWriter;

    BeanWriter(RowMapper<B> rowMapper, BindVarWriter bindVarWriter) {
        this.rowMapper = rowMapper;
        this.bindVarWriter = bindVarWriter;
    }

    void write(@NonNull B bean) {
        Object[] var = rowMapper.toRow(bean);
        bindVarWriter.write(var);
    }

    @Override
    public void close() {
        try {
            bindVarWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
