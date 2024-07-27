package com.ogawa.fico.command;

import com.ogawa.fico.application.Application;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.ExecutionError;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseCommand extends Command {

    public DatabaseCommand(String[] commandArguments) {
        super(commandArguments);
        checkConnection();
    }

    public String getDatabaseName() {
        return Application.getDatabaseName();
    }

    /**
     * Check if the database connection can be established and throws a runtime exception if not. The connection is
     * closed after the check.
     */
    private void checkConnection() {
        Connection connection = getConnection();
        try {
            connection.close();
        } catch (SQLException sqlException) {
            throw new ExecutionError("Error checking connection " + getDatabaseName(), sqlException);
        }
    }

    public Connection getConnection() {
        return Util.getConnection(getDatabaseName());
    }

}
