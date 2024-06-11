package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.executeable.LoggingOneCallSupplier;
import com.ogawa.fico.performance.logging.building.ReturnerAdjustmentBuilder;
import com.ogawa.fico.performance.logging.building.ReturnerErrorCorrectionBuilder;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LoggingSupplierBuilder<SUPPLIER_RESULT>
    extends LoggingSupplierBaseBuilder<LoggingSupplierBuilder<SUPPLIER_RESULT>, SUPPLIER_RESULT, SUPPLIER_RESULT>
    implements
    ReturnerAdjustmentBuilder<
        LoggingSupplierBuilder<SUPPLIER_RESULT>,
        LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT>,
        LoggingSupplierFinalBuilder<SUPPLIER_RESULT>,
        SUPPLIER_RESULT,
        Supplier<SUPPLIER_RESULT>
        >,
    ReturnerErrorCorrectionBuilder<
        LoggingSupplierBuilder<SUPPLIER_RESULT>,
        LoggingSupplierFinalBuilder<SUPPLIER_RESULT>,
        SUPPLIER_RESULT, Supplier<SUPPLIER_RESULT>
        > {

    private final Supplier invokationSupplier;

    private Predicate<SUPPLIER_RESULT> invocationResultQualityCheck;

    private Predicate<SUPPLIER_RESULT> adjustmentQualityCheck;

    private Predicate<SUPPLIER_RESULT> errorCorrectionQualityCheck;

    private Supplier<SUPPLIER_RESULT> errorCorrectionSupplier;

    LoggingSupplierBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder,
        Supplier<SUPPLIER_RESULT> invokationSupplier,
        Predicate<SUPPLIER_RESULT> invocationResultQualityCheck, Supplier<SUPPLIER_RESULT> errorCorrectionSupplier) {
        super(abstractLoggingActionBuilder);
        this.invokationSupplier = invokationSupplier;
        this.invocationResultQualityCheck = invocationResultQualityCheck;
        this.errorCorrectionSupplier = errorCorrectionSupplier;
    }

    LoggingSupplierBuilder(LoggingSupplierBuilder<SUPPLIER_RESULT> loggingSupplierBuilder) {
        super(loggingSupplierBuilder);
        this.invokationSupplier = loggingSupplierBuilder.invokationSupplier;
        this.invocationResultQualityCheck = loggingSupplierBuilder.invocationResultQualityCheck;
        this.errorCorrectionSupplier = loggingSupplierBuilder.errorCorrectionSupplier;
    }

    @Override
    public LoggingSupplierBuilder<SUPPLIER_RESULT> clone() {
        return new LoggingSupplierBuilder<>(this);
    }

    public LoggingOneCallSupplier<SUPPLIER_RESULT> build() {
        return new LoggingOneCallSupplier<SUPPLIER_RESULT>(createActionLogger(), invokationSupplier);
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> correctError(Runnable errorCorrectionRunnable) {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> correctError(Consumer<Exception> errorCorrectionConsumer) {
        return null;
    }

    @Override
    public LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT> adjustIf(Predicate<SUPPLIER_RESULT> resultPredicate,
        Supplier<SUPPLIER_RESULT> adjustmentSupplier) {
        return null;
    }

    @Override
    public LoggingSupplierBuilder<SUPPLIER_RESULT> check(Predicate<SUPPLIER_RESULT> adjustmentQualityCheck) {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> correctError(
        Supplier<SUPPLIER_RESULT> errorCorrectionSupplier) {
        return null;
    }

}