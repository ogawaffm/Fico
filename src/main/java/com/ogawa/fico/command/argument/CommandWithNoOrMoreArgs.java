package com.ogawa.fico.command.argument;

public interface CommandWithNoOrMoreArgs extends ArgumentCardinality {

    default int getMinGivenArgumentCount() {
        return 0;
    }

    default int getMaxGivenArgumentCount() {
        return Integer.MAX_VALUE;
    }

}
