package com.ogawa.fico.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class FileRowBatchAccess {

    final protected Connection connection;

    final protected BatchRowWriter batchRowWriter;

    protected FileRowBatchAccess(Connection connection,
        String sqlWithBindVariables, int batchSize, boolean commitAfterBatch) {

        this.connection = connection;

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlWithBindVariables);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        batchRowWriter = new BatchRowWriter(preparedStatement, batchSize, commitAfterBatch);

    }

    public void close() {
        batchRowWriter.close();
    }

}
