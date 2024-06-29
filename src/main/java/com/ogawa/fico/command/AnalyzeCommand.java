package com.ogawa.fico.command;

import com.ogawa.fico.application.ChecksumCalcService;
import com.ogawa.fico.command.argument.CommandWithNoArgs;

// TODO: Make scanId variable
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

        checkSummer.calc(getConnection());

        System.out.print("Analyzed " + checkSummer.getFileCount() + " files");
        System.out.println(" and " + checkSummer.getDirCount() + " directories");
    }
}
