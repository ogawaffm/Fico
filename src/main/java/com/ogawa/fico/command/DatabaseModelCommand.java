package com.ogawa.fico.command;

import com.ogawa.fico.application.Config;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.CommandLineError;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for DatabaseCommand that maintains a database model and which requires only the database name as
 * argument.
 */
public abstract class DatabaseModelCommand extends DatabaseCommand {

    public DatabaseModelCommand(String[] commandArguments) {
        super(commandArguments);

        checkConnection();
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
            throw new CommandLineError("Cannot connect to '" + getDatabaseName() + "':" + sqlException);
        }
    }

    public String getDatabaseName() {
        return Config.getDatabaseName();
    }

    public Connection getConnection() {
        return Util.getConnection(getDatabaseName());
    }

}
