package com.ogawa.fico.performance.logging.building;

public interface BatchableBuilder {

    /**
     * Build an eternal batch for a runnable.
     */
    void batch();

}
