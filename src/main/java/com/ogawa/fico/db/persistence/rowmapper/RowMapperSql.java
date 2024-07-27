package com.ogawa.fico.db.persistence.rowmapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;

public class RowMapperSql {

    private final RowMapper rowMapper;

    public RowMapperSql(RowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }

    private void checkNoPrimaryKey(String operation) {
        if (rowMapper.getPrimaryKeyColumnNames().isEmpty()) {
            throw new IllegalArgumentException(
                "Primary key columns are required for"
                    + operation
                    + " but are not defined in the row mapper "
                    + rowMapper.getClass().getName()
            );
        }
    }

    /**
     * Get the select projection for all columns in the row mapper without the SELECT keyword.
     *
     * @return The select projection (column names separated by commas).
     */
    public String getProjection() {
        return getProj("").toString();
    }

    /**
     * Get the select projection for all columns in the row mapper without the SELECT keyword.
     *
     * @param alias The table alias to use for the column names or "" for no alias.
     * @return The select projection (column names separated by commas).
     */
    public String getProjection(@NonNull String alias) {
        return getProj(alias).toString();
    }

    private StringBuilder getProj(String alias) {
        return getColumnExpressions(getNonPkColThenPkColNames(), ", ", alias, true, "");
    }

    /**
     * Get the select SQL for all columns in the row mapper.
     *
     * @return The select SQL with the SELECT keyword and projection (column names separated by commas), but without the
     * FROM keyword and table name etc.
     */
    public String getSelectWithoutSourceSql() {
        return getSelectWithoutSrcSql("").toString();
    }

    public String getSelectWithoutSourceSql(@NonNull String alias) {
        return getSelectWithoutSrcSql(alias).toString();
    }

    private StringBuilder getSelectWithoutSrcSql(String alias) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT\n");
        sb.append(getProj(alias));
        return sb;
    }

    public String getSelectFromTableSql(@NonNull String tableName, boolean withPrimaryKeyFilter) {
        if (withPrimaryKeyFilter) {
            checkNoPrimaryKey("select");
        }
        StringBuilder sb = getSelectWithOufFilterSql(tableName, "");
        if (withPrimaryKeyFilter) {
            sb.append("\nWHERE\n");
            sb.append(getPrimaryKeyBindVarFilter(""));
        }
        return sb.toString();
    }

    public String getSelectFromSubSelectSql(@NonNull String subSelectSql) {
        return getSelectWithOufFilterSql("(" + subSelectSql + ")", "S").toString();
    }

    private StringBuilder getSelectWithOufFilterSql(String tableExpression, String alias) {
        StringBuilder sb = new StringBuilder();
        sb.append(getSelectWithoutSrcSql(alias));
        sb.append("\nFROM ").append(tableExpression).append("\n");
        return sb;
    }

    public String getInsertSql(@NonNull String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");
        sb.append(getColumnExpressions(getNonPkColThenPkColNames(), ", ", "", false, ""));
        sb.append("\n)\nVALUES (");
        sb.append(getBindVarPlaceholders(rowMapper.getColumnNames().size()));
        sb.append(")");
        return sb.toString();
    }

    public String getUpdateSql(@NonNull String tableName) {
        checkNoPrimaryKey("update");
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName).append("\nSET ");
        sb.append(
            getColumnExpressions(getNonPrimaryKeyColumnNames(),
                ", ", "", false, " = ?"
            )
        );
        sb.append("\nWHERE\n");
        sb.append(getPrimaryKeyBindVarFilter(""));
        return sb.toString();
    }

    /**
     * Get column expressions for the given column names, using the given separator, alias and suffix. If
     * assureUnaliasedColumnNames is true, the column names are re-aliased from the alias to the column name.
     *
     * @param columnNames                The column names to use.
     * @param separator                  The separator to use between column expressions.
     * @param alias                      The alias to use for the column names or "" for no alias.
     * @param assureUnaliasedColumnNames If true, the column names are re-aliased from the alias to the column name.
     * @param suffix                     The suffix to append to each column expression.
     * @return
     */
    private StringBuilder getColumnExpressions(List<String> columnNames, String separator, String alias,
        boolean assureUnaliasedColumnNames, String suffix) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columnNames) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (!alias.isEmpty()) {
                sb.append(alias).append(".").append(columnName);
                if (assureUnaliasedColumnNames) {
                    sb.append(" AS ").append(alias).append(columnName);
                }
            } else {
                sb.append(columnName);
            }
            sb.append(suffix);
        }
        return sb;
    }

    private StringBuilder getPrimaryKeyBindVarFilter(String alias) {
        return getColumnExpressions(
            rowMapper.getPrimaryKeyColumnNames(), " AND ", alias, false, " = ?"
        );
    }

    private StringBuilder getBindVarPlaceholders(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        return sb;
    }

    private List<String> getNonPkColThenPkColNames() {
        return (List<String>) Stream.concat(
                getNonPrimaryKeyColumnNames().stream(),
                rowMapper.getPrimaryKeyColumnNames().stream())
            .collect(Collectors.toList());
    }

    private List<String> getNonPrimaryKeyColumnNames() {
        return (List<String>) rowMapper.getColumnNames().stream()
            .filter(columnName -> !rowMapper.getPrimaryKeyColumnNames().contains(columnName)).collect(
                Collectors.toList());
    }

}
