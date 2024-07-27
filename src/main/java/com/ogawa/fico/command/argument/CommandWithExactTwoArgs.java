package com.ogawa.fico.command.argument;

public interface CommandWithExactTwoArgs extends ArgumentCardinality {

    default int getMinGivenArgumentCount() {
        return 2;
    }

    default int getMaxGivenArgumentCount() {
        return 2;
    }

}
