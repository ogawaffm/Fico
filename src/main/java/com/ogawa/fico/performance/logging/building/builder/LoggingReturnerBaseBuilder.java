package com.ogawa.fico.performance.logging.building.builder;

public abstract class LoggingReturnerBaseBuilder<CURR_BUILDER extends LoggingReturnerBaseBuilder<CURR_BUILDER>>
    extends LoggingActionBaseBuilder<CURR_BUILDER> {

    LoggingReturnerBaseBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    LoggingReturnerBaseBuilder(AbstractFunctionBaseBuilder abstractFunctionBaseBuilder) {
        super(abstractFunctionBaseBuilder);
    }

    public CURR_BUILDER result(String resultName) {
        return super.result(resultName);
    }

    public CURR_BUILDER result() {
        return super.result();
    }

}
