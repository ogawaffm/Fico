package com.ogawa.fico.application;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

    @Getter
    private final static String name = "FiCo";

    @Getter
    private final static String version = "1.0";

    @Getter
    private static String defaultDatabaseName = "fico";

    private static boolean isDatabaseSetByArgument = false;

    static public boolean isDatabaseSetByArgument() {
        return isDatabaseSetByArgument;
    }


    public static void setDatabaseNameFromArgument(String databaseName) {
        isDatabaseSetByArgument = true;
        Config.setDatabaseName(databaseName);
    }

    public static String getDatabaseName() {
        if (Config.getDatabaseName() == null) {
            if (Config.getEnvDBName() != null && !Config.getEnvDBName().isEmpty()) {
                log.debug("Recognized {}={}", Config.getEnvDBNameVar(), Config.getEnvDBName());
                Config.setDatabaseName(Config.getEnvDBName());
            } else {
                Config.setDatabaseName(getDefaultDatabaseName());
            }
        }
        return Config.getDatabaseName();
    }

}
