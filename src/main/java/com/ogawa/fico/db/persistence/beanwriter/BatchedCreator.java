package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.BatchedBindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;

public class BatchedCreator<B> extends BeanWriter<B> implements Creator<B> {

    public BatchedCreator(Connection connection, String tableName, int batchSize, RowMapper rowMapper) {
        super(rowMapper,
            new BatchedBindVarWriter(
                connection,
                new RowMapperSql(rowMapper).getInsertSql(tableName),
                batchSize,
                true)
        );
    }

    public B create(B bean) {
        write(bean);
        return bean;
    }

}
