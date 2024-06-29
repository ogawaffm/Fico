package com.ogawa.fico.command;

import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.CommandLineError;
import com.ogawa.fico.exception.ExecutionException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseCommand extends Command {

    public DatabaseCommand(String[] commandArguments) {

        super(commandArguments);
        checkConnection();
    }

    @Override
    boolean usesDatabase() {
        return true;
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
            throw new ExecutionException("Error checking connection " + getDatabaseName(), sqlException);
        }
    }

    public Connection getConnection() {
        return Util.getConnection(getDatabaseName());
    }

}
