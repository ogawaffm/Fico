package com.ogawa.fico.performance.logging.building;

import java.util.function.Function;

public interface ReceiverBatchBuilder<BATCH_BUILDER, ARG> {

    BATCH_BUILDER group(Function<ARG, ?> groupingFunction);

}
