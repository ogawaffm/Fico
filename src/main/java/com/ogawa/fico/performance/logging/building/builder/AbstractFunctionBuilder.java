package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.function.Function3;
import com.ogawa.fico.performance.logging.building.ReturnerAdjustmentBuilder;
import com.ogawa.fico.performance.logging.building.ReturnerErrorCorrectionBuilder;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/*
        CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<FUNC_ARG, FUNC_RESULT>,
        FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT>>

 */
// Facade for building a function by publishing inherited methods
public abstract class AbstractFunctionBuilder<FUNC_ARG, FUNC_RESULT,
    CURR_BUILDER extends AbstractFunctionBuilder<FUNC_ARG, FUNC_RESULT, CURR_BUILDER, CORR_AND_ERR_BUILDER, FINAL_BUILDER>,
    CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, FUNC_ARG, FUNC_RESULT>,
    FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>>
    extends
    AbstractFunctionBaseBuilder<CURR_BUILDER, FUNC_ARG, FUNC_RESULT, Function<FUNC_ARG, FUNC_RESULT>, CORR_AND_ERR_BUILDER, FINAL_BUILDER>
    implements ReturnerAdjustmentBuilder<
    CURR_BUILDER,
    CORR_AND_ERR_BUILDER,
    FINAL_BUILDER,
    FUNC_RESULT, Function<FUNC_ARG, FUNC_RESULT>
    >,
    ReturnerErrorCorrectionBuilder<CURR_BUILDER, FINAL_BUILDER, FUNC_RESULT, Function<FUNC_ARG, FUNC_RESULT>> {

    //    public static BiPredicate RESULT_IS_NULL = (functionArgument, originalResult) -> originalResult == null;
//    public static BiPredicate RESULT_IS_NOT_NULL = (functionArgument, originalResult) -> originalResult != null;
    private static BiPredicate UNCONDITIONAL = (functionArgument, originalResult) -> true;

    AbstractFunctionBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    AbstractFunctionBuilder(AbstractFunctionBaseBuilder abstractFunctionBaseBuilder) {
        super(abstractFunctionBaseBuilder);
    }

    abstract CURR_BUILDER createFunctionBuilder(CURR_BUILDER functionBuilder);

    MessageSetDefinition getContextMessageSetDefinition() {
        return this.invocationMessageSetDefinition;
    }

    public CURR_BUILDER clone() {
        return createFunctionBuilder((CURR_BUILDER) this);
    }

    public CORR_AND_ERR_BUILDER adjustIf(Predicate<FUNC_RESULT> resultPredicate,
        Function<FUNC_ARG, FUNC_RESULT> adjustmentFunction) {
        return super.adjustIf(resultPredicate, adjustmentFunction);
    }

    public CORR_AND_ERR_BUILDER adjustIfNull(Function<FUNC_ARG, FUNC_RESULT> adjustmentFunction) {
        return super.adjustIfNull(adjustmentFunction);
    }

    public CORR_AND_ERR_BUILDER adjustIfNull(
        Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3) {
        return super.adjustIfNull(adjustmentFunction3);
    }

    public CORR_AND_ERR_BUILDER adjustIf(BiPredicate<FUNC_ARG, FUNC_RESULT> argumentAndResultPredicate,
        FUNC_RESULT defaultValue) {
        return super.adjustIf(argumentAndResultPredicate, defaultValue);
    }

    public CORR_AND_ERR_BUILDER adjustIf(BiPredicate<FUNC_ARG, FUNC_RESULT> argumentAndResultPredicate,
        Supplier<FUNC_RESULT> adjustmentSupplier) {
        return super.adjustIf(argumentAndResultPredicate, adjustmentSupplier);
    }

    public CORR_AND_ERR_BUILDER adjustIf(BiPredicate<FUNC_ARG, FUNC_RESULT> argumentAndResultPredicate,
        Function<FUNC_ARG, FUNC_RESULT> adjustmentFunction) {
        return super.adjustIf(argumentAndResultPredicate, adjustmentFunction);
    }

    public CORR_AND_ERR_BUILDER adjustNull(Predicate<FUNC_RESULT> resultPredicate,
        Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3) {
        return super.adjustNull(resultPredicate, adjustmentFunction3);
    }

    public CORR_AND_ERR_BUILDER adjustNull(BiPredicate<FUNC_ARG, FUNC_RESULT> argumentAndResultPredicate,
        Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3) {
        return super.adjustNull(argumentAndResultPredicate, adjustmentFunction3);
    }

    @Override
    public CORR_AND_ERR_BUILDER adjustIf(Predicate<FUNC_RESULT> resultPredicate,
        Supplier<FUNC_RESULT> adjustmentSupplier) {
        return super.adjustIf((ignore, result) -> resultPredicate.test(result), adjustmentSupplier);
    }

    @Override
    public FINAL_BUILDER correctError(Function<FUNC_ARG, FUNC_RESULT> function) {
        return super.correctError(function);
    }

    @Override
    public FINAL_BUILDER correctError(Supplier<FUNC_RESULT> errorCorrectionSupplier) {
        return super.correctError(errorCorrectionSupplier);
    }

    @Override
    public FINAL_BUILDER correctError(Runnable errorCorrectionRunnable) {
        return super.correctError(errorCorrectionRunnable);
    }

    @Override
    public FINAL_BUILDER correctError(Consumer<Exception> errorCorrectionConsumer) {
        return super.correctError(errorCorrectionConsumer);
    }

}
