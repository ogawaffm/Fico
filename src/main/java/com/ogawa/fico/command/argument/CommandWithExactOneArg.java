package com.ogawa.fico.command.argument;

public interface CommandWithExactOneArg extends ArgumentCardinality {

    default int getMinArgumentCount() {
        return 1;
    }

    default int getMaxArgumentCount() {
        return 1;
    }

}
