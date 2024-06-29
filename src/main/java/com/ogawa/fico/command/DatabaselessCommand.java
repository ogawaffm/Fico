package com.ogawa.fico.command;

import lombok.NonNull;

public abstract class DatabaselessCommand extends Command {

    /**
     * @param commandFollowingArgs The arguments after the command name, may including an optional preceding database
     *                             name
     */
    DatabaselessCommand(@NonNull String[] commandFollowingArgs) {
        super(commandFollowingArgs);
    }

    public boolean usesDatabase() {
        return false;
    }

}
