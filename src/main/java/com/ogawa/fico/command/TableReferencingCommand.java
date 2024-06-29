package com.ogawa.fico.command;

import java.util.List;

public abstract class TableReferencingCommand extends DatabaseCommand {

    TableReferencingCommand(String[] commandArguments) {
        super(commandArguments);
    }

    abstract String getTableName();

    private final static List<String> allowedTableNames = List.of("file", "scan", "scan_stat");

    private boolean isAllowedTableName(String tableName) {
        return allowedTableNames.contains(tableName);
    }

    void checkTableName() {
        if (!isAllowedTableName(getTableName())) {
            throwExecutionError("Invalid table name. Only "
                + allowedTableNames.stream().reduce((a, b) -> a + ", " + b).orElse("")
                + " are allowed, but got: " + getTableName());
        }
    }

    String getTableSelect() {
        // use check first to avoid SQL injection
        checkTableName();
        return "SELECT * FROM " + getTableName();
    }


}
