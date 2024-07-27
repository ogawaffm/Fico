package com.ogawa.fico.command.argument;

public interface CommandWithExactOneArg extends ArgumentCardinality {

    default int getMinGivenArgumentCount() {
        return 1;
    }

    default int getMaxGivenArgumentCount() {
        return 1;
    }

}
