package com.ogawa.fico.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcTransferor {

    static public Object[] resultSetToObjectArray(ResultSet resultSet) throws SQLException {

        int columnCount;

        try {
            columnCount = resultSet.getMetaData().getColumnCount();
        } catch (SQLException cause) {
            throw new SQLException("Error determining column count from result set meta data", cause);
        }

        Object[] row = new Object[columnCount];
        int columnNo = 0;

        try {
            do {
                columnNo++;
                row[columnNo - 1] = resultSet.getObject(columnNo);
            } while (columnNo < columnCount);

        } catch (SQLException getObjException) {
            String columnName;
            try {
                columnName = resultSet.getMetaData().getColumnLabel(columnNo);
            } catch (SQLException ignorableException) {
                throw new SQLException("Error reading column #" + columnNo + " (1-based)", getObjException);
            }
            throw new SQLException(
                "Error reading column #" + columnNo + "(" + columnName + ")", getObjException);
        }

        return row;

    }

    static public void objectArrayToBindVariables(Object[] row, PreparedStatement preparedStatement)
        throws SQLException {

        int columnNo = 0;

        try {
            for (columnNo = 1; columnNo <= row.length; columnNo++) {
                preparedStatement.setObject(columnNo, row[columnNo - 1]);
            }

        } catch (SQLException setObjException) {
            throw new SQLException("Error writing column #" + columnNo + " (1-based)", setObjException);
        }
    }

}
