package com.ogawa.fico.performance.logging.building;

import com.ogawa.fico.performance.logging.messageset.ReturnerMessageSetBuilderInterface;
import java.util.function.Predicate;

public interface ReturnerCurrentStageBuilder<RETURNER_BUILDER, RETURNER_RESULT, BUILD_RESULT> extends
    ReturnerMessageSetBuilderInterface<RETURNER_BUILDER> {

    default RETURNER_BUILDER checkIsNull() {
        return check(result -> result == null);
    }

    default RETURNER_BUILDER checkIsNotNull() {
        return check(result -> result != null);
    }

    RETURNER_BUILDER check(Predicate<RETURNER_RESULT> adjustmentQualityCheck);

    RETURNER_BUILDER result();

    RETURNER_BUILDER result(String resultName);

}
