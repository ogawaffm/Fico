package com.ogawa.fico.db;

import static com.ogawa.fico.db.Util.closeSilently;
import static org.h2.api.ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1;

import com.ogawa.fico.exception.ModelError;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Model {

    private final Connection connection;

    public Model(Connection connection) {
        this.connection = connection;
    }

    private boolean tableExists(String tableName) {
        try (Statement statement = connection.createStatement();) {
            statement.execute("SELECT * FROM \"" + tableName + "\"\n WHERE 1 = 2");
            statement.close();
            return true;
        } catch (SQLException ignore) {
            return false;
        }
    }

    public void create() {
        try {
            Util.executeBatch(connection, "CreateModel");
        } catch (SQLException sqlException) {
            if (sqlException.getErrorCode() == TABLE_OR_VIEW_ALREADY_EXISTS_1) {
                throw new ModelError("Model already exists. Drop model first and create again or reset model.");
            } else {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public void drop() {
        try {
            Util.executeBatch(connection, "DropModel");
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public void reset() {
        drop();
        create();
    }

    public void close() {
        closeSilently(connection);
    }

}
