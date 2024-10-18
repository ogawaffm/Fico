package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.ImmediateBindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;

public class ImmediateDeleter<B> extends BeanWriter<B> implements Deleter<B> {

    public ImmediateDeleter(Connection connection, RowMapper rowMapper, String tableName) {
        super(rowMapper,
            new ImmediateBindVarWriter(connection, new RowMapperSql(rowMapper).getUpdateSql(tableName))
        );
    }

    @Override
    public void delete(B bean) {
        write(bean);
    }

    @Override
    void write(@NonNull B bean) {
        Object[] row = rowMapper.toRow(bean);
        bindVarWriter.write(row);
    }
}
