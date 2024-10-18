package com.ogawa.fico.db;

import com.ogawa.fico.application.Config;
import com.ogawa.fico.jdbc.JdbcTransferor;
import com.ogawa.fico.performance.logging.Formatter;
import com.ogawa.fico.performance.measuring.StopWatch;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    static public <T> T getValue(Connection con, String sql, T defaultValue, Object... params) {
        List values = getValueList(con, sql, 1);
        return values.isEmpty() ? defaultValue : (T) values.get(0);
    }

    static public List getValueList(Connection con, String sql, int columnIndex, Object... params) {

        List result = new ArrayList<>();

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {

            if (params != null && params.length > 0) {
                JdbcTransferor.setBindVars(preparedStatement, params);
            }
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                result.add(rs.getObject(columnIndex));
            }

        } catch (SQLException ignore) {
        }

        return result;

    }

    static JDBCType getJdbcType(Class<?> clazz) {
        if (clazz == null) {
            return JDBCType.NULL;
        } else if (clazz == Boolean.class) {
            return JDBCType.BOOLEAN;
        } else if (clazz == Byte.class) {
            return JDBCType.TINYINT;
        } else if (clazz == Short.class) {
            return JDBCType.SMALLINT;
        } else if (clazz == Integer.class) {
            return JDBCType.INTEGER;
        } else if (clazz == Long.class) {
            return JDBCType.BIGINT;
        } else if (clazz == String.class) {
            return JDBCType.VARCHAR;
        } else if (clazz == byte[].class) {
            return JDBCType.BINARY;
        } else if (clazz == java.sql.Date.class) {
            return JDBCType.DATE;
        } else if (clazz == java.sql.Time.class) {
            return JDBCType.TIME;
        } else if (clazz == java.sql.Timestamp.class) {
            return JDBCType.TIMESTAMP;
        } else {
            return JDBCType.OTHER;
        }
    }

    static Array createSqlArray(Connection connection, JDBCType jdbcType, Object[] array) {
        try {
            return connection.createArrayOf(jdbcType.getName(), array);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static public Array createSqlArray(Connection connection, Object[] array) {
        if (array == null) {
            return null;
        } else {
            JDBCType jdbcType = getJdbcType(array.getClass().getComponentType());
            return createSqlArray(connection, jdbcType, array);
        }
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
                throw new SQLException("Error executing command no #" + scriptNo + " of script " + batchScriptName,
                    sqlException);
            } else {
                throw sqlException;
            }
        } finally {
            statement.close();
        }

    }

    static public long execute(Connection connection, String sql, String itemCountInTimeMessage, Object... params) {

        PreparedStatement preparedStatement;
        long recordAffected;

        StopWatch stopWatch = StopWatch.create();
        stopWatch.start();

        try {
            preparedStatement = connection.prepareStatement(sql);
            JdbcTransferor.setBindVars(preparedStatement, params);
            recordAffected = execAndReturnRowsAffected(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        stopWatch.stop();

        log.debug(itemCountInTimeMessage, recordAffected, Formatter.format(stopWatch.getAccumulatedRecordedTime()));

        return recordAffected;

    }

    static public long executeIteratively(Connection connection, String sql,
        String updateStepCountTimeMessage, String finalMessageWithStepNoTime,
        String totalUpdateCountStepCountTimeMessage,
        Object... params) {

        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null) {
                JdbcTransferor.setBindVars(preparedStatement, params);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return executeIteratively(preparedStatement, updateStepCountTimeMessage, finalMessageWithStepNoTime,
            totalUpdateCountStepCountTimeMessage);

    }

    static public long executeIteratively(PreparedStatement preparedStatement,
        String updateStepCountTimeMessage, String finalStepMessageWithStepNoTime,
        String totalUpdateCountStepCountTimeMessage
    ) {

        long updateStepNo = 0;
        long totalUpdatedCount = 0;
        long updatedCount;

        StopWatch stopWatch = StopWatch.create();
        stopWatch.pause();

        do {
            updateStepNo++;

            stopWatch.resume();
            updatedCount = execAndReturnRowsAffected(preparedStatement);
            try {
                preparedStatement.getConnection().commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            stopWatch.pause();

            totalUpdatedCount = totalUpdatedCount + updatedCount;

            if (updatedCount > 0) {
                log.debug(updateStepCountTimeMessage,
                    updateStepNo, updatedCount, Formatter.format(stopWatch.getLastRecordedTime())
                );
            } else {
                log.debug(finalStepMessageWithStepNoTime,
                    updateStepNo, Formatter.format(stopWatch.getLastRecordedTime())
                );
            }

        } while (updatedCount > 0);

        stopWatch.stop();

        closeSilently(preparedStatement);

        log.info(totalUpdateCountStepCountTimeMessage,
            totalUpdatedCount,
            updateStepNo,
            Formatter.format(stopWatch.getAccumulatedRecordedTime())
        );

        return totalUpdatedCount;

    }

    /**
     * Closes the given AutoCloseable silently. AutoCloseable for example are Connection, Statement, ResultSet
     *
     * @param autoCloseable AutoCloseable
     */
    static public void closeSilently(AutoCloseable autoCloseable) {
        try {
            autoCloseable.close();
        } catch (Exception ignore) {
        }
    }

}
