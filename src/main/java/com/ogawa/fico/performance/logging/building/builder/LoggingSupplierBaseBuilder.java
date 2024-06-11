package com.ogawa.fico.performance.logging.building.builder;

public abstract class LoggingSupplierBaseBuilder<SUPPLIER_BUILDER
    extends LoggingSupplierBaseBuilder<SUPPLIER_BUILDER, SUPPLIER_ARG, SUPPLIER_RESULT>, SUPPLIER_ARG, SUPPLIER_RESULT>
    extends LoggingReturnerBaseBuilder<SUPPLIER_BUILDER> {

    LoggingSupplierBaseBuilder(AbstractLoggingActionBuilder abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    LoggingSupplierBaseBuilder(LoggingSupplierBaseBuilder loggingSupplierBaseBuilder) {
        super(loggingSupplierBaseBuilder);
    }

}
