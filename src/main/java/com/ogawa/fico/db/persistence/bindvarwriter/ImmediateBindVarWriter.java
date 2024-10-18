package com.ogawa.fico.db.persistence.bindvarwriter;

import static com.ogawa.fico.db.Util.closeSilently;

import com.ogawa.fico.jdbc.JdbcTransferor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class ImmediateBindVarWriter implements BindVarWriter {

    private boolean returnedResultSet = false;
    final private PreparedStatement preparedStatement;

    private Consumer<Object[]> generatedKeyConsumer = null;

    public ImmediateBindVarWriter(Connection connection, String sqlWithBindVariables) {
        try {
            this.preparedStatement = connection.prepareStatement(
                sqlWithBindVariables, PreparedStatement.RETURN_GENERATED_KEYS
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGeneratedKeyHandler(Consumer<Object[]> generatedKeyConsumer) {
        this.generatedKeyConsumer = generatedKeyConsumer;
    }

    @Override
    public void write(Object[] var) {
        try {
            JdbcTransferor.setBindVars(preparedStatement, var);
            preparedStatement.execute();
            if (generatedKeyConsumer != null) {
                Object[] generatedKey = null;
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedKey = JdbcTransferor.resultSetToObjectArray(generatedKeys);
                    generatedKeyConsumer.accept(generatedKey);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        closeSilently(preparedStatement);
    }

    @Override
    public boolean isClosed() {
        boolean isClose;
        try {
            isClose = preparedStatement.isClosed();
        } catch (SQLException ignore) {
            isClose = true;
        }
        return isClose;
    }
}
