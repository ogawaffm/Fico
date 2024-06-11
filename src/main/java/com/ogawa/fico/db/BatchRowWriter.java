package com.ogawa.fico.db;

import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.*;

public class BatchRowWriter implements AutoCloseable {

    private PreparedStatement batchStatement;

    final boolean commitAfterBatch;
    final private int batchSize;
    private int batchNo = 0;
    private int rowNum = 0;

    public BatchRowWriter(PreparedStatement preparedStatement, int batchSize, boolean commitAfterBatch) {
        this.batchStatement = preparedStatement;
        this.batchSize = batchSize;
        this.commitAfterBatch = commitAfterBatch;
    }

    private boolean isOpen() {
        boolean isOpen;
        try {
            isOpen = batchStatement != null || !batchStatement.isClosed();
        } catch (SQLException batchStatementClosedException) {
            isOpen = false;
        }
        return isOpen;
    }

    private boolean isBatchFull() {
        return rowNum > 0 && (rowNum % batchSize == 0);
    }

    public void setRow(Object[] row) {

        rowNum++;

        try {

            JdbcTransferor.objectArrayToBindVariables(row, batchStatement);
            batchStatement.addBatch();

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
        close();
        throw new RuntimeException(sb.toString());
    }

    private void flush() {

        batchNo++;

        try {
            batchStatement.executeBatch();
        } catch (SQLException executeBatchException) {
            throwFlushException("executing", batchNo, rowNum, executeBatchException);
        }

        if (commitAfterBatch) {
            try {
                batchStatement.getConnection().commit();
            } catch (SQLException commitException) {
                throwFlushException("committing", batchNo, rowNum, commitException);
            }
        }

    }

    public void close() {

        if (isOpen()) {
            // Still unwritten rows?
            if (!isBatchFull()) {
                flush();
            }

            // mark as closed
            batchStatement = null;

        }

    }

}
