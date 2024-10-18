package com.ogawa.fico.db.persistence.bindvarwriter;

import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.*;
import java.util.function.Consumer;

public class BatchedBindVarWriter implements BindVarWriter {

    private PreparedStatement preparedStatement;
    private final boolean commitAfterBatch;
    private final int batchSize;
    private int batchNo = 0;
    private int rowNum = 0;

    private Consumer<Object[]> generatedKeyConsumer = null;

    public BatchedBindVarWriter(Connection connection,
        String sqlWithBindVariables, int batchSize, boolean commitAfterBatch) {
        try {
            preparedStatement = connection.prepareStatement(sqlWithBindVariables);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.batchSize = batchSize;
        this.commitAfterBatch = commitAfterBatch;
    }

    public void setGeneratedKeyHandler(Consumer<Object[]> generatedKeyConsumer) {
        this.generatedKeyConsumer = generatedKeyConsumer;
    }

    private boolean isOpen() {
        boolean isOpen;
        try {
            isOpen = preparedStatement != null || !preparedStatement.isClosed();
        } catch (SQLException batchStatementClosedException) {
            isOpen = false;
        }
        return isOpen;
    }

    @Override
    public boolean isClosed() {
        return !isOpen();
    }

    private boolean isBatchFull() {
        return rowNum > 0 && (rowNum % batchSize == 0);
    }

    public void write(Object[] row) {

        rowNum++;

        try {

            JdbcTransferor.setBindVars(preparedStatement, row);

            preparedStatement.addBatch();

        } catch (SQLException addBatchException) {
            throw new RuntimeException(
                "Error setting data of row #" + rowNum + " (1-based) in batch #" + batchNo + " (1-based)",
                addBatchException);
        }

        // batch size reached?
        if (isBatchFull()) {
            flush();
        }

    }

    private void throwFlushException(String action, int batchNo, int rowNum, SQLException cause) {

        StringBuilder sb = new StringBuilder();

        int batchStart = (batchNo - 1) * batchSize + 1;
        int batchEnd = rowNum;

        sb.append("Error ");
        sb.append(action);
        sb.append(" batch #").append(batchNo);
        sb.append(" (1-based, Row #").append(batchStart).append("-").append(batchEnd).append(").");

        if (cause instanceof BatchUpdateException) {
            int[] updateCounts = ((BatchUpdateException) cause).getUpdateCounts();
            if (updateCounts == null) {
                sb.append(
                    " There are no update counts available from BatchUpdateException.getUpdateCounts() to determine erroneous rows.");
            } else {
                sb.append(" Erroneous row numbers (1-based) are: ");
                int rowNo = 1;
                int numberFailedRows = 0;
                for (int updateCount : updateCounts) {
                    if (updateCount == Statement.EXECUTE_FAILED) {
                        if (numberFailedRows > 0) {
                            sb.append(", ");
                        }
                        sb.append(batchStart + rowNo - 1);
                        numberFailedRows++;
                    }
                    rowNo++;
                }
                sb.append("\n")
                    .append(numberFailedRows).append(" rows of ").append(batchEnd - batchStart + 1).append(" failed.");
            }
        }
        sb.append(" Caused by: ");
        sb.append(cause.getMessage());
        try {
            close();
        } catch (SQLException ignored) {
        }
        throw new RuntimeException(sb.toString());
    }

    private void flush() {

        batchNo++;

        try {
            preparedStatement.executeBatch();
            if (generatedKeyConsumer != null) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                while (generatedKeys.next()) {
                    Object[] generatedKey = JdbcTransferor.resultSetToObjectArray(generatedKeys);
                    generatedKeyConsumer.accept(generatedKey);
                }
            }
        } catch (SQLException executeBatchException) {
            throwFlushException("executing", batchNo, rowNum, executeBatchException);
        }

        if (commitAfterBatch) {
            try {
                preparedStatement.getConnection().commit();
            } catch (SQLException commitException) {
                throwFlushException("committing", batchNo, rowNum, commitException);
            }
        }

    }

    @Override
    public void close() throws SQLException {

        if (isOpen()) {
            // Still unwritten rows?
            if (!isBatchFull()) {
                flush();
            }
            preparedStatement.close();
            // mark as closed
            preparedStatement = null;

        }

    }

}
