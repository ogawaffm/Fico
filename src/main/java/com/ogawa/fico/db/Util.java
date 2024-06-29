package com.ogawa.fico.db;

import com.ogawa.fico.application.Config;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

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

    static public long execAndReturnRowsAffected(PreparedStatement preparedStatement) {

        try {

            return preparedStatement.executeLargeUpdate();

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    static public long execAndReturnGeneratedKey(PreparedStatement preparedStatement) {

        try {

            preparedStatement.execute();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }

        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }


    /**
     * Reads the sql file from the resources/sql folder and returns the sql string
     *
     * @param sqlName name of the sql file in the resources/sql folder without the .sql extension
     * @return the sql string from the file
     */
    public static String getSql(String sqlName) {
        return Config.getResource("sql/" + sqlName + ".sql");
    }

    public static void executeBatch(Connection connection, String batchScriptName) throws SQLException {

        Statement statement = connection.createStatement();

        String batch = Util.getSql(batchScriptName);

        String[] sqls = batch.split("[ \\t]*\\n[ \\t]*\\n");

        int scriptNo = 0;
        try {
            for (String sql : sqls) {
                scriptNo++;
                statement.execute(sql);
            }
        } catch (SQLException sqlException) {
            if (sqls.length > 1) {
                throw new SQLException("Error executing script no #" + scriptNo + " of " + batchScriptName,
                    sqlException);
            } else {
                throw sqlException;
            }
        } finally {
            statement.close();
        }

    }

    static public void closeSilently(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException ignore) {
        }
    }

    static public void closeSilently(Statement statement) {
        try {
            statement.close();
        } catch (SQLException ignore) {
        }
    }

    static public void closeSilently(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignore) {
        }
    }

    static String getDirectoryName(Path path) {
        if (path == null || path.getParent() == null) {
            return "";
        } else {
            return path.getParent().toString();
        }
    }

    static String getFilename(Path path) {
        if (path == null || path.getFileName() == null) {
            return "";
        } else {
            return path.getFileName().toString();
        }
    }

    static Path getFullPath(String path, String name) {
        if (path == null || name == null) {
            return null;
        } else {
            return Path.of(path, name);
        }
    }

    static Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return Timestamp.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

    static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return timestamp.toLocalDateTime();
        }
    }
}
