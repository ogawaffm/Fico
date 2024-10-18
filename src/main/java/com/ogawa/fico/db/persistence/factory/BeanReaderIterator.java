package com.ogawa.fico.db.persistence.factory;

import com.ogawa.fico.db.persistence.beanreader.BeanReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.NonNull;

public class BeanReaderIterator<B> implements Iterator<B>, AutoCloseable {

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
    final private BeanReader<B> beanReader;
    private Boolean hasNextRow = null;

    final private boolean closeIfNoNext;

    private B cachedBean = null;

    /**
     * Creates a RowIterator with a row limit (if rowLimit is 0, there is no limit)
     *
     * @param beanReader    the BeanReader to iterate over
     * @param closeIfNoNext if true, the ResultSet is closed if there is no next row
     * @param rowLimit      maximum number of rows to iterate over (0 means no limit)
     */
    public BeanReaderIterator(@NonNull BeanReader<B> beanReader, boolean closeIfNoNext, long rowLimit) {
        this.beanReader = beanReader;
        if (rowLimit < 0) {
            throw new IllegalArgumentException("rowLimit must be >= 0");
        }
        this.closeIfNoNext = closeIfNoNext;
        this.iterationLimit = rowLimit == 0L ? -1 : rowLimit;
    }

    public void close() {
        try {
            cachedBean = null;
            beanReader.close();
        } catch (Exception ignore) {
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
                cachedBean = beanReader.read();
                hasNextRow = cachedBean != null;
            }
        }

        if (!hasNextRow && closeIfNoNext && !beanReader.isClosed()) {
            close();
        }

        return hasNextRow;
    }

    public B next() {

        // Is there no next row?
        if (hasNext()) {

            // yes, move to next row
            rowNo++;

            // do not know whether there is a next row
            hasNextRow = null;

            return cachedBean;

        } else {
            throw new NoSuchElementException();
        }

    }

}
