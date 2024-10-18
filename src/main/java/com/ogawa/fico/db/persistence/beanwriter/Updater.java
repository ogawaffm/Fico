package com.ogawa.fico.db.persistence.beanwriter;

/**
 * Update a bean.
 *
 * @param <B> The type of bean to update.
 */
public interface Updater<B> extends AutoCloseable {

    /**
     * Update a bean.
     *
     * @param bean The bean to update.
     */
    void update(B bean);

}
