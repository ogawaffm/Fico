package com.ogawa.fico.command.argument;

public interface CommandWithExactTwoArgs extends ArgumentCardinality {

    default int getMinArgumentCount() {
        return 2;
    }

    default int getMaxArgumentCount() {
        return 2;
    }

}
