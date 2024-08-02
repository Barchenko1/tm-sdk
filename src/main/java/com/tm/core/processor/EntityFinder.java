package com.tm.core.processor;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.util.Arrays;
import java.util.List;

public class EntityFinder {
    private final String selectQuery;
    private final List<Object> paramList;

    public EntityFinder(String selectQuery, Object... params) {
        this.selectQuery = selectQuery;
        this.paramList = Arrays.asList(params);
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public List<Object> getParamList() {
        return paramList;
    }

    @SuppressWarnings("unchecked")
    public <E> Query<E> executeSelectQuery(Session session, Class<?> clazz) {
        if (!validateSelectValues() && clazz != null) {
            throw new RuntimeException("Invalid select values");
        }
        NativeQuery<E> query = (NativeQuery<E>) session.createNativeQuery(selectQuery, clazz);
        for (int i = 0; i < paramList.size(); i++) {
            query.setParameter(i + 1, paramList.get(i));
        }
        return query;
    }

    private boolean validateSelectValues() {
        return selectQuery != null
                && !selectQuery.isEmpty()
                && paramList != null
                && !paramList.isEmpty();
    }
}
