package com.ogawa.fico.db.persistence.beanwriter;

/**
 * Delete a bean.
 *
 * @param <B> The type of bean to delete.
 */
public interface Creator<B> extends AutoCloseable {

    /**
     * Create a new bean.
     *
     * @param bean The bean to create.
     * @return The created bean.
     */
    B create(B bean);

}
