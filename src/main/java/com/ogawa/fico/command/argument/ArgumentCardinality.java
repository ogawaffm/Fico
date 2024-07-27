package com.ogawa.fico.command.argument;

public interface ArgumentCardinality {

    /**
     * Indicates the minimum number of arguments required, if arguments are given.
     *
     * @return
     */
    int getMinGivenArgumentCount();

    /**
     * Indicates the maximum number of arguments required, if arguments are given.
     *
     * @return
     */
    int getMaxGivenArgumentCount();

    /**
     * Indicates whether any arguments are required at all.
     *
     * @return
     */
    default boolean isAnyArgumentRequired() {
        // This default implementation assumes mandatory arguments (if arguments are accepted at all).
        // Override this method in implementing classes to allow an argumentless call or, alternatively
        // the number of arguments in the range of getMinGivenArgumentCount() and getMaxGivenArgumentCount()
        return getMinGivenArgumentCount() > 0;
    }

}
