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

public class EntityIdentifierDao implements IEntityIdentifierDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityIdentifierDao.class);

    private final IEntityMappingManager entityMappingManager;

    public EntityIdentifierDao(IEntityMappingManager entityMappingManager) {
        this.entityMappingManager = entityMappingManager;
    }

    @Override
    public <E> List<E> getEntityList(Session session, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        return query.list();
    }

    @Override
    public <E> List<E> getEntityListGraph(Session session, String graphName, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, graphName, clazz, parameters);
        return query.list();
    }

    @Override
    public <E> List<E> getEntityListNamedQuery(Session session, String namedQuery, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, namedQuery, clazz, parameters);
        return query.list();
    }

    @Override
    public <E> E getEntity(Session session, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> E getEntityGraph(Session session, String graphName, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, graphName, clazz, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> E getEntityNamedQuery(Session session, String namedQuery, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, namedQuery, clazz, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Session session, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        return query.uniqueResultOptional();
    }

    @Override
    public <E> Optional<E> getOptionalEntityGraph(Session session, String graphName, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, graphName, clazz, parameters);
        return query.uniqueResultOptional();
    }

    @Override
    public <E> Optional<E> getOptionalEntityNamedQuery(Session session, String namedQuery, Class<?> clazz, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, namedQuery, clazz, parameters);
        return query.uniqueResultOptional();
    }

    @SuppressWarnings("unchecked")
    private <E> Query<E> getEntityQuery(Session session, Class<?> clazz, Parameter... params) {
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new IllegalArgumentException("Invalid select class: " + clazz);
        }

        String queryStr = (params == null || params.length == 0)
                ? entityTable.getSelectAllQuery()
                : entityTable.createFindQuery(params);

        Query<E> query = (Query<E>) session.createNativeQuery(queryStr, entityTable.getClazz());

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i].getValue());
            }
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    private <E> Query<E> getDynamicEntityGraphQuery(Session session, String graphName, Class<?> clazz, Parameter... params) {
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        String jpql = (params == null || params.length == 0)
                ? entityTable.getSelectAllJqlQuery()
                : entityTable.createFindJqlQuery(params);

        Query<E> query = (Query<E>) session.createQuery(jpql, entityTable.getClazz());

        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }

        query.setHint("jakarta.persistence.loadgraph", session.getEntityGraph(graphName));
        return query;
    }

    @SuppressWarnings("unchecked")
    private <E> Query<E> getNamedQuery(Session session, String namedQuery, Class<?> clazz, Parameter... params) {
        Query<E> query = (Query<E>) session.createNamedQuery(namedQuery, clazz);
        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }
        return query;
    }

}
