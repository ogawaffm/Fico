package com.ogawa.fico.db.persistence.factory;

import com.ogawa.fico.db.persistence.beanreader.SeekingBeanReader;
import com.ogawa.fico.db.persistence.beanreader.StaticBeanReader;
import com.ogawa.fico.db.persistence.beanreader.BeanReader;
import com.ogawa.fico.db.persistence.beanreader.PrimaryKeySeekingBeanReader;
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
import java.util.Iterator;
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

    public RowMapper<B> getRowMapper() {
        return rowMapper;
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

    public BeanReader<B> createTableReader() {
        return createTableReader(defaultTableName);
    }

    public BeanReader<B> createTableReader(int fetchSize) {
        return createTableReader(defaultTableName, fetchSize);
    }

    public BeanReader<B> createTableReader(String tableName) {
        return createTableReader(tableName, defaultFetchSize);
    }

    public BeanReader<B> createTableReader(String tableName, int fetchSize) {
        return new StaticBeanReader<>(
            connection,
            new RowMapperSql(rowMapper).getSelectFromTableSql(tableName, false),
            fetchSize,
            rowMapper
        );
    }

    public BeanReader<B> createSqlReader(String sql) {
        return createSqlReader(sql, defaultFetchSize);
    }

    public BeanReader<B> createSqlReader(String sql, int fetchSize) {
        return new StaticBeanReader<>(
            connection,
            new RowMapperSql(rowMapper).getSelectFromSubSelectSql(sql),
            fetchSize,
            rowMapper
        );
    }

    public SeekingBeanReader<B> createSeekingReader(String sqlWithSeekBindVariables) {
        return createSeekingReader(sqlWithSeekBindVariables, defaultFetchSize);
    }

    public SeekingBeanReader<B> createSeekingReader(String sqlWithSeekBindVariables, int fetchSize) {
        return new SeekingBeanReader<>(
            connection,
            new RowMapperSql(rowMapper).getSelectFromSubSelectSql(sqlWithSeekBindVariables),
            fetchSize,
            rowMapper
        );
    }

    public PrimaryKeySeekingBeanReader<B> createByPrimaryKeySeekingReader() {
        return createByPrimaryKeySeekingReader(defaultTableName);
    }

    public PrimaryKeySeekingBeanReader<B> createByPrimaryKeySeekingReader(String tableName) {
        return new PrimaryKeySeekingBeanReader<>(
            connection,
            new RowMapperSql(rowMapper).getSelectFromTableSql(tableName, true),
            1,
            rowMapper
        );
    }

    public Iterator<B> createIterator(BeanReader<B> beanReader, boolean closeIfNoNext) {
        return createIterator(beanReader, closeIfNoNext, 0);
    }

    public Iterator<B> createIterator(BeanReader<B> beanReader, boolean closeIfNoNext, long rowLimit) {
        return new BeanReaderIterator<>(beanReader, closeIfNoNext, rowLimit);
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
