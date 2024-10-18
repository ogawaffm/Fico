package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.BatchedBindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import lombok.NonNull;

public class BatchedCreator<B> extends BeanWriter<B> implements Creator<B> {

    private B beanCache[];
    private int beanCacheIndex = 0;

    public BatchedCreator(Connection connection, String tableName, int batchSize, RowMapper rowMapper) {
        super(rowMapper,
            new BatchedBindVarWriter(
                connection,
                new RowMapperSql(rowMapper).getInsertSql(tableName),
                batchSize,
                true)
        );

        beanCache = (B[]) new Object[batchSize];
        ((BatchedBindVarWriter) bindVarWriter).setGeneratedKeyHandler(this::handleGeneratedKey);
    }

    public void create(B bean) {
        write(bean);
    }

    @Override
    void write(@NonNull B bean) {
        Object[] row = rowMapper.toInsertRow(bean);
        bindVarWriter.write(row);
    }

    void handleGeneratedKey(Object[] generatedKey) {

        rowMapper.setPrimaryKeyValues(beanCache[beanCacheIndex], generatedKey);

        beanCacheIndex++;

        if (beanCacheIndex >= beanCache.length) {
            beanCacheIndex = 0;
        }
    }

    @Override
    public void close() {
        beanCache = null;
        super.close();
    }

}
