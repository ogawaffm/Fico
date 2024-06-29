package com.ogawa.fico.db;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetTabulator extends ResultSetDumper {

    private final String CONTROL_CHAR_SYMBOL = "␀␁␂␃␄␅␆␇␈␉␊␋␌␍␎␏␐␑␒␓␔␕␖␗␘␙␚␛␜␝␞␟";


    private int[] columnSizes;

    private boolean[] isLeftAligned;

    public ResultSetTabulator(ResultSet resultSet, PrintStream printStream, String rowDelimiter, String columnDelimiter,
        String nullRepresentative) {
        super(resultSet, printStream, rowDelimiter, columnDelimiter, nullRepresentative);

        try {
            columnSizes = DataType.getFixedLengthColumnSizes(resultSet.getMetaData());
            isLeftAligned = DataType.getColumnsLeftAlignment(resultSet.getMetaData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Writes a string to the output, escaping control characters. \u0000 => \u02400 (␀), \u0001 => \u2400 (␁) etc.
     * {@see <a href="compart.com/de/unicode/block/U+2400">unicode 2400-2446</a> compart.com/de/unicode/block/U+2400}
     *
     * @param s the string to write
     */
    private String escape(String s) {

        if (s == null) {
            return nullRepresentative;
        } else {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) < 32) {
                    s = s.replace(s.charAt(i), (char) ('\u2400' + i));
                }
            }
        }
        return s;
    }

    @Override
    void writeColumnData(int columnIndex) {
        String data;

        Object colData = getColumnData(columnIndex);

        if (colData == null) {
            data = nullRepresentative;
        } else {
            data = escape(colData.toString());
        }

        if (!isLeftAligned[columnIndex - 1]) {
            writeChar(' ', columnSizes[columnIndex - 1] - data.length());
            writeString(data);
        } else {
            writeString(data);
            writeChar(' ', columnSizes[columnIndex - 1] - data.length());
        }

    }

    private void writeHeaderLine() {

        for (int columnIndex = 1; columnIndex <= columnSizes.length; columnIndex++) {

            writeChar('-', columnSizes[columnIndex - 1]);

            if (columnIndex < columnSizes.length) {
                writeColumnDelimiter();
            }
        }
        writeRowDelimiter();
    }

    private void writeHeaderLabels() {

        String columnLabel;

        for (int columnIndex = 1; columnIndex <= columnSizes.length; columnIndex++) {

            columnLabel = escape(getColumnLabel(columnIndex));

            writeString(columnLabel);

            writeChar(' ', columnSizes[columnIndex - 1] - columnLabel.length());

            if (columnIndex < columnSizes.length) {
                writeColumnDelimiter();
            }

        }

        writeRowDelimiter();

    }

    @Override
    void writeHeader() {

        writeHeaderLine();

        writeHeaderLabels();

        writeHeaderLine();

    }

}
