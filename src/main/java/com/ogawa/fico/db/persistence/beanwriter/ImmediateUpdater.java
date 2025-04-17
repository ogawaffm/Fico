package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.ImmediateBindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import lombok.NonNull;

public class ImmediateUpdater<B> extends BeanWriter<B> implements Updater<B> {

    public ImmediateUpdater(Connection connection, RowMapper rowMapper, String tableName) {
        super(rowMapper,
            new ImmediateBindVarWriter(connection, new RowMapperSql(rowMapper).getUpdateSql(tableName))
        );
    }

    @Override
    public void update(B bean) {
        write(bean);
    }

    @Override
    void write(@NonNull B bean) {
        Object[] row = rowMapper.toRow(bean);
        bindVarWriter.write(row);
    }
}
