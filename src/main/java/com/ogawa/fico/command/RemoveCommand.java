package com.ogawa.fico.command;

import com.ogawa.fico.command.argument.CommandWithAtLeastOneArg;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.CommandLineError;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.Set;

public class RemoveCommand extends DatabaseCommand implements CommandWithAtLeastOneArg {

    static final public String KEY_WORD = "remove";

    private Set<Long> scanIds;

    RemoveCommand(String[] commandArguments) {
        super(commandArguments);
        scanIds = checkAndBuildScanList();
    }

    private Set<Long> checkAndBuildScanList() {

        Set<Long> scanIds = new LinkedHashSet<>();

        if (getArgumentCount() == 2 && getArgument(0).equals("all")) {
            scanIds.addAll(Util.getValueList(getConnection(), Util.getSql("SelectAllScanIds"), 1));
        } else {

            for (int i = 0; i < getArgumentCount() - 1; i++) {
                String scanIdString = getArgument(i);
                try {
                    long scanId = Integer.parseInt(scanIdString);
                    scanIds.add(scanId);
                } catch (NumberFormatException numberFormatException) {
                    throw new CommandLineError("Invalid scan id: " + scanIdString);
                }
            }
        }
        return scanIds;
    }

    @Override
    public String getName() {
        return KEY_WORD;
    }

    public void execute() {

        String idList = String.join(", ", scanIds.stream().map(Object::toString).toArray(String[]::new));
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = getConnection().prepareStatement(
                "DELETE FROM SCAN WHERE SCAN_ID IN (" + idList + ")"
            );

            long deletedScans = Util.execAndReturnRowsAffected(preparedStatement);
            System.out.println("Deleted scans: " + deletedScans);


        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            Util.closeSilently(preparedStatement);
        }
    }
}

