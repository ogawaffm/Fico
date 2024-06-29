package com.ogawa.fico.db;

import com.ogawa.fico.checksum.ChecksumBuilder;
import java.io.PrintStream;
import java.sql.ResultSet;
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

    @Override
    void writeColumnData(int columnIndex) {

        Object data = getColumnData(columnIndex);

        if (data == null) {
            data = nullRepresentative;
        } else if (data instanceof Date) {
            // yes, truncate time part to seconds for better readability and for proper import to Excel
            Date date = (Date) data;
            date.setTime((date.getTime() / 86400) * 86400);
            data = date;
        } else if (data instanceof byte[]) {
            data = ChecksumBuilder.getBytesToHex((byte[]) data);
        }

        data = escape(data.toString());
        writeString(data.toString());
        
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