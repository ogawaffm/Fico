package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.BatchedBindVarWriter;
import com.ogawa.fico.db.persistence.bindvarwriter.BindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import lombok.NonNull;

public class BatchedUpdater<B> extends BeanWriter<B> implements Updater<B> {

    public BatchedUpdater(Connection connection, String tableName, int batchSize, RowMapper rowMapper) {
        super(rowMapper,
            new BatchedBindVarWriter(
                connection,
                new RowMapperSql(rowMapper).getUpdateSql(tableName),
                batchSize,
                true)
        );
    }

    BatchedUpdater(RowMapper rowMapper, BindVarWriter bindVarWriter) {
        super(rowMapper, bindVarWriter);
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
