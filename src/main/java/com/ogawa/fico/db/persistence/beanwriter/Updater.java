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
     * @return The updated bean.
     */
    B update(B bean);

}
