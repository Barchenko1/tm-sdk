package com.tm.core.process.dao.identifier;

import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.table.EntityTable;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QueryService implements IQueryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryService.class);

    private final IEntityMappingManager entityMappingManager;

    public QueryService(IEntityMappingManager entityMappingManager) {
        this.entityMappingManager = entityMappingManager;
    }

    @Override
    public <E> List<E> getGraphEntityList(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, clazz, namedQuery, parameters);
        return query.list();
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, clazz, namedQuery, parameters);
        return query.list();
    }

    @Override
    public <E> List<E> getNamedQueryEntityMap(Session session, Class<E> clazz, String namedQuery, Map<String, List<?>> parameters) {
        Query<E> query = getNamedQueryMap(session, clazz, namedQuery, parameters);
        return query.list();
    }

    @Override
    public <E> E getGraphEntity(Session session, Class<E> clazz, String graph, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, clazz, graph, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> E getNamedQueryEntity(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, clazz, namedQuery, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Session session, Class<E> clazz, String graph, Parameter... parameters) {
        Query<E> query = getDynamicEntityGraphQuery(session, clazz, graph, parameters);
        return query.uniqueResultOptional();
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Session session, Class<E> clazz, String namedQuery, Parameter... parameters) {
        Query<E> query = getNamedQuery(session, clazz, namedQuery, parameters);
        return query.uniqueResultOptional();
    }

    @Override
    public <E> E getEntityByDefaultNamedQuery(Session session, Class<E> clazz, Parameter... parameters) {
        Query<E> query = getDefaultNamedQuery(session, clazz, parameters);
        return query.getSingleResult();
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

    private <E> Query<E> getNamedQueryMap(Session session, Class<E> clazz, String namedQuery, Map<String, List<?>> paramMap) {
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        Query<E> query = session.createNamedQuery(namedQuery, clazz);
        if (paramMap != null) {
            paramMap.forEach((key, value) -> {
                if (value == null || value.isEmpty()) {
                    throw new IllegalArgumentException("Parameter '" + key + "' is null or empty.");
                }
                if (value.size() == 1) {
                    query.setParameter(key, value.get(0));
                } else {
                    query.setParameterList(key, value);
                }
            });
        }
        return query;
    }

    private <E> Query<E> getDefaultNamedQuery(Session session, Class<E> clazz, Parameter... params) {
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        Query<E> query = session.createNamedQuery(entityTable.getDefaultNamedQuery(), clazz);
        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }
        return query;
    }

}
