package com.ogawa.fico.db.persistence.beanreader;

import static com.ogawa.fico.db.Util.closeSilently;
import static com.ogawa.fico.jdbc.JdbcTransferor.resultSetToObjectArray;

import com.ogawa.fico.db.persistence.rowmapper.RowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Abstract class for reading bean from a {@link ResultSet} based on a SQL query using a {@link RowMapper}. This reader
 * does not open the {@link ResultSet}. This is done by the implementing class.
 */
public abstract class BaseBeanReader<B> implements BeanReader<B> {

    PreparedStatement preparedStatement;
    ResultSet resultSet;
    final RowMapper<B> rowMapper;

    BaseBeanReader(Connection connection, String selectSql, int fetchSize, RowMapper<B> rowMapper) {
        try {
            preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setFetchSize(fetchSize);
        } catch (SQLException exception) {
            throw new RuntimeException("Could not create " + getClass().getName(), exception);
        }
        this.rowMapper = rowMapper;
    }

    /**
     * Executes the query and moves to the first row in the {@link ResultSet}.
     *
     * @throws SQLException
     */
    void open() throws SQLException {
        resultSet = preparedStatement.executeQuery();
        move();
    }

    /**
     * Move to the next row in the {@link ResultSet}. If there are no more rows, then the ResultSet is closed.
     *
     * @throws SQLException
     */
    void move() throws SQLException {
        if (!resultSet.next()) {
            close();
        }
    }

    /**
     * Read the current row from the {@link ResultSet} and map it to a bean. Then move to the next row.
     *
     * @return
     * @throws SQLException
     */
    public B read() {
        // Have all rows been read?
        if (isClosed()) {
            return null;
        } else {
            B bean;
            try {
                Object[] row = resultSetToObjectArray(resultSet);
                bean = rowMapper.toObject(row);
                move();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return bean;
        }
    }

    @Override
    public boolean isClosed() {
        return resultSet == null;
    }

    public void close() {
        closeSilently(resultSet);
        // indicate all rows have been read
        resultSet = null;

        closeSilently(preparedStatement);
    }

}
