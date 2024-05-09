package com.tm.core.dao;

import java.util.List;
import java.util.Optional;

public interface IEntityDao {

    void setClazz(Class<?> clazz);
    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void updateEntityWithSQL(String sqlQuery, Object param);
    <E> void deleteEntity(E entity);
    <E> void deleteEntityWithSQL(String sqlQuery, Object param);
    <E> List<E> getEntityListBySQLQuery(String sqlQuery);
    <E> E getEntityBySQLQuery(String sqlQuery);
    <E> Optional<E> getOptionEntityBySQLQuery(String sqlQuery);
    <E> List<E> getEntityListBySQLQueryWithParams(String sqlQuery, List<Object> params);
    <E> Optional<E> getOptionalEntityBySQLQueryWithParams(String sqlQuery, List<Object> params);
    <E> List<E> getEntityBySQLQueryWithStringParam(String sqlQuery, String param);
    <E> Optional<E> getOptionalEntityBySQLQueryWithStringParam(String sqlQuery, String param);
    <E> List<E> getEntityBySQLQueryWithNumberParam(String sqlQuery, Number param);
    <E> Optional<E> getOptionalEntityBySQLQueryWithNumberParam(String sqlQuery, Number param);

}
