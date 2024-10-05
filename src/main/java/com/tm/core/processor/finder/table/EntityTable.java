package com.tm.core.processor.finder.table;

import com.tm.core.processor.finder.parameter.Parameter;

public class EntityTable implements IEntityTable {
    private static final String SELECT_TEMPLATE = "SELECT * FROM %s";

    private final Class<?> clazz;
    private final String tableName;
    private final String selectAllQuery;
//    private List<Parameter> paramList;

    public EntityTable(Class<?> clazz, String tableName) {
        this.clazz = clazz;
        this.tableName = tableName;
        this.selectAllQuery = String.format(SELECT_TEMPLATE, tableName);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getSelectAllQuery() {
        return selectAllQuery;
    }

    public String getTableName() {
        return tableName;
    }

    //    public List<Parameter> getParamList() {
//        return paramList;
//    }

//    public String createFindQuery() {
//        return createFindQuery(this.paramList);
//    }

//    @Override
//    public void addParams(Parameter... params) {
//        this.paramList = List.of(params);
//    }

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
}
