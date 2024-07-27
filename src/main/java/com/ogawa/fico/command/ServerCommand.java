package com.ogawa.fico.command;

import lombok.NonNull;

public abstract class ServerCommand extends Command {

    /**
     * @param commandFollowingArgs The arguments after the command name, may including an optional preceding database
     *                             name
     */
    ServerCommand(@NonNull String[] commandFollowingArgs) {
        super(commandFollowingArgs);
    }

}
