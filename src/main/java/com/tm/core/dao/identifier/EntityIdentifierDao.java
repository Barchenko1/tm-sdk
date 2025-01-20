package com.tm.core.dao.identifier;

import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.table.EntityTable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityIdentifierDao implements IEntityIdentifierDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityIdentifierDao.class);

    private final IEntityMappingManager entityMappingManager;

    public EntityIdentifierDao(IEntityMappingManager entityMappingManager) {
        this.entityMappingManager = entityMappingManager;
    }

    @Override
    public <E> List<E> getEntityList(Session session, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        List<E> list = query.list();

        return list;
    }

    @Override
    public <E, R> List<R> getEntityListFunction(Session session, Class<?> clazz, Function<E, R> function, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        List<E> list = query.list();
        return list.stream().map(function)
                .collect(Collectors.toList());
    }

    @Override
    public <E> E getEntity(Session session, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        E e = query.getSingleResult();

        return e;
    }

    @Override
    public <E, R> R getEntityFunction(Session session, Class<?> clazz, Function<E, R> function, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        E e = query.getSingleResult();
        return Optional.ofNullable(e).map(function)
                .orElseThrow(() -> new RuntimeException());
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Session session, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session,clazz, parameters);
        Optional<E> optional = query.uniqueResultOptional();

        return optional;
    }

    @Override
    public <E, R> Optional<R> getOptionalEntityFunction(Session session, Class<?> clazz, Function<E, R> function, Parameter... parameters) {
        Query<E> query = getEntityQuery(session,clazz, parameters);
        Optional<E> optional = query.uniqueResultOptional();
        return Optional.ofNullable(optional.map(function)
                .orElseThrow(() -> new RuntimeException()));
    }

    @SuppressWarnings("unchecked")
    public <E> Query<E> getEntityQuery(Session session, Class<?> clazz, Parameter... params) {
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        if ((params == null) || (params.length == 0)) {
            return (Query<E>) session.createNativeQuery(entityTable.getSelectAllQuery(), entityTable.getClazz());
        } else {
            Query<E> query = (Query<E>) session.createNativeQuery(entityTable.createFindQuery(params),
                    entityTable.getClazz());
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i].getValue());
            }
            return query;
        }
    }
}
