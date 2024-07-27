package com.ogawa.fico.command;

import com.ogawa.fico.command.argument.CommandWithExactOneArg;
import com.ogawa.fico.db.ResultSetTabulator;
import com.ogawa.fico.performance.logging.Formatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListCommand extends TableReferencingCommand implements CommandWithExactOneArg {

    final static public String KEY_WORD = "list";

    ListCommand(String[] commandArguments) {
        super(commandArguments);
        checkTableName();
    }

    @Override
    String getTableName() {
        return getArgument(0);
    }

    public String getName() {
        return ListCommand.KEY_WORD;
    }


    @Override
    public void execute() {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getTableSelect());

            ResultSet resultSet = preparedStatement.executeQuery()) {

            ResultSetTabulator resultSetTabulator = new ResultSetTabulator(
                resultSet, System.out, "\n", "|", "␀"
            );

            log.info("Listed table " + getTableName() + " with "
                + Formatter.format(resultSetTabulator.write()) + " rows"
            );

            log.debug("DEBUG!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
