package com.ogawa.fico.performance.logging.building;

import java.util.function.Predicate;

public interface ReturnerBatchBuilder<BATCH_BUILDER, RETURNER_RESULT> {

    BATCH_BUILDER abort(Predicate<RETURNER_RESULT> abortCondition);

}
