package com.ogawa.fico.performance.logging.building;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface FunctionAdjustmentAndErrorCorrectionBuilder<
    SELF extends FunctionAdjustmentAndErrorCorrectionBuilder<SELF, ERROR_BUILDER, FINAL_BUILDER, A, R, B>,
    ERROR_BUILDER extends ReturnerErrorCorrectionBuilder<ERROR_BUILDER, FINAL_BUILDER, R, B>,
    FINAL_BUILDER extends ReturnerCurrentStageBuilder<FINAL_BUILDER, R, B>,
    A, R, B extends Function<A, R>>
    extends ReturnerAdjustmentAndErrorCorrectionBuilder<ERROR_BUILDER, FINAL_BUILDER, R, B> {

    default ERROR_BUILDER adjustIf(Predicate<R> resultPredicate, Supplier<R> adjustmentSupplier) {
        return adjustIf(resultPredicate, (ignored) -> adjustmentSupplier.get());
    }

    ERROR_BUILDER adjustIf(Predicate<R> resultPredicate, Function<A, R> function);

    default FINAL_BUILDER correctError(Supplier<R> errorCorrectionSupplier) {
        return correctError((Function<A, R>) (ignore) -> errorCorrectionSupplier.get());
    }

    FINAL_BUILDER correctError(Function<A, R> function);

}
