package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.LogEvent;
import com.ogawa.fico.performance.logging.building.ReturnerErrorCorrectionBuilder;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.event.Level;

public class LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT>
    implements
    ReturnerErrorCorrectionBuilder<LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT>, LoggingSupplierFinalBuilder<SUPPLIER_RESULT>,
        SUPPLIER_RESULT, Supplier<SUPPLIER_RESULT>> {

    @Override
    public Supplier<SUPPLIER_RESULT> build() {
        return null;
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
    public LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT> check(Predicate<SUPPLIER_RESULT> adjustmentQualityCheck) {
        return null;
    }

    @Override
    public LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT> result() {
        return null;
    }

    @Override
    public LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT> result(String resultName) {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> correctError(
        Supplier<SUPPLIER_RESULT> errorCorrectionSupplier) {
        return null;
    }

    @Override
    public LoggingSupplierErrCorrBuilder<SUPPLIER_RESULT> log(LogEvent logEvent, Level logLevel,
        String messageTemplate) {
        return null;
    }
}
