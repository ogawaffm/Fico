package com.ogawa.fico.command.argument;

public interface CommandWithAtLeastOneArg extends ArgumentCardinality {

    default int getMinArgumentCount() {
        return 1;
    }

    default int getMaxArgumentCount() {
        return Integer.MAX_VALUE;
    }

}
