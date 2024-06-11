package com.ogawa.fico.performance.logging.building;

import com.ogawa.fico.performance.logging.messageset.BaseMessageSetBuilderInterface;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public interface BatchableReceiverBuilder<BATCH_BUILDER, BUILD_RESULT>
    extends BaseMessageSetBuilderInterface<BATCH_BUILDER> {

    <ARG> BATCH_BUILDER batch(Iterator<ARG> iterator);

    default <ARG> BATCH_BUILDER batch(ARG... arguments) {
        return batch(Arrays.stream(arguments).iterator());
    }

    default <ARG> BATCH_BUILDER batch(Iterable<ARG> collection) {
        return batch(collection.iterator());
    }

    /**
     * Since Stream does not implement the {@link Iterable} interface it is possible to use this method to iterate over
     * the elements of the stream.
     */
    default <ARG> BATCH_BUILDER batch(Stream<ARG> stream) {
        return batch(stream.iterator());
    }

}
