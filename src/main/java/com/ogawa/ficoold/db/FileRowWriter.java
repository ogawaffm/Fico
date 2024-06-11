package com.ogawa.ficoold.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class FileRowWriter {

    final private Connection connection;
    final private int scanId;

    final private PreparedStatement insertIntoFile;
    final private PreparedStatement updateFileChecksum;

    private final static String INSERT_INTO_FILE =
        "INSERT INTO FILE (SCAN_ID, DIR_ID, PATH, NAME, SIZE, LAST_WRITE_ACCESS) VALUES (?, ?, ?, ?, ?, ?)";


    private final static String UPDATE_FILE_CHECKSUM =
        "UPDATE FILE SET CHECKSUM = ?, CALC_STARTED = ?, CALC_FINISHED = ? WHERE FILE_ID = ?";

    public FileRowWriter(Connection connection, int scanId) {
        this.connection = connection;
        this.scanId = scanId;
        try {
            insertIntoFile = connection.prepareStatement(INSERT_INTO_FILE, Statement.RETURN_GENERATED_KEYS);
            insertIntoFile.setInt(1, scanId);

            updateFileChecksum = connection.prepareStatement(UPDATE_FILE_CHECKSUM);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int create(Integer dirId, String path, String name, long size, Date lastWriteAccess) {
        try {

            insertIntoFile.setInt(2, dirId == null ? -1 : dirId);
            insertIntoFile.setString(3, path);
            insertIntoFile.setString(4, name == null ? "" : name);
            insertIntoFile.setLong(5, size);

            insertIntoFile.setTimestamp(6, new Timestamp(lastWriteAccess.getTime()));

            return Util.execAndReturnGeneratedKey(insertIntoFile);

        } catch (Exception e) {
            System.out.println(
                "dirId: " + dirId + " path: " + path + " name: " + name + " size: " + size + " lastWriteAccess: "
                    + lastWriteAccess);
            throw new RuntimeException(e);
        }

    }

    public void updateCheckSum(int fileId, byte[] checksum, Date calcStarted, Date calcFinished) {

        try (PreparedStatement preparedStatement = connection.prepareStatement(
            UPDATE_FILE_CHECKSUM)) {

            preparedStatement.setBytes(1, checksum);
            preparedStatement.setTimestamp(2, new Timestamp(calcStarted.getTime()));
            preparedStatement.setTimestamp(3, new Timestamp(calcFinished.getTime()));
            preparedStatement.setInt(4, fileId);

            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
