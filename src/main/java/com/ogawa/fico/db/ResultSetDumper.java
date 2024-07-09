package com.ogawa.fico.db;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultSetDumper {

    private final ResultSet resultSet;
    private final PrintStream printStream;

    private final String rowDelimiter;

    private final String columnDelimiter;

    final String nullRepresentative;

    private final int columnCount;

    ResultSetDumper(ResultSet resultSet, PrintStream printStream, String rowDelimiter, String columnDelimiter,
        String nullRepresentative) {

        try {
            this.resultSet = resultSet;
            this.printStream = printStream;
            this.rowDelimiter = rowDelimiter;
            this.columnDelimiter = columnDelimiter;
            this.nullRepresentative = nullRepresentative;

            // buffer column count to avoid calling resultSet.getMetaData().getColumnCount() multiple times, which can cause exceptions
            columnCount = resultSet.getMetaData().getColumnCount();

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    Object getColumnData(int columnIndex) {
        try {
            return resultSet.getObject(columnIndex);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    String getColumnLabel(int columnIndex) {
        try {
            return resultSet.getMetaData().getColumnLabel(columnIndex);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    int getColumnCount() {
        return columnCount;
    }

    String getRowDelimiter() {
        return rowDelimiter;
    }

    String getColumnDelimiter() {
        return columnDelimiter;
    }

    abstract void writeHeader();

    abstract void writeColumnData(int columnIndex);

    void writeChar(char c, int number) {
        for (int i = 0; i < number; i++) {
            printStream.print(c);
        }
    }

    void writeString(String s) {
        if (s == null) {
            printStream.print('␀');
        } else {
            printStream.print(s);
        }
    }

    void writeColumnDelimiter() {
        writeString(columnDelimiter);
    }

    void writeRowDelimiter() {
        writeString(rowDelimiter);
    }

    private void writeRow() {
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            if (columnIndex > 1) {
                writeColumnDelimiter();
            }
            writeColumnData(columnIndex);
        }
    }

    private long writeRows() {
        long rows = 0;
        try {
            while (resultSet.next()) {
                writeRow();
                writeRowDelimiter();
                rows++;
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
        return rows;
    }

    public long write() {
        writeHeader();
        return writeRows();
    }

}
