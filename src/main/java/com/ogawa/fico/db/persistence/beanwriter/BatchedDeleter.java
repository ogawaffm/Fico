package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.BatchedBindVarWriter;
import com.ogawa.fico.db.persistence.bindvarwriter.BindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import lombok.NonNull;

public class BatchedDeleter<B> extends BeanWriter<B> implements Deleter<B> {

    public BatchedDeleter(Connection connection, String tableName, int batchSize, RowMapper rowMapper) {
        this(rowMapper,
            new BatchedBindVarWriter(
                connection,
                new RowMapperSql(rowMapper).getUpdateSql(tableName),
                batchSize,
                true)
        );
    }

    BatchedDeleter(RowMapper rowMapper, BindVarWriter bindVarWriter) {
        super(rowMapper, bindVarWriter);
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
