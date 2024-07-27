package com.ogawa.fico.db.persistence.beanwriter;

import com.ogawa.fico.db.persistence.bindvarwriter.ImmediateBindVarWriter;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import java.sql.SQLException;

public class ImmediateCreator<B> extends BeanWriter<B> implements Creator<B> {

    B beanCache;

    public ImmediateCreator(Connection connection, String tableName, RowMapper rowMapper) {
        super(rowMapper, new ImmediateBindVarWriter(connection, new RowMapperSql(rowMapper).getInsertSql(tableName)));
        ((ImmediateBindVarWriter) bindVarWriter).setGeneratedKeyHandler(this::handleGeneratedKey);
    }

    void handleGeneratedKey(Object[] generatedKey) {
        rowMapper.setPrimaryKeyValues(beanCache, generatedKey);
    }

    public B create(B bean) {
        beanCache = bean;
        write(beanCache);
        return beanCache;
    }

}
