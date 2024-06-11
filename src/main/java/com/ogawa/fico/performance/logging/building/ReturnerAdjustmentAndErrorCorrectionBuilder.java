package com.ogawa.fico.performance.logging.building;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ReturnerAdjustmentAndErrorCorrectionBuilder<
    CORR_AND_ERR_BUILDER extends ReturnerErrorCorrectionBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    FINAL_BUILDER extends ReturnerCurrentStageBuilder<FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    RETURNER_RESULT, BUILD_RESULT>
    extends ReturnerErrorCorrectionBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT> {

    default CORR_AND_ERR_BUILDER adjustIfNull(RETURNER_RESULT nullDefault) {
        return adjustIfNull(() -> nullDefault);
    }

    default CORR_AND_ERR_BUILDER adjustIfNull(Supplier<RETURNER_RESULT> adjustmentSupplier) {
        return adjustIf((result -> result == null), adjustmentSupplier);
    }

    default CORR_AND_ERR_BUILDER adjustIf(Predicate<RETURNER_RESULT> resultPredicate, RETURNER_RESULT defaultValue) {
        return adjustIf(resultPredicate, () -> defaultValue);
    }

    CORR_AND_ERR_BUILDER adjustIf(Predicate<RETURNER_RESULT> resultPredicate,
        Supplier<RETURNER_RESULT> adjustmentSupplier);

}
