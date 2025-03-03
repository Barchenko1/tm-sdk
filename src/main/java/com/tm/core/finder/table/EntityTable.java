package com.tm.core.finder.table;

import com.tm.core.finder.parameter.Parameter;

public class EntityTable implements IEntityTable {
    private static final String JQL_SELECT_TEMPLATE = "SELECT e FROM %s e";

    private final Class<?> clazz;
    private final String className;
    private final String selectAllJqlQuery;
    private String defaultNamedQuery;

    public EntityTable(Class<?> clazz, String className) {
        this.clazz = clazz;
        this.className = className;
        this.selectAllJqlQuery = String.format(JQL_SELECT_TEMPLATE, this.clazz.getSimpleName());
    }

    public EntityTable(Class<?> clazz, String className, String defaultNamedQuery) {
        this.clazz = clazz;
        this.className = className;
        this.defaultNamedQuery = defaultNamedQuery;
        this.selectAllJqlQuery = String.format(JQL_SELECT_TEMPLATE, this.clazz.getSimpleName());
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getSelectAllJqlQuery() {
        return selectAllJqlQuery;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String createFindJqlQuery(Parameter... params) {
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder(String.format(selectAllJqlQuery, className));
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

    @Override
    public String getDefaultNamedQuery() {
        return defaultNamedQuery;
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


}
