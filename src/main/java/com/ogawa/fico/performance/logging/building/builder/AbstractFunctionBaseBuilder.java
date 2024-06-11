package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.function.Function3;
import com.ogawa.fico.performance.logging.building.ReceiverCurrentStageBuilder;
import com.ogawa.fico.performance.logging.executeable.LoggingOneCallFunction;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.Lombok;
import lombok.NonNull;

public abstract class AbstractFunctionBaseBuilder
    <CURR_BUILDER extends AbstractFunctionBaseBuilder<CURR_BUILDER, FUNC_ARG, FUNC_RESULT, BUILD_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>,
        FUNC_ARG, FUNC_RESULT, BUILD_RESULT,
        CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, FUNC_ARG, FUNC_RESULT>,
        FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>>
    extends LoggingReturnerBaseBuilder<CURR_BUILDER>
    implements ReceiverCurrentStageBuilder<CURR_BUILDER, Function<FUNC_ARG, FUNC_RESULT>> {

    static final BiPredicate RESULT_IS_NULL = (originalArgument, originalResult) -> originalResult == null;

    Function<FUNC_ARG, FUNC_RESULT> invocationFunction;

    BiPredicate<FUNC_ARG, FUNC_RESULT> invocationResultQualityCheck;

    Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3;
    BiPredicate<FUNC_ARG, FUNC_RESULT> adjustmentQualityCheck;

    BiPredicate<FUNC_ARG, FUNC_RESULT> errorCorrectionQualityCheck;

    Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, Exception, FUNC_RESULT> errorCorrectionFunction3;

    AbstractFunctionBaseBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
        this.invocationFunction = null;
        this.invocationResultQualityCheck = null;
        this.adjustmentFunction3 = null;
        this.adjustmentQualityCheck = null;
        this.errorCorrectionFunction3 = null;
    }

    AbstractFunctionBaseBuilder(AbstractFunctionBaseBuilder abstractFunctionBaseBuilder) {
        super(abstractFunctionBaseBuilder);
        this.invocationFunction = abstractFunctionBaseBuilder.invocationFunction;
        this.invocationResultQualityCheck = abstractFunctionBaseBuilder.invocationResultQualityCheck;
        this.adjustmentFunction3 = abstractFunctionBaseBuilder.adjustmentFunction3;
        this.adjustmentQualityCheck = abstractFunctionBaseBuilder.adjustmentQualityCheck;
        this.errorCorrectionFunction3 = abstractFunctionBaseBuilder.errorCorrectionFunction3;
    }

    abstract CURR_BUILDER createFunctionBuilder(CURR_BUILDER functionBuilder);

    abstract CORR_AND_ERR_BUILDER createFunctionErrorCorrectionBuilder(CURR_BUILDER abstractFunctionBuilder);

    abstract FINAL_BUILDER createFunctionFinalBuilder(CURR_BUILDER abstractFunctionBuilder);

    public CURR_BUILDER arguments() {
        return super.arguments();
    }

    public CURR_BUILDER arguments(String... names) {
        return super.arguments(names);
    }

    public CURR_BUILDER check(BiPredicate<FUNC_ARG, FUNC_RESULT> invocationResultQualityCheck) {
        this.invocationResultQualityCheck = invocationResultQualityCheck;
        return (CURR_BUILDER) this;
    }

    public CURR_BUILDER check(Predicate<FUNC_RESULT> resultPredicate) {
        this.invocationResultQualityCheck = (originalArgument, originalResult) -> resultPredicate.test(originalResult);
        return (CURR_BUILDER) this;
    }

    CORR_AND_ERR_BUILDER adjustIfNull(Function<FUNC_ARG, FUNC_RESULT> adjustmentFunction) {
        this.invocationResultQualityCheck = RESULT_IS_NULL;
        this.adjustmentFunction3 = (function, originalArgument, originalResult) -> adjustmentFunction.apply(
            originalArgument);
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustIfNull(BiFunction<FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentBiFunction) {
        this.invocationResultQualityCheck = RESULT_IS_NULL;
        this.adjustmentFunction3 = (function, originalArgument, originalResult) -> adjustmentBiFunction.apply(
            originalArgument, originalResult);
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustIfNull(
        Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3) {
        this.invocationResultQualityCheck = RESULT_IS_NULL;
        this.adjustmentFunction3 = adjustmentFunction3;
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustIf(BiPredicate<FUNC_ARG, FUNC_RESULT> invocationResultQualityCheck,
        FUNC_RESULT defaultValue) {
        this.invocationResultQualityCheck = (originalArgument, originalResult)
            -> invocationResultQualityCheck.test(originalArgument, originalResult);
        this.adjustmentFunction3 = (function, originalArgument, originalResult) -> defaultValue;
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustIf(BiPredicate<FUNC_ARG, FUNC_RESULT> invocationResultQualityCheck,
        Supplier<FUNC_RESULT> adjustmentSupplier) {
        this.invocationResultQualityCheck = (originalArgument, originalResult)
            -> invocationResultQualityCheck.test(originalArgument, originalResult);
        this.adjustmentFunction3 = (function, originalArgument, originalResult) -> adjustmentSupplier.get();
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustIf(Predicate<FUNC_RESULT> resultPredicate,
        Function<FUNC_ARG, FUNC_RESULT> adjustmentFunction) {
        this.invocationResultQualityCheck = (originalArgument, originalResult) -> resultPredicate.test(originalResult);
        this.adjustmentFunction3 = (function, originalArgument, originalResult) -> adjustmentFunction.apply(
            originalArgument);
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustIf(BiPredicate<FUNC_ARG, FUNC_RESULT> invocationResultQualityCheck,
        Function<FUNC_ARG, FUNC_RESULT> adjustmentFunction) {
        this.invocationResultQualityCheck = (originalArgument, originalResult)
            -> invocationResultQualityCheck.test(originalArgument, originalResult);
        this.adjustmentFunction3 = (function, originalArgument, originalResult) -> adjustmentFunction.apply(
            originalArgument);
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustNull(Predicate<FUNC_RESULT> resultPredicate,
        Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3) {
        this.invocationResultQualityCheck = (originalArgument, originalResult) -> resultPredicate.test(originalResult);
        this.adjustmentFunction3 = adjustmentFunction3;
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    CORR_AND_ERR_BUILDER adjustNull(BiPredicate<FUNC_ARG, FUNC_RESULT> invocationResultQualityCheck,
        Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, FUNC_RESULT, FUNC_RESULT> adjustmentFunction3) {
        this.invocationResultQualityCheck = (originalArgument, originalResult)
            -> invocationResultQualityCheck.test(originalArgument, originalResult);
        this.adjustmentFunction3 = adjustmentFunction3;
        return createFunctionErrorCorrectionBuilder((CURR_BUILDER) this);
    }

    FINAL_BUILDER correctError(Runnable errorCorrectionRunnable) {
        this.errorCorrectionFunction3 = (function, originalArgument, exception) -> {
            errorCorrectionRunnable.run();
            Lombok.sneakyThrow(exception);
            return null;
        };
        return createFunctionFinalBuilder((CURR_BUILDER) this);
    }

    FINAL_BUILDER correctError(Consumer<Exception> errorCorrectionConsumer) {
        this.errorCorrectionFunction3 = (function, originalArgument, exception) -> {
            errorCorrectionConsumer.accept(exception);
            Lombok.sneakyThrow(exception);
            return null;
        };
        return createFunctionFinalBuilder((CURR_BUILDER) this);
    }

    FINAL_BUILDER correctError(@NonNull Supplier<FUNC_RESULT> errorCorrectionSupplier) {
        this.errorCorrectionFunction3 = (function, originalArgument, exception) -> errorCorrectionSupplier.get();
        return createFunctionFinalBuilder((CURR_BUILDER) this);
    }

    FINAL_BUILDER correctError(@NonNull Function<FUNC_ARG, FUNC_RESULT> errorCorrectionFunction) {
        this.errorCorrectionFunction3 = (function, originalArgument, exception) -> errorCorrectionFunction.apply(
            originalArgument);
        return createFunctionFinalBuilder((CURR_BUILDER) this);
    }

    FINAL_BUILDER correctError(
        @NonNull Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, Exception, FUNC_RESULT> errorCorrectionFunction3) {
        this.errorCorrectionFunction3 = errorCorrectionFunction3;
        return createFunctionFinalBuilder((CURR_BUILDER) this);
    }

    public Function<FUNC_ARG, FUNC_RESULT> build() {
        return new LoggingOneCallFunction<>(createActionLogger(), invocationFunction,
            invocationResultQualityCheck, adjustmentFunction3, errorCorrectionFunction3,
            resultName, arguments
        );
    }

    public LoggingBatchableBuilder batch(Iterator<FUNC_ARG> iterator) {
        return new LoggingBatchableBuilder<LoggingBatchableBuilder>((AbstractLoggingActionBuilder) this);
    }

}
