package com.ogawa.fico.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * Iterator for ResultSets. At the moment, it only supports one ResultSet.
 */
public class ResultIterator implements Iterator {

    private ResultSet resultSet = null;

    ResultIterator(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    ResultIterator(Statement statement) {
        try {
            this.resultSet = statement.getResultSet();
        } catch (SQLException ignore) {
        }
    }

    @Override
    public boolean hasNext() {
        return resultSet != null;
    }

    @Override
    public Object next() {
        ResultSet resultSet = this.resultSet;
        // memory leak prevention
        this.resultSet = null;
        return resultSet;
    }

}
