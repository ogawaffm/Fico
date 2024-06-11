package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.function.Function3;
import com.ogawa.fico.performance.logging.building.ReturnerErrorCorrectionBuilder;
import com.ogawa.fico.performance.logging.messageset.MessageSetDefinition;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;

public abstract class AbstractFunctionErrCorrBuilder<
    CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, FUNC_ARG, FUNC_RESULT>,
    FINAL_BUILDER extends AbstractFunctionFinalBuilder<FUNC_ARG, FUNC_RESULT, CORR_AND_ERR_BUILDER, FINAL_BUILDER>,
    FUNC_ARG,
    FUNC_RESULT> extends
    AbstractFunctionBaseBuilder<
        CORR_AND_ERR_BUILDER,
        FUNC_ARG, FUNC_RESULT,
        Function<FUNC_ARG, FUNC_RESULT>,
        CORR_AND_ERR_BUILDER,
        FINAL_BUILDER
        >
    implements ReturnerErrorCorrectionBuilder<
    CORR_AND_ERR_BUILDER,
    FINAL_BUILDER,
    FUNC_RESULT,
    Function<FUNC_ARG, FUNC_RESULT>
    > {

    public AbstractFunctionErrCorrBuilder(AbstractFunctionBaseBuilder abstractFunctionBaseBuilder) {
        super(abstractFunctionBaseBuilder);
    }

    MessageSetDefinition getContextMessageSetDefinition() {
        return this.errorCorrectionMessageSetDefinition;
    }

    public CORR_AND_ERR_BUILDER clone() {
        return createFunctionErrorCorrectionBuilder((CORR_AND_ERR_BUILDER) this);
    }

    @Override
    public CORR_AND_ERR_BUILDER check(BiPredicate<FUNC_ARG, FUNC_RESULT> errorCorrectionQualityCheck) {
        this.errorCorrectionQualityCheck = errorCorrectionQualityCheck;
        return (CORR_AND_ERR_BUILDER) this;
    }

    public FINAL_BUILDER correctError(
        @NonNull Supplier<FUNC_RESULT> errorCorrectionSupplier) {
        return super.correctError(errorCorrectionSupplier);
    }

    public FINAL_BUILDER correctError(
        @NonNull Function<FUNC_ARG, FUNC_RESULT> errorCorrectionFunction) {
        return super.correctError(errorCorrectionFunction);
    }

    public FINAL_BUILDER correctError(
        @NonNull Function3<Function<FUNC_ARG, FUNC_RESULT>, FUNC_ARG, Exception, FUNC_RESULT> errorCorrectionFunction3) {
        return super.correctError(errorCorrectionFunction3);
    }

    @Override
    public FINAL_BUILDER correctError(Runnable errorCorrectionRunnable) {
        return super.correctError(errorCorrectionRunnable);
    }

    @Override
    public FINAL_BUILDER correctError(
        Consumer<Exception> errorCorrectionConsumer) {
        return null;
    }

}
