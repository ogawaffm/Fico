package com.ogawa.fico.performance.logging.building.builder;

public class FunctionBuilder<
    FUNC_ARG,
    FUNC_RESULT,
    CURR_BUILDER extends FunctionBuilder<FUNC_ARG, FUNC_RESULT, CURR_BUILDER, CORR_AND_ERR_BUILDER, FINAL_BUILDER>,
    CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, FUNC_ARG, FUNC_RESULT>,
    FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>
    > extends AbstractFunctionBuilder<FUNC_ARG, FUNC_RESULT, CURR_BUILDER, CORR_AND_ERR_BUILDER, FINAL_BUILDER> {

    FunctionBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    @Override
    CURR_BUILDER createFunctionBuilder(CURR_BUILDER functionBuilder) {
        return (CURR_BUILDER) new FunctionBuilder<>(this);
    }

    @Override
    CORR_AND_ERR_BUILDER createFunctionErrorCorrectionBuilder(CURR_BUILDER abstractFunctionBuilder) {
        return (CORR_AND_ERR_BUILDER) new FunctionErrCorrBuilder<>(this);
    }

    @Override
    FINAL_BUILDER createFunctionFinalBuilder(CURR_BUILDER abstractFunctionBuilder) {
        return null;
    }

}
