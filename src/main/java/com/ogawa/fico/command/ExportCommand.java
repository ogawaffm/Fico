package com.ogawa.fico.command;

import static com.ogawa.fico.misc.System.createPrintStream;

import com.ogawa.fico.command.argument.CommandWithExactTwoArgs;
import com.ogawa.fico.db.ResultSetExporter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportCommand extends TableReferencingCommand implements CommandWithExactTwoArgs {

    final static public String KEY_WORD = "export";

    ExportCommand(String[] commandArguments) {
        super(commandArguments);
        checkTableName();
        checkFilename();
    }

    @Override
    String getTableName() {
        return getArgument(0);
    }

    String getFilename() {
        return getArgument(1);
    }

    private void checkFilename() {
        try {
            Paths.get(getFilename());
        } catch (InvalidPathException invalidPathException) {
            throwExecutionError(invalidPathException);
        }
    }

    public String getName() {
        return ExportCommand.KEY_WORD;
    }

    @Override
    public void execute() {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getTableSelect());

            ResultSet resultSet = preparedStatement.executeQuery()) {

            PrintStream filePrintStream = createPrintStream(getFilename());

            ResultSetExporter resultSetExporter = new ResultSetExporter(
                resultSet, filePrintStream, "\n", "\t", ""
            );
            resultSetExporter.write();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException ioException) {
            // file is a directory or cannot be created (e.g. due to permissions, disk is full, or read-only state)
            throwExecutionError(ioException);
        }
    }
}
