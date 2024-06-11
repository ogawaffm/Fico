package com.ogawa.fico.performance.logging.building;

import com.ogawa.fico.performance.logging.messageset.BaseMessageSetBuilderInterface;

public interface ReceiverCurrentStageBuilder<CURR_BUILDER>
    extends BaseMessageSetBuilderInterface<CURR_BUILDER> {

    CURR_BUILDER arguments();

    CURR_BUILDER arguments(String... names);

}
