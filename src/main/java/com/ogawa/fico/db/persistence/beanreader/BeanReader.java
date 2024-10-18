package com.ogawa.fico.db.persistence.beanreader;

import com.ogawa.fico.db.persistence.factory.BeanReaderIterator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface BeanReader<B> extends AutoCloseable, Iterable<B> {

    /**
     * Read the next bean from the source.
     *
     * @return The bean or null if there are no more beans to read.
     * @throws Exception
     */
    B read();

    boolean isClosed();

    @Override
    default Iterator<B> iterator() {
        return new BeanReaderIterator<>(this, true, 0L);
    }

    default Stream<B> stream() {
        Stream<B> stream =
            StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
        return stream.onClose(new Runnable() {
            @Override
            public void run() {
                try {
                    BeanReader.this.close();
                } catch (Exception ignore) {
                }
            }
        });
    }

}