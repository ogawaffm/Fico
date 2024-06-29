package com.ogawa.fico.db;

import static com.ogawa.fico.jdbc.JdbcTransferor.objectArrayToBindVariables;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Abstract class for writing rows to a {@link PreparedStatement}.
 */
public abstract class RowWriter {

    abstract PreparedStatement getPreparedStatement();

    void setRow(Object[] row) throws SQLException {
        objectArrayToBindVariables(row, getPreparedStatement());
    }

}
