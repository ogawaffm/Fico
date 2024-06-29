package com.ogawa.fico.command.argument;

public interface CommandWithNoArgs extends ArgumentCardinality {


    default int getMinArgumentCount() {
        return 0;
    }

    default int getMaxArgumentCount() {
        return 0;
    }

}
