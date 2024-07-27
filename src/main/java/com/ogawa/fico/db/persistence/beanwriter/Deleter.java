package com.ogawa.fico.db.persistence.beanwriter;

/**
 * Delete a bean.
 *
 * @param <B> The type of bean to delete.
 */
public interface Deleter<B> extends AutoCloseable {

    /**
     * Delete a bean.
     *
     * @param bean The bean to delete.
     * @return The deleted bean.
     */
    B delete(B bean);

}
