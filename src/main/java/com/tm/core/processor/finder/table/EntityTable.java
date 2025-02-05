package com.tm.core.processor.finder.table;

import com.tm.core.processor.finder.parameter.Parameter;

public class EntityTable implements IEntityTable {
    private static final String SELECT_TEMPLATE = "SELECT * FROM %s";
    private static final String JQL_SELECT_TEMPLATE = "SELECT e FROM %s e";

    private final Class<?> clazz;
    private final String tableName;
    private final String selectAllQuery;
    private final String selectAllJqlQuery;

    public EntityTable(Class<?> clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.selectAllQuery = String.format(SELECT_TEMPLATE, tableName);
        this.selectAllJqlQuery = String.format(JQL_SELECT_TEMPLATE, this.clazz.getSimpleName());
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getSelectAllQuery() {
        return selectAllQuery;
    }

    public String getSelectAllJqlQuery() {
        return selectAllJqlQuery;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String createFindQuery(Parameter... params) {
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder(selectAllQuery);
            sb.append(" WHERE ");
            if (params.length > 1) {
                for (Parameter param : params) {
                    sb.append(String.format("%s = ", param.getName()));
                    sb.append("? ");
                    sb.append("OR ");
                }
                sb.delete(sb.length() - 4, sb.length());
            } else {
                for (Parameter param : params) {
                    sb.append(String.format("%s = ", param.getName()));
                    sb.append("? ");
                }
            }
            sb.append(";");
            return sb.toString();
        }
        throw new IllegalArgumentException("params cannot be null or empty");
    }

    @Override
    public String createFindJqlQuery(Parameter... params) {
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder(String.format(selectAllJqlQuery, tableName));
            sb.append(" WHERE ");
            if (params.length > 1) {
                for (Parameter param : params) {
                    sb.append(String.format("%s : ", param.getName()));
                    sb.append("? ");
                    sb.append("OR ");
                }
                sb.delete(sb.length() - 4, sb.length());
            } else {
                for (Parameter param : params) {
                    sb.append(String.format("e.%s = ", param.getName()));
                    sb.append(String.format(":%s ", param.getName()));
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("params cannot be null or empty");
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
