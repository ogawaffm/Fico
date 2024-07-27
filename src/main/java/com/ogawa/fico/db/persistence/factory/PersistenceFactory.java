package com.ogawa.fico.db.persistence.factory;

import com.ogawa.fico.db.persistence.beanreader.StaticBeanReader;
import com.ogawa.fico.db.persistence.beanreader.BeanReader;
import com.ogawa.fico.db.persistence.beanreader.ByPrimaryKeySeekingBeanReader;
import com.ogawa.fico.db.persistence.beanwriter.BatchedCreator;
import com.ogawa.fico.db.persistence.beanwriter.BatchedDeleter;
import com.ogawa.fico.db.persistence.beanwriter.BatchedUpdater;
import com.ogawa.fico.db.persistence.beanwriter.Creator;
import com.ogawa.fico.db.persistence.beanwriter.Deleter;
import com.ogawa.fico.db.persistence.beanwriter.ImmediateCreator;
import com.ogawa.fico.db.persistence.beanwriter.ImmediateDeleter;
import com.ogawa.fico.db.persistence.beanwriter.ImmediateUpdater;
import com.ogawa.fico.db.persistence.beanwriter.Updater;
import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import com.ogawa.fico.db.persistence.rowmapper.RowMapperSql;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.NonNull;

public abstract class PersistenceFactory<B> {

    final Connection connection;
    final RowMapper<B> rowMapper;

    final String defaultTableName;

    final int defaultBatchSize;

    final int defaultFetchSize;

    PersistenceFactory(@NonNull Connection connection, @NonNull RowMapper<B> rowMapper,
        @NonNull String defaultTableName, int defaultBatchSize,
        int defaultFetchSize) {
        this.connection = connection;
        this.rowMapper = rowMapper;
        this.defaultTableName = defaultTableName;
        this.defaultBatchSize = defaultBatchSize;
        this.defaultFetchSize = defaultFetchSize;
    }

    public Creator<B> createCreator() {
        return createCreator(defaultTableName, defaultBatchSize);
    }

    public Creator<B> createCreator(int batchSize) {
        return createCreator(defaultTableName, batchSize);
    }

    public Creator<B> createCreator(String tableName) {
        return createCreator(tableName, defaultBatchSize);
    }

    public Creator<B> createCreator(String tableName, int batchSize) {
        if (batchSize > 1) {
            return new BatchedCreator<>(connection, tableName, defaultBatchSize, rowMapper);
        } else {
            return new ImmediateCreator<>(connection, tableName, rowMapper);
        }
    }

    public BeanReader<B> createReader() {
        return createReader(defaultTableName);
    }

    public BeanReader<B> createReader(int fetchSize) {
        return createReader(defaultTableName, fetchSize);
    }

    public BeanReader<B> createReader(String tableName) {
        return createReader(tableName, defaultFetchSize);
    }

    public BeanReader<B> createReader(String tableName, int fetchSize) {
        return new StaticBeanReader<>(
            connection,
            new RowMapperSql(rowMapper).getSelectFromTableSql(tableName, true),
            fetchSize,
            rowMapper
        );
    }

    private ByPrimaryKeySeekingBeanReader<B> createSeekingReader(String sqlWithSeekBindVariables) {
        return new ByPrimaryKeySeekingBeanReader<>(connection, sqlWithSeekBindVariables, defaultFetchSize, rowMapper);
    }

    private ByPrimaryKeySeekingBeanReader<B> createSeekingReader(String sqlWithSeekBindVariables, int fetchSize) {
        return new ByPrimaryKeySeekingBeanReader<>(connection, sqlWithSeekBindVariables, fetchSize, rowMapper);
    }

    public ByPrimaryKeySeekingBeanReader<B> createByPrimaryKeySeekingReader() {
        return createByPrimaryKeySeekingReader(defaultTableName);
    }

    public ByPrimaryKeySeekingBeanReader<B> createByPrimaryKeySeekingReader(String tableName) {
        return new ByPrimaryKeySeekingBeanReader<>(
            connection,
            new RowMapperSql(rowMapper).getSelectFromTableSql(tableName, true),
            1,
            rowMapper);
    }

    public Updater<B> createUpdater() {
        return createUpdater(defaultBatchSize);
    }

    public Updater<B> createUpdater(int batchSize) {
        return createUpdater(defaultTableName, batchSize);
    }

    public Updater<B> createUpdater(String tableName, int batchSize) {
        if (batchSize > 1) {
            return new BatchedUpdater<>(connection, tableName, defaultBatchSize, rowMapper);
        } else {
            return new ImmediateUpdater<>(connection, rowMapper, tableName);
        }
    }

    public Deleter<B> createDeleter() {
        return createDeleter(defaultBatchSize);
    }

    public Deleter<B> createDeleter(int batchSize) {
        return createDeleter(defaultTableName, batchSize);
    }

    public Deleter<B> createDeleter(String tableName, int batchSize) {
        if (batchSize > 1) {
            return new BatchedDeleter<>(connection, tableName, batchSize, rowMapper);
        } else {
            return new ImmediateDeleter<>(connection, rowMapper, tableName);
        }
    }

}
