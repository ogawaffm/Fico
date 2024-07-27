package com.ogawa.fico.command;

import com.ogawa.fico.application.ChecksumCalcService;
import com.ogawa.fico.command.argument.CommandWithNoArgs;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

// TODO: Make scanId variable
@Slf4j
public class AnalyzeCommand extends DatabaseCommand implements CommandWithNoArgs {

    static final public String KEY_WORD = "analyze";

    AnalyzeCommand(String[] commandArguments) {
        super(commandArguments);
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    public void execute() {

        ChecksumCalcService checkSummer = new ChecksumCalcService();
        Connection connection = getConnection();

        try {
            checkSummer.calc(connection);

            log.info("Analyzed " + checkSummer.getFileCount() + " files"
                + " and " + checkSummer.getDirCount() + " directories");
            // TODO commit is set by BATCHER
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
