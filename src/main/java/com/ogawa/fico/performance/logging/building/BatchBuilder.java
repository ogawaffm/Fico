package com.ogawa.fico.performance.logging.building;

import com.ogawa.fico.performance.logging.building.builder.LoggingReceiverBatchBuilder;
import java.util.function.BooleanSupplier;

public interface BatchBuilder<BATCH_BUILDER> {

    BATCH_BUILDER group(long groupSize);

    BATCH_BUILDER abort(BooleanSupplier abortCondition);

}
