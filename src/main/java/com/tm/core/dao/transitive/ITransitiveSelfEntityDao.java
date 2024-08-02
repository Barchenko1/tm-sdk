package com.tm.core.dao.transitive;

import com.tm.core.processor.EntityFinder;
import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.util.TransitiveSelfEnum;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ITransitiveSelfEntityDao {

    <E extends TransitiveSelfEntity> void saveEntityTree(E entity);
    <E extends TransitiveSelfEntity> void updateEntityTree(EntityFinder entityFinder, E newEntity);
    <E extends TransitiveSelfEntity> void deleteEntityTree(EntityFinder entityFinder);

    <E extends TransitiveSelfEntity> E getEntityBySQLQuery(String sqlQuery);
    <E extends TransitiveSelfEntity> Optional<E> getOptionalEntityBySQLQuery(String sqlQuery);

    <E extends TransitiveSelfEntity> List<E> getEntityListBySQLQuery(String sqlQuery);
    <E extends TransitiveSelfEntity> List<E> getEntityListBySQLQueryWithParams(String sqlQuery, Object... params);

    <E extends TransitiveSelfEntity> E getEntityBySQLQueryWithParams(String sqlQuery, Object... params);
    <E extends TransitiveSelfEntity> Optional<E> getOptionalEntityBySQLQueryWithParams(String sqlQuery, Object... params);

    <E extends TransitiveSelfEntity> Map<TransitiveSelfEnum, List<E>> getEntitiesTreeBySQLQuery(String sqlQuery);
}
