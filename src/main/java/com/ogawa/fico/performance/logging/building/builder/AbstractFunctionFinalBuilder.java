package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.building.ReturnerCurrentStageBuilder;
import java.util.function.Function;

public abstract class AbstractFunctionFinalBuilder<
    FUNC_ARG,
    FUNC_RESULT,
    CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, FUNC_ARG, FUNC_RESULT>,
    FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>
    >
    extends AbstractFunctionBaseBuilder<
    FINAL_BUILDER, FUNC_ARG, FUNC_RESULT, Function<FUNC_ARG, FUNC_RESULT>, CORR_AND_ERR_BUILDER, FINAL_BUILDER
    >
    implements ReturnerCurrentStageBuilder<FINAL_BUILDER, FUNC_RESULT, Function<FUNC_ARG, FUNC_RESULT>> {

    AbstractFunctionFinalBuilder(AbstractFunctionBaseBuilder abstractFunctionBaseBuilder) {
        super(abstractFunctionBaseBuilder);
    }

    @Override
    public FINAL_BUILDER clone() {
        return createFunctionFinalBuilder((FINAL_BUILDER) this);
    }

}
