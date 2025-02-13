package com.tm.core.process.dao.identifier;

import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.table.EntityTable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class QueryService implements IQueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);

    private final IEntityMappingManager entityMappingManager;

    public QueryService(IEntityMappingManager entityMappingManager) {
        this.entityMappingManager = entityMappingManager;
    }

    @Override
    public <E> List<E> getEntityList(Session session, Class<E> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        return query.list();
    }

    @Override
    public <E> List<E> getEntityListGraph(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, clazz, namedQuery, parameters);
        return query.list();
    }

    @Override
    public <E> List<E> getEntityListNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, clazz, namedQuery, parameters);
        return query.list();
    }

    @Override
    public <E> E getEntity(Session session, Class<E> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> E getEntityGraph(Session session, Class<E> clazz, String graph, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, clazz, graph, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> E getEntityNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, clazz, namedQuery, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Session session, Class<E> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(session, clazz, parameters);
        return query.uniqueResultOptional();
    }

    @Override
    public <E> Optional<E> getOptionalEntityGraph(Session session, Class<E> clazz, String graph, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, clazz, graph, parameters);
        return query.uniqueResultOptional();
    }

    @Override
    public <E> Optional<E> getOptionalEntityNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, clazz, namedQuery, parameters);
        return query.uniqueResultOptional();
    }

    @SuppressWarnings("unchecked")
    private <E> Query<E> getEntityQuery(Session session, Class<E> clazz, Parameter... params) {
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
    private <E> Query<E> getDynamicEntityGraphQuery(Session session, Class<E> clazz, String graphName, Parameter... params) {
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
    private <E> Query<E> getNamedQuery(Session session, Class<E> clazz, String namedQuery, Parameter... params) {
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        Query<E> query = session.createNamedQuery(namedQuery, clazz);
        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }
        return query;
    }

}
