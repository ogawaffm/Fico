package com.ogawa.fico.performance.logging.building;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface ReturnerAdjustmentBuilder<
    SELF extends ReturnerAdjustmentBuilder<SELF, ERROR_BUILDER, FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    ERROR_BUILDER extends ReturnerErrorCorrectionBuilder<ERROR_BUILDER, FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    FINAL_BUILDER extends ReturnerCurrentStageBuilder<FINAL_BUILDER, RETURNER_RESULT, BUILD_RESULT>,
    RETURNER_RESULT, BUILD_RESULT>
    extends ReturnerCurrentStageBuilder<SELF, RETURNER_RESULT, BUILD_RESULT> {

    default ERROR_BUILDER adjustIfNull(RETURNER_RESULT nullDefault) {
        return adjustIfNull(() -> nullDefault);
    }

    default ERROR_BUILDER adjustIfNull(Supplier<RETURNER_RESULT> adjustmentSupplier) {
        return adjustIf((result -> result == null), adjustmentSupplier);
    }

    default ERROR_BUILDER adjustIf(Predicate<RETURNER_RESULT> resultPredicate, RETURNER_RESULT defaultValue) {
        return adjustIf(resultPredicate, () -> defaultValue);
    }

    ERROR_BUILDER adjustIf(Predicate<RETURNER_RESULT> resultPredicate, Supplier<RETURNER_RESULT> adjustmentSupplier);

}
