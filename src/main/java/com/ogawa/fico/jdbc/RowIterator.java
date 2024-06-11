package com.ogawa.fico.jdbc;

import static com.ogawa.fico.jdbc.JdbcTransferor.resultSetToObjectArray;

import lombok.NonNull;

import java.sql.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RowIterator implements Iterator<Object[]>, AutoCloseable {

    /**
     * -1 means no limit
     */
    final private long iterationLimit;
    /**
     * 1-based row number
     */
    private long rowNo = 0;

    /**
     * ResultSet to iterate over.
     */
    final private ResultSet resultSet;
    private Boolean hasNextRow = null;

    final private boolean closeIfNoNext;

    /**
     * Creates a RowIterator with a row limit (if rowLimit is 0, there is no limit)
     *
     * @param resultSet     ResultSet to iterate over
     * @param closeIfNoNext if true, the ResultSet is closed if there is no next row
     * @param rowLimit      maximum number of rows to iterate over (0 means no limit)
     */
    public RowIterator(@NonNull ResultSet resultSet, boolean closeIfNoNext, long rowLimit) {
        this.resultSet = resultSet;
        if (rowLimit < 0) {
            throw new IllegalArgumentException("rowLimit must be >= 0");
        }
        this.closeIfNoNext = closeIfNoNext;
        this.iterationLimit = rowLimit == 0L ? -1 : rowLimit;
    }

    public void close() {
        try {
            resultSet.close();
        } catch (SQLException ignore) {
        }
    }

    public boolean hasNext() {
        // Reached last row?
        if (rowNo == iterationLimit) {
            // yes, no more rows
            hasNextRow = false;
        } else {
            // Still logEvent current row?
            if (hasNextRow == null) {
                // move one row forward
                try {
                    hasNextRow = resultSet.next();
                } catch (SQLException cause) {
                    throw new RuntimeException(cause);
                }
            }
        }
        try {
            if (!hasNextRow && closeIfNoNext && !resultSet.isClosed()) {
                close();
            }
        } catch (SQLException cause) {
            throw new RuntimeException(cause);
        }

        return hasNextRow;
    }

    public Object[] next() {

        // Is there no next row?
        if (hasNext()) {

            // yes, move to next row
            rowNo++;

            // do not know whether there is a next row
            hasNextRow = null;

            try {
                return resultSetToObjectArray(resultSet);
            } catch (SQLException cause) {
                throw new RuntimeException("Error reading row #" + rowNo + " (1-based)", cause);
            }

        } else {
            throw new NoSuchElementException();
        }

    }

}
