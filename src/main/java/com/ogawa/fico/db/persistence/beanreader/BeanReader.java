package com.ogawa.fico.db.persistence.beanreader;

public interface BeanReader<B> extends AutoCloseable {

    /**
     * Read the next bean from the source.
     *
     * @return The bean or null if there are no more beans to read.
     * @throws Exception
     */
    B read();

}