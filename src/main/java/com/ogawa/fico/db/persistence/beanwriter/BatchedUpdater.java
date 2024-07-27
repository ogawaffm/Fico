package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.BatchedBindVarWriter;
import com.ogawa.fico.db.persistence.bindvarwriter.BindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;

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
    public B update(B bean) {
        write(bean);
        return bean;
    }

}
