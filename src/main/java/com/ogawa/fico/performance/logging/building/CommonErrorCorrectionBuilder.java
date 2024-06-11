package com.ogawa.fico.performance.logging.building;

import com.ogawa.fico.performance.logging.messageset.BaseMessageSetBuilderInterface;
import java.util.function.Consumer;

public interface CommonErrorCorrectionBuilder<CORR_AND_ERR_BUILDER, FINAL_BUILDER, BUILD_RESULT>
    extends BaseMessageSetBuilderInterface<CORR_AND_ERR_BUILDER> {

    FINAL_BUILDER correctError(Runnable errorCorrectionRunnable);

    FINAL_BUILDER correctError(Consumer<Exception> errorCorrectionConsumer);

}