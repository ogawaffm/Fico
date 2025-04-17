package com.ogawa.fico.db;

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.NonNull;

public class ColumnIterator implements Iterator {


    private int columnCount;
    private int columnIndex = 1;
    private ResultSet resultSet;

    public ColumnIterator(@NonNull ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean hasNext() {
        return columnIndex <= columnCount;
    }

    @Override
    public Object next() {
        if (hasNext()) {
            try {
                return resultSet.getObject(columnIndex++);
            } catch (Exception e) {
                // memory leak prevention
                resultSet = null;
                throw new RuntimeException("Error reading column data. Column index: " + columnIndex, e);
            }
        } else {
            // memory leak prevention
            resultSet = null;
            throw new NoSuchElementException(
                "Column index out of bounds. Requested index: " + columnIndex + ", column count: " + columnCount);
        }
    }
}
