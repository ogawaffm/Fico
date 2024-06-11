package com.ogawa.fico.performance.logging.building;

import com.ogawa.fico.performance.logging.messageset.ReturnerMessageSetBuilderInterface;
import java.util.function.Supplier;

public interface ReturnerErrorCorrectionBuilder<
    CORR_AND_ERR_BUILDER extends ReturnerErrorCorrectionBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    FINAL_BUILDER extends ReturnerCurrentStageBuilder<FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    RETURNER_RESULT,
    BUILD_RESULT
    > extends ReturnerCurrentStageBuilder<CORR_AND_ERR_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    CommonErrorCorrectionBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, BUILD_RESULT>,
    ReturnerMessageSetBuilderInterface<CORR_AND_ERR_BUILDER> {

    default FINAL_BUILDER correctError(RETURNER_RESULT errorDefault) {
        return correctError(() -> errorDefault);
    }

    FINAL_BUILDER correctError(Supplier<RETURNER_RESULT> errorCorrectionSupplier);

}
