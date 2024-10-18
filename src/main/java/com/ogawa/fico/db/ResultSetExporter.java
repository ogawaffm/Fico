package com.ogawa.fico.db;

import com.ogawa.fico.checksum.ChecksumBuilder;
import java.io.PrintStream;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

public class ResultSetExporter extends ResultSetDumper {

    public ResultSetExporter(ResultSet resultSet, PrintStream printStream, String rowDelimiter, String columnDelimiter,
        String nullRepresentative) {
        super(resultSet, printStream, rowDelimiter, columnDelimiter, nullRepresentative);
    }

    @Override
    void writeHeader() {

        for (int columnIndex = 1; columnIndex <= getColumnCount(); columnIndex++) {

            String columnLabel = getColumnLabel(columnIndex);
            columnLabel = escape(columnLabel);
            writeString(columnLabel);

            if (columnIndex < getColumnCount()) {
                writeColumnDelimiter();
            }
        }
        writeRowDelimiter();
    }

    private String toString(Object object) {
        if (object == null) {
            return nullRepresentative;
        } else if (object instanceof Date) {
            // yes, truncate time part to seconds for better readability and for proper import to Excel
            Date date = (Date) object;
            date.setTime((date.getTime() / 86400) * 86400);
        } else if (object instanceof byte[]) {
            object = ChecksumBuilder.getBytesToHex((byte[]) object);
        } else if (object instanceof Object[]) {
            Object[] objects = (Object[]) (object);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < objects.length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(toString(objects[i]));
            }
            sb.append("]");
            return sb.toString();
        } else if (object instanceof java.sql.Array) {
            try {
                return toString(((java.sql.Array) object).getArray());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            object = object.toString();
        }

        return escape(object.toString());
    }

    @Override
    void writeColumnData(int columnIndex) {

        Object object = getColumnData(columnIndex);

        String str = toString(object);

        str = escape(str);

        writeString(str);

    }

    private String quote(String s) {
        if (s == null) {
            return nullRepresentative;
        } else {
            return "\"" + s + "\"";
        }
    }

    String escape(String s) {
        if (s == null) {
            s = nullRepresentative;
        } else {
            if (s.startsWith("\"")) {
                s = s.replace("\"", "\"\"");
                s = quote(s);
            } else {
                if (s.contains(getRowDelimiter()) || s.contains(getColumnDelimiter())) {
                    s = quote(s);
                }
            }
        }

        return s;
    }

}