package com.ogawa.fico.command.argument;

public interface CommandWithAtLeastOneArg extends ArgumentCardinality {

    default int getMinGivenArgumentCount() {
        return 1;
    }

    default int getMaxGivenArgumentCount() {
        return Integer.MAX_VALUE;
    }

}
