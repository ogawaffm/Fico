package com.ogawa.fico.db;

import com.ogawa.fico.application.Config;
import com.ogawa.fico.jdbc.JdbcTransferor;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

    public static Connection getConnection(String databaseName) {
        String url = "";
        Connection connection;
        try {
/*
            connection = DriverManager.getConnection(
                "jdbc:h2:tcp:mem//localhost/~/" + databaseName + ";TRACE_LEVEL_FILE=2", "sa", "");
                   jdbc:h2:tcp://localhost/mem:db1 */
            url = "jdbc:h2:tcp://localhost/mem:" + databaseName + ";DB_CLOSE_DELAY=-1";
            connection = DriverManager.getConnection(url, "sa", "");
        } catch (SQLException sqlException) {
            log.debug(sqlException.getMessage());
            log.info("Could not connect to {} using url {}. Trying to connect to local file ...", databaseName,
                url.toString());
            try {
//                connection = DriverManager.getConnection("jdbc:h2:mem:~/" + databaseName + ";DB_CLOSE_DELAY=-1", "sa", "");
                url = "jdbc:h2:~/" + databaseName + ";DB_CLOSE_DELAY=-1";
                connection = DriverManager.getConnection(url, "sa", "");
            } catch (SQLException exception) {
                String msg = "Could not connect to {} using url {}";
                log.error(msg + ": " + exception.getMessage());
                throw new RuntimeException(msg, exception);
            }

        }
        log.info("Connected to {} using url {}", databaseName, url);
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

    static public long execute(Connection connection, String sql, String pluralItemName) {

        PreparedStatement preparedStatement;
        long recordAffected;

        StopWatch stopWatch = StopWatch.create();
        stopWatch.start();

        try {
            preparedStatement = connection.prepareStatement(sql);
            recordAffected = execAndReturnRowsAffected(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        stopWatch.stop();

        log.debug("Updated {} {} in {}", recordAffected, pluralItemName,
            Formatter.format(stopWatch.getAccumulatedRecordedTime()));

        return recordAffected;

    }

    static public long executeIteratively(Connection connection, String sql, String pluralItemName) {

        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return executeIteratively(preparedStatement, pluralItemName);

    }

    static public long executeIteratively(Connection connection, String sql, String pluralItemName, Object... params) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            JdbcTransferor.setBindVars(preparedStatement, params);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return executeIteratively(preparedStatement, pluralItemName);

    }

    static public long executeIteratively(PreparedStatement preparedStatement, String pluralItemName) {

        long updateStepNo = 0;
        long totalUpdatedDirCount = 0;
        long updatedDirCount;

        StopWatch stopWatch = StopWatch.create();
        stopWatch.pause();

        do {
            updateStepNo++;

            stopWatch.resume();
            updatedDirCount = execAndReturnRowsAffected(preparedStatement);
            try {
                preparedStatement.getConnection().commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            stopWatch.pause();

            totalUpdatedDirCount = totalUpdatedDirCount + updatedDirCount;

            if (updatedDirCount > 0) {
                log.debug("Step #{} updated {} {} in {}",
                    updateStepNo, updatedDirCount, pluralItemName, Formatter.format(stopWatch.getLastRecordedTime())
                );
            } else {
                log.debug("Step #{} (final check step) took {}",
                    updateStepNo, Formatter.format(stopWatch.getLastRecordedTime())
                );
            }

        } while (updatedDirCount > 0);

        stopWatch.stop();

        closeSilently(preparedStatement);

        log.info("Updated {} {} in total in {} steps in {}",
            totalUpdatedDirCount, pluralItemName, updateStepNo, Formatter.format(stopWatch.getAccumulatedRecordedTime())
        );

        return totalUpdatedDirCount;

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

}
