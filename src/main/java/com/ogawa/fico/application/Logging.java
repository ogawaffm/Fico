package com.ogawa.fico.application;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Logging {

    private static final String ENV_VAR_RECOGNIZED = "Recognized {}={}";
    static private ch.qos.logback.classic.Logger log;

    private static URL getLogConfigUrl() {
        if (log == null) {
            throw new RuntimeException(Logging.class.getName() + " not initialized!");
        }
        LoggerContext loggerContext = log.getLoggerContext();
        URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(loggerContext);
        return mainURL;
    }

    private static void logVarRecognized(String varName, String varValue) {
        log.debug(ENV_VAR_RECOGNIZED, varName, varValue);
    }

    static void initialize() {

        // uninitialized?
        if (log != null) {
            // do not allow multiple initializations, because duplicate log messages would be created
            // and multiple calls of this method are indication of a programming error
            throw new RuntimeException(Logging.class.getName() + " already initialized!");
        }

        String envLogConfigFile = "";
        String envLogLevel = "";

        boolean explicitConfigFileExist = false;

        // must use logback logger instead of slf4j, because we want to be able to overwrite the log level of the
        // log config file on root level and system property org.slf4j.simpleLogger.defaultLogLevel seems only to
        // work if the log config file has no log level. Overwriting existing log levels does not work.

        // config file cannot be changed after the logger is initialized
        // therefore we have to set the system properties before the logger is initialized

        if (Config.getEnvLogConfigFile() != null && !Config.getEnvLogConfigFile().isEmpty()) {
            envLogConfigFile = Config.getEnvLogConfigFile();
            explicitConfigFileExist = Files.exists(Path.of(envLogConfigFile));
            if (explicitConfigFileExist) {
                System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, envLogConfigFile);
            }
        }

        // now after the configuration file is set, we can initialize the logger
        log = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(
            ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME
        );

        if (Config.getEnvLogLevel() != null && !Config.getEnvLogLevel().isEmpty()) {
            envLogLevel = Config.getEnvLogLevel();
            log.setLevel(Level.toLevel(envLogLevel));
        }

        if (Config.getEnvDebugPort() != null && !Config.getEnvDebugPort().isEmpty()) {
            logVarRecognized(Config.getEnvDebugPortVar(), Config.getEnvDebugPort());
        }

        if (!envLogLevel.isEmpty()) {

            logVarRecognized(Config.getEnvLogLevelVar(), envLogLevel);

            // Did the change of the log level work?
            if (!log.getLevel().toString().equals(envLogLevel)) {
                log.warn("Log level '{}' set in {} variable is not recognized, log level is set to {}"
                    , envLogLevel, Config.getEnvLogLevelVar(), log.getLevel()
                );
            }
        }

        if (!envLogConfigFile.isEmpty()) {

            logVarRecognized(Config.getEnvLogConfigFileVar(), envLogConfigFile);
            if (!explicitConfigFileExist) {
                log.warn("Log configuration file '{}' set in {} variable does not exist",
                    envLogConfigFile, Config.getEnvLogConfigFileVar());
                log.info("Using '{}' as the log configuration file", getLogConfigUrl());
            } else {
                // log the log configuration filename, which can be expanded from a relative path to an absolute path
                log.debug("Using '{}' as the log configuration file", getLogConfigUrl());
            }
        }

    }

}
