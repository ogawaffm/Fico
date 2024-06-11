package com.ogawa.fico.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

    public static Connection getTcpConnection(String databaseName) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/" + databaseName, "sa", "");
        } finally {
            return connection;
        }
    }

    public static Connection getFileConnection(String databaseName) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:h2:~/" + databaseName, "sa", "");
        } finally {
            return connection;
        }
    }

    public static Connection getConnection(String databaseName) {
        Connection connection;
        try {
            connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/" + databaseName, "sa", "");
        } catch (SQLException ignore) {
            log.info("Could not connect to " + databaseName + " using tcp. Trying to connect to local file ...");
            try {
                connection = DriverManager.getConnection("jdbc:h2:~/" + databaseName, "sa", "");
            } catch (SQLException exception) {
                String msg = "Could not connect to " + databaseName + " using local file";
                log.error(msg + ": " + exception.getMessage());
                throw new RuntimeException(msg, exception);
            }

        }
        return connection;
    }

    static public <T> T getValue(Connection con, String sql, T defaultValue) {
        List values = getValueList(con, sql, 1);
        return values.isEmpty() ? defaultValue : (T) values.get(0);
    }

    static public List getValueList(Connection con, String sql, int columnIndex) {

        List result = new ArrayList<>();

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                result.add(rs.getObject(columnIndex));
            }

        } catch (SQLException ignore) {
        }

        return result;

    }


    static private final String MARK_DUPLICATE_CANDIDATES =
        "UPDATE FILE\n" +
            "SET CHECKSUM = X'00'\n" +
            "WHERE FILE_ID IN (\n" +
            "    SELECT FILE_ID\n" +
            "    FROM FILE\n" +
            "    JOIN (\n" +
            "        SELECT NAME, SIZE\n" +
            "        FROM FILE \n" +
            "        GROUP BY SIZE, NAME\n" +
            "        HAVING SIZE > 0 AND COUNT(*) > 1\n" +
            "    ) DUPLICATES\n" +
            "    ON FILE.NAME = DUPLICATES.NAME AND FILE.SIZE = DUPLICATES.SIZE\n" +
            ")\n";

    /*
       Generated column "PUBLIC.FILE.FILE_ID" cannot be assigned; SQL statement:
       UPDATE "PUBLIC"."FILE" SET "FILE_ID"=? ,"NAME"=? ,"PATH"=? ,"CHECKSUM"=?  WHERE "FILE_ID"=? [90154-224]
     */
    static private final String SELECT_MARKED_DUPLICATE_CANDIDATES =
        "SELECT SCAN_ID, DIR_ID, NAME, PATH, CHECKSUM FROM FILE\n" +
            "WHERE CHECKSUM = X'00'\n\n";


    /**
     * Takes about 2 minutes to run logEvent 1 million rows
     *
     * @param connection
     * @return
     */
    static public int markDuplicateCandidates(Connection connection) {

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(MARK_DUPLICATE_CANDIDATES);
            return execAndReturnRowsAffected(preparedStatement);

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    static public ResultSet getMarkedDuplicateCandidates(Connection connection) {

        try {

            Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            return statement.executeQuery(SELECT_MARKED_DUPLICATE_CANDIDATES);

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    static public int execAndReturnRowsAffected(PreparedStatement preparedStatement) {

        try {

            return preparedStatement.executeUpdate();

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    static public int execAndReturnGeneratedKey(PreparedStatement preparedStatement) {

        try {

            preparedStatement.execute();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    static public final String SELECT_ASTERISK_MARKED_DUPLICATE_CANDIDATES =
        "SELECT * FROM FILE WHERE CHECKSUM = X'00'\n\n";

    static public final String SELECT_MARKED_DUPLICATE_CANDIDATES_WITH_FILE_ID =
        "SELECT FILE_ID, SCAN_ID, DIR_ID, NAME, PATH, CHECKSUM FROM FILE\n" +
            "WHERE CHECKSUM = X'00'\n\n";

    static private final String CREATE_DUPLICATE_CANDIDATES =
        "CREATE LOCAL TEMPORARY TABLE FILE_DUPLICATE AS\n"
            + "SELECT FILE_ID\n"
            + "FROM FILE\n"
            + "JOIN (\n"
            + "        SELECT NAME, SIZE\n"
            + "        FROM FILE \n"
            + "        GROUP BY SIZE, NAME\n"
            + "        HAVING SIZE > 0 AND COUNT(*) > 1\n"
            + "    ) DUPLICATES\n"
            + "ON FILE.NAME = DUPLICATES.NAME AND FILE.SIZE = DUPLICATES.SIZE\n";

    static private final String MARK_DUPLICATE_CANDIDATES_2 =
        "UPDATE FILE\n"
            + "SET CHECKSUM = X'00'\n"
            + "WHERE FILE_ID IN (\n"
            + "    SELECT FILE_ID\n"
            + "    FROM FILE_DUPLICATE\n"
            + ")\n";


    static public int createDuplicateCandidates(Connection connection) {

        try {

            PreparedStatement preparedStatement;
            preparedStatement = connection.prepareStatement(CREATE_DUPLICATE_CANDIDATES);
            execAndReturnRowsAffected(preparedStatement);
            preparedStatement = connection.prepareStatement(MARK_DUPLICATE_CANDIDATES_2);
            return execAndReturnRowsAffected(preparedStatement);

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

}
