package com.ogawa.fico.command.argument;

public interface CommandWithOptionalTwoArgs extends ArgumentCardinality {

    default int getMinGivenArgumentCount() {
        return 2;
    }

    default int getMaxGivenArgumentCount() {
        return 2;
    }

    @Override
    default boolean isAnyArgumentRequired() {
        return false;
    }

}
