package com.ogawa.fico.scan;

import com.ogawa.fico.db.Util;
import java.sql.Connection;
import java.util.concurrent.Callable;

public abstract class FileScanner implements Callable<Long> {

    private final String databaseName;

    FileScanner(String databaseName) {
        this.databaseName = databaseName;
    }

    String getDatabaseName() {
        return databaseName;
    }

    Connection getConnection() {
        return Util.getConnection(databaseName);
    }

}
