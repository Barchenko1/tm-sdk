package com.tm.core.dao.single;

import java.util.List;
import java.util.Optional;

public interface ISingleEntityDao {

    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);
    void mutateEntityBySQLQueryWithParams(String sqlQuery, Object... params);

    <E> E getEntityBySQLQueryWithParams(String sqlQuery, Object... params);
    <E> Optional<E> getOptionalEntityBySQLQueryWithParams(String sqlQuery, Object... params);

    <E> List<E> getEntityListBySQLQuery(String sqlQuery);
    <E> List<E> getEntityListBySQLQueryWithParams(String sqlQuery, Object... params);

}
