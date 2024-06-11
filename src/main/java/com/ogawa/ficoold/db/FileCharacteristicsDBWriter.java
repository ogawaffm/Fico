package com.ogawa.ficoold.db;

import com.ogawa.ficoold.CombinedFileInfo;
import com.ogawa.ficoold.FileCharacteristicsConsumer;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class FileCharacteristicsDBWriter extends FileCharacteristicsConsumer {

    static private final String INSERT_INTO_SCAN = "INSERT INTO SCAN (ROOTPATH, TIMESTARTED) VALUES (?, ?)";

    static private final String UPDATE_SCAN =
        "UPDATE SCAN SET\n"
            + "TIMEFINISHED = ?,\n"
            + "DURATION = CAST((CAST(? AS TIMESTAMP) - TIMESTARTED) AS INTERVAL DAY(3) TO SECOND(0)),\n"
            + "FILECOUNT = ?,\n"
            + "FILESIZE = ?,\n"
            + "MINLASTWRITETIME = ?,\n"
            + "MAXLASTWRITETIME = ?";

    static private final String INSERT_INTO_FILE =
        "INSERT INTO FILE (SCAN_ID, PATH, NAME, DIR, SIZE, LASTWRITETIME, CHECKSUM)\n" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";

    private final Connection connection;
    private PreparedStatement preparedStatement;
    private final Path rootPath;
    private final int scanId;

    public FileCharacteristicsDBWriter(Connection connection, Path rootPath, int scanId) {

        this.connection = connection;
        this.rootPath = rootPath;
        this.scanId = scanId;

    }

    @Override
    public void accept(CombinedFileInfo combinedFileInfo) {

        Path path = rootPath.relativize(combinedFileInfo.getPath());

        try {

            preparedStatement.setInt(1, scanId);
            preparedStatement.setString(2, path.toString());
            preparedStatement.setString(3, path.getFileName().toString());

            preparedStatement.setString(4,
                path.getParent() == null ? "" : path.getParent().getFileName().toString());

            preparedStatement.setLong(5, combinedFileInfo.getAttributes().size());

            Timestamp lastModifiedTime = new Timestamp(combinedFileInfo.getAttributes().lastModifiedTime().toMillis());
            preparedStatement.setTimestamp(6, lastModifiedTime);
            preparedStatement.setObject(7, combinedFileInfo.getCheckSumStats().getBinaryChecksum());
            preparedStatement.execute();

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void open() {

        try {

            preparedStatement = connection.prepareStatement(INSERT_INTO_FILE);

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public void close() {
        super.close();
        try {
            preparedStatement.close();
        } catch (SQLException ignore) {
        }
    }

}
