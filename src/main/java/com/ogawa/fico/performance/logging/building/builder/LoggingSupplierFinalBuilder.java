package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.LogEvent;
import com.ogawa.fico.performance.logging.building.ReturnerCurrentStageBuilder;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.event.Level;

public class LoggingSupplierFinalBuilder<SUPPLIER_RESULT>
    implements
    ReturnerCurrentStageBuilder<LoggingSupplierFinalBuilder<SUPPLIER_RESULT>, SUPPLIER_RESULT, Supplier<SUPPLIER_RESULT>> {

    @Override
    public Supplier<SUPPLIER_RESULT> build() {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> check(Predicate<SUPPLIER_RESULT> adjustmentQualityCheck) {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> result() {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> result(String resultName) {
        return null;
    }

    @Override
    public LoggingSupplierFinalBuilder<SUPPLIER_RESULT> log(LogEvent logEvent, Level logLevel, String messageTemplate) {
        return null;
    }
}
