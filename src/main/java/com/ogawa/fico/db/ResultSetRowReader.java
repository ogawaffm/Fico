package com.ogawa.fico.db;

import static com.ogawa.fico.jdbc.JdbcTransferor.resultSetToObjectArray;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface for reading rows from a {@link ResultSet}.
 */
public interface ResultSetRowReader {

    ResultSet getResultSet();

    default Object[] getRow() throws SQLException {
        return resultSetToObjectArray(getResultSet());
    }

}
