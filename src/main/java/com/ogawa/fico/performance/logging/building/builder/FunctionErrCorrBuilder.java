package com.ogawa.fico.performance.logging.building.builder;

public class FunctionErrCorrBuilder<
    CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, FUNC_ARG, FUNC_RESULT>,
    FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>,
    FUNC_ARG,
    FUNC_RESULT> extends
    AbstractFunctionErrCorrBuilder<
        CORR_AND_ERR_BUILDER,
        FINAL_BUILDER,
        FUNC_ARG, FUNC_RESULT> {

    public FunctionErrCorrBuilder(AbstractFunctionBaseBuilder abstractFunctionBaseBuilder) {
        super(abstractFunctionBaseBuilder);
    }

    @Override
    CORR_AND_ERR_BUILDER createFunctionBuilder(CORR_AND_ERR_BUILDER functionBuilder) {
        return null;
    }

    @Override
    CORR_AND_ERR_BUILDER createFunctionErrorCorrectionBuilder(CORR_AND_ERR_BUILDER abstractFunctionBuilder) {
        return null;
    }

    @Override
    FINAL_BUILDER createFunctionFinalBuilder(CORR_AND_ERR_BUILDER abstractFunctionBuilder) {
        return null;
    }

}
