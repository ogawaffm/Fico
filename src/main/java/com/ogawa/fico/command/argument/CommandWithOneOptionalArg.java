package com.ogawa.fico.command.argument;

public interface CommandWithOneOptionalArg extends ArgumentCardinality {

    default int getMinGivenArgumentCount() {
        return 0;
    }

    default int getMaxGivenArgumentCount() {
        return 1;
    }

}
