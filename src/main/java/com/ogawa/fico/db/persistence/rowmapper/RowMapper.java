package com.ogawa.fico.db.persistence.rowmapper;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

public interface RowMapper<T> {

    List<String> getColumnNames();

    default List<String> getPrimaryKeyColumnNames() {
        return Collections.EMPTY_LIST;
    }


    /**
     * Convert a row to an object. The column order is the same as the order in getColumnNames, but without the primary
     * key columns, which are appended at the end of the row in the order of getPrimaryKeyColumnNames.
     *
     * @param row
     * @return
     */
    T toObject(Object[] row);

    /**
     * Convert an object to a row. The column order is the same as the order in getColumnNames, but without the primary
     * key columns, which are appended at the end of the row in the order of getPrimaryKeyColumnNames.
     *
     * @param object
     * @return
     */
    Object[] toRow(T object);

    default Object[] toInsertRow(T object) {
        if (hasGeneratedKeys()) {
            Object[] rowWithOutKeys = new Object[getColumnNames().size() - getPrimaryKeyColumnNames().size()];
            System.arraycopy(toRow(object), 0, rowWithOutKeys, 0, rowWithOutKeys.length);
            return rowWithOutKeys;
        } else {
            return toRow(object);
        }
    }

    Object[] getPrimaryKeyValues(T object);

    default boolean isPrimaryKeySet(T object) {
        return getPrimaryKeyValues(object) != null;
    }

    void setPrimaryKeyValues(T object, Object[] primaryKeyValues);

    boolean hasGeneratedKeys();

    default String primaryKeyToString(T bean) {
        Object[] primaryKey = getPrimaryKeyValues(bean);
        return Arrays.stream(primaryKey).map(Object::toString).collect(Collectors.joining(":"));
    }

}