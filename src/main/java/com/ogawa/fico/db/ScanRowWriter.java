package com.ogawa.fico.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class ScanRowWriter {

    final private Connection connection;

    final private PreparedStatement insertIntoScan;
    final private PreparedStatement updateScanStarted;
    final private PreparedStatement updateScanFinished;

    private final static String INSERT_INTO_SCAN = "INSERT INTO SCAN (ROOT) VALUES (?)";


    private final static String UPDATE_SCAN_STARTED = "UPDATE SCAN SET STARTED = ? WHERE SCAN_ID = ?";
    private final static String UPDATE_SCAN_FINISHED = "UPDATE SCAN SET FINISHED = ? WHERE SCAN_ID = ?";

    public ScanRowWriter(Connection connection) {
        this.connection = connection;
        try {
            insertIntoScan = connection.prepareStatement(INSERT_INTO_SCAN, Statement.RETURN_GENERATED_KEYS);

            updateScanStarted = connection.prepareStatement(UPDATE_SCAN_STARTED);
            updateScanFinished = connection.prepareStatement(UPDATE_SCAN_FINISHED);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int create(Path root) {
        try {

            insertIntoScan.setString(1, root.toString());

            return Util.execAndReturnGeneratedKey(insertIntoScan);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateStarted(int scanId, Date started) {

        update(updateScanStarted, scanId, started);

    }

    public void updateFinished(int scanId, Date finished) {

        update(updateScanFinished, scanId, finished);

    }

    private void update(PreparedStatement preparedStatement, int scanId, Date date) {
        try {
            preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
            preparedStatement.setInt(2, scanId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
