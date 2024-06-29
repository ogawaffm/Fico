package com.ogawa.fico.db;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class DataType {

    public static boolean[] getColumnsLeftAlignment(ResultSetMetaData resultSetMetaData) {
        try {
            boolean[] isLeftAligned = new boolean[resultSetMetaData.getColumnCount()];
            for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++) {
                isLeftAligned[columnIndex - 1] = isLeftAlignedDataType(resultSetMetaData.getColumnType(columnIndex));
            }
            return isLeftAligned;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static int[] getFixedLengthColumnSizes(ResultSetMetaData resultSetMetaData) {

        try {

            int[] columnSizes = new int[resultSetMetaData.getColumnCount()];

            for (int columnIndex = 1; columnIndex <= resultSetMetaData.getColumnCount(); columnIndex++) {

                columnSizes[columnIndex - 1] = getFixedLengthColumnSize(resultSetMetaData, columnIndex);

            }

            return columnSizes;

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static int getFixedLengthColumnSize(ResultSetMetaData resultSetMetaData, int column) {
        return Math.max(
            getColumnLabelLength(resultSetMetaData, column),
            getMaxColumnDataLength(resultSetMetaData, column)
        );
    }

    public static int getColumnLabelLength(ResultSetMetaData resultSetMetaData, int column) {
        try {
            return resultSetMetaData.getColumnLabel(column).length();
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    private static int getMaxColumnDataLength(ResultSetMetaData resultSetMetaData, int column) {
        try {

            int columnType = resultSetMetaData.getColumnType(column);
            int precision = resultSetMetaData.getPrecision(column);
            int scale = resultSetMetaData.getScale(column);

            return getMaxColumnDataLength(columnType, precision, scale);

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

    }

    private static boolean isCharacterBasedType(int columnType) {
        switch (columnType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:

            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.SQLXML:
                return true;
            default:
                return false;
        }
    }

    private static boolean isBinaryBasedType(int columnType) {
        switch (columnType) {
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                return true;
            default:
                return false;
        }
    }

    private static boolean isNumericType(int columnType) {
        switch (columnType) {
            case Types.BIT:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                return true;
            default:
                return false;
        }
    }

    private static boolean isTimeBasedType(int columnType) {
        switch (columnType) {
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
            case Types.DATE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isLeftAlignedDataType(int columnType) {
        return !isRightAlignedDataType(columnType);
    }

    public static boolean isRightAlignedDataType(int columnType) {
        return isNumericType(columnType) || isTimeBasedType(columnType);
    }

    private static int getMaxColumnDataLength(int columnType, int precision, int scale) {

        int adjustedPrecision;

        switch (columnType) {

            // *** character based data types ***
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.CLOB:

            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.SQLXML:
                return precision == 0 ? Integer.MAX_VALUE : precision;

            // *** binary based data types ***
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                // 2 char for each byte, but limited to the Integer.MAX_VALUE
                return precision == 0 ? Integer.MAX_VALUE
                    : (int) Math.min((long) precision * 2L, Integer.MAX_VALUE);

            // ***************** numbers *****************
            case Types.BIT:
                return 1;
            case Types.BOOLEAN:
                // "false" => 5
                return 5;
            case Types.SMALLINT:
                return 6;
            case Types.TINYINT:
                return 4;
            case Types.INTEGER:
                return 11;
            case Types.BIGINT:
                return 20;
            case Types.FLOAT:
                return 13;
            case Types.REAL:
            case Types.DOUBLE:
                return 24;
            case Types.NUMERIC:
            case Types.DECIMAL:
                adjustedPrecision = precision == 0 ? 18 : precision;
                // sign + digits + decimal point (if is scaled)
                return 1 + adjustedPrecision + (scale > 0 ? 1 : 0);

            // *** time-related data types ***
            case Types.TIME:
                adjustedPrecision = precision == 0 ? 8 : precision;
                return scale > 0 ? adjustedPrecision + 1 + scale : adjustedPrecision;
            case Types.TIME_WITH_TIMEZONE:
                adjustedPrecision = precision == 0 ? 8 : precision;
                return (scale > 0 ? adjustedPrecision + 1 + scale : adjustedPrecision) + 6;
            case Types.TIMESTAMP:
                adjustedPrecision = precision == 0 ? 19 : precision;
                return (scale > 0 ? adjustedPrecision + 1 + scale : adjustedPrecision);
            case Types.TIMESTAMP_WITH_TIMEZONE:
                adjustedPrecision = precision == 0 ? 19 : precision;
                return (scale > 0 ? adjustedPrecision + 1 + scale : adjustedPrecision) + 6;
            case Types.DATE:
                return precision == 0 ? 10 : precision;

            // *** other data types ***
            case Types.OTHER:
            case Types.NULL:
            case Types.ARRAY:
            case Types.STRUCT:
            case Types.REF:
            case Types.REF_CURSOR:
            case Types.ROWID:
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
            case Types.DATALINK:
            default:
                return 0;
        }

    }

}
