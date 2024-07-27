package com.ogawa.fico.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import com.ogawa.fico.misc.System;

public class ScanRowWriter {

    final private Connection connection;

    final private PreparedStatement insertIntoScan;
    final private PreparedStatement updateScanStarted;
    final private PreparedStatement updateScanFinished;

    private final static String UPDATE_SCAN_STARTED = "UPDATE SCAN SET STARTED = ? WHERE SCAN_ID = ?";
    private final static String UPDATE_SCAN_FINISHED = "UPDATE SCAN SET FINISHED = ? WHERE SCAN_ID = ?";

    public ScanRowWriter(Connection connection) {
        this.connection = connection;
        String sql = Util.getSql("CreateScan");

        try {

            insertIntoScan = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not create prepared statement for scan creation: " + sql, sqlException);
        }

        try {
            updateScanStarted = connection.prepareStatement(UPDATE_SCAN_STARTED);

        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not create prepared statement for scan start time update: "
                + UPDATE_SCAN_STARTED, sqlException);
        }

        try {
            updateScanFinished = connection.prepareStatement(UPDATE_SCAN_FINISHED);

        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not create prepared statement for scan finish time update: "
                + UPDATE_SCAN_STARTED, sqlException);
        }
    }

    public long create(Path root) {
        try {

            insertIntoScan.setString(1, root.toString());

            insertIntoScan.setString(2, System.getHostName());
            insertIntoScan.setString(3, System.getUsername());
            insertIntoScan.setLong(4, System.getPid());

            return Util.execAndReturnGeneratedKey(insertIntoScan);

        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not create row in scan for root path '" + root + "'", sqlException);
        }

    }

    public void updateStarted(long scanId, Date started) {

        try {
            update(updateScanStarted, scanId, started);
        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not update scan start time for scan #" + scanId, sqlException);
        }

    }

    public void updateFinished(long scanId, Date finished) {

        try {
            update(updateScanFinished, scanId, finished);
        } catch (SQLException sqlException) {
            throw new RuntimeException("Could not update scan finished time for scan #" + scanId, sqlException);
        }

    }

    private void update(PreparedStatement preparedStatement, long scanId, Date date) throws SQLException {
        preparedStatement.setTimestamp(1, new Timestamp(date.getTime()));
        preparedStatement.setLong(2, scanId);
        preparedStatement.execute();
    }

}
