package com.ogawa.fico.performance.logging.building.builder;

import com.ogawa.fico.performance.logging.building.BatchableReceiverBuilder;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.ToString;

@ToString(callSuper = true)
public class LoggingBatchableBuilder<
    CURR_BUILDER extends LoggingBatchableBuilder<CURR_BUILDER, BUILD_RESULT>,
    FUNC_BUILDER extends AbstractFunctionBuilder<FUNC_BUILDER, ?>,
    FUNC_CORR_AND_ERR_BUILDER extends AbstractFunctionErrCorrBuilder<FUNC_CORR_AND_ERR_BUILDER, ?, ?>,
    FINAL_FUNC_BUILDER extends AbstractFunctionErrCorrBuilder<CORR_AND_ERR_BUILDER, >,
    BUILD_RESULT
    >
    extends LoggingActionBaseBuilder<CURR_BUILDER>
    implements BatchableReceiverBuilder<CURR_BUILDER, BUILD_RESULT> {

    public LoggingBatchableBuilder() {
        super();
    }

    LoggingBatchableBuilder(AbstractLoggingActionBuilder<CURR_BUILDER> abstractLoggingActionBuilder) {
        super(abstractLoggingActionBuilder);
    }

    public CURR_BUILDER clone() {
        return (CURR_BUILDER) new LoggingBatchableBuilder(this);
    }

    @Override
    public <ARG, RESULT> FunctionBuilder<ARG, RESULT> T, R> invoke(Function<ARG, RESULT> function) {
        return super.invoke(function);
    }

    public <T> LoggingConsumerBuilder<T> invoke(Consumer<T> consumer) {
        return super.invoke(consumer);
    }

}
