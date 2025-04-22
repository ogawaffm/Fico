package com.ogawa.fico.application;

import static com.ogawa.fico.db.Util.closeSilently;

import com.ogawa.fico.db.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

// No @Slf4j annotation allowed in this file because configuration of the logger is not done yet
public class Config {

    private static int DEFAULT_SERVER_PORT = 9092;

    private static String SERVER_SHUTDOWN_PASSWORD = "fico";

    private static String ENV_DEBUG_PORT_VAR = "FICO_DEBUG_PORT";

    private static String ENV_LOG_CONFIG_FILE_VAR = "FICO_LOG_CONFIG_FILE";
    private static String ENV_LOG_LEVEL_VAR = "FICO_LOG_LEVEL";

    private static String ENV_DB_NAME = "FICO_DB_NAME";

    private static String databaseName;

    static public int getDefaultServerPort() {
        return DEFAULT_SERVER_PORT;
    }

    static public String getServerShutdownPassword() {
        return SERVER_SHUTDOWN_PASSWORD;
    }

    static protected void setDatabaseName(String databaseName) {
        Config.databaseName = databaseName;
    }

    static protected String getDatabaseName() {
        return databaseName;
    }

    static public String getEnvDBNameVar() {
        return ENV_DB_NAME;
    }

    static public String getEnvDBName() {
        return System.getenv(getEnvDBNameVar());
    }

    static public String getEnvLogConfigFileVar() {
        return ENV_LOG_CONFIG_FILE_VAR;
    }

    static public String getEnvLogConfigFile() {
        return System.getenv(getEnvLogConfigFileVar());
    }

    static public String getEnvLogLevelVar() {
        return ENV_LOG_LEVEL_VAR;
    }

    static public String getEnvLogLevel() {
        return System.getenv(getEnvLogLevelVar());
    }

    static public String getEnvDebugPort() {
        return System.getenv(getEnvDebugPortVar());
    }

    static public String getEnvDebugPortVar() {
        return ENV_DEBUG_PORT_VAR;
    }

    public static String getResource(String filename) {

        int bufferSize = 8192;
        char[] buffer = new char[bufferSize];

        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = Util.class.getClassLoader().getResourceAsStream(filename);

        if (inputStream == null) {
            throw new RuntimeException("Could not find file " + filename);
        }

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            for (int numRead; (numRead = reader.read(buffer, 0, buffer.length)) > 0; ) {
                stringBuilder.append(buffer, 0, numRead);
            }

        } catch (IOException ioException) {
            throw new RuntimeException("Could not read file " + filename, ioException);
        } finally {
            closeSilently(inputStream);
        }

        return stringBuilder.toString();

    }

}
