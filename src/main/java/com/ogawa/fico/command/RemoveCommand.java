package com.ogawa.fico.command;

import com.ogawa.fico.command.argument.CommandWithAtLeastOneArg;
import com.ogawa.fico.db.Util;
import com.ogawa.fico.exception.ExecutionError;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoveCommand extends DatabaseCommand implements CommandWithAtLeastOneArg {

    static final public String KEY_WORD = "remove";

    private Set<Long> scanIds;

    RemoveCommand(String[] commandArguments) {
        super(commandArguments);
    }

    private Set<Long> checkAndBuildScanList() {

        long scanId;
        Set<Long> scanIds = new LinkedHashSet<>();

        if (getArgumentCount() == 1 && getArgument(0).equals("all")) {
            scanIds.addAll(Util.getValueList(getConnection(), Util.getSql("SelectAllScanIds"), 1));
        } else {

            // check arguments to be numeric and build list of scan ids
            for (int i = 0; i <= getArgumentCount() - 1; i++) {
                String scanIdString = getArgument(i);
                try {
                    scanId = Integer.parseInt(scanIdString);
                    scanIds.add(scanId);
                } catch (NumberFormatException numberFormatException) {
                    throw new ExecutionError("Invalid scan id: " + scanIdString);
                }
            }

            // check and remove non-existing scan ids
            long[] numericScanIds = new long[scanIds.size()];
            numericScanIds = scanIds.stream().mapToLong(Long::longValue).toArray();
            for (int i = 0; i <= getArgumentCount() - 1; i++) {

                scanId = numericScanIds[i];
                boolean exists =
                    1 == Util.getValue(getConnection(), "SELECT 1 FROM SCAN WHERE SCAN_ID = " + scanId, 0);
                if (!exists) {
                    log.warn("Scan id " + scanId + " does not exist");
                    scanIds.remove(scanId);
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

        scanIds = checkAndBuildScanList();

        if (scanIds.isEmpty()) {
            log.info("No scans to remove");
            return;
        }

        String idList = String.join(", ", scanIds.stream().map(Object::toString).toArray(String[]::new));
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = getConnection().prepareStatement(
                "DELETE FROM SCAN WHERE SCAN_ID IN (" + idList + ")"
            );

            long deletedScans = Util.execAndReturnRowsAffected(preparedStatement);
            log.info("Deleted " + deletedScans + " scan" + (deletedScans != 1 ? "s" : ""));


        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            Util.closeSilently(preparedStatement);
        }
    }
}

