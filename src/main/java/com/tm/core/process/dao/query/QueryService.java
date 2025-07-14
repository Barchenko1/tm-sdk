package com.tm.core.process.dao.query;

import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.table.EntityTable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    public <E> List<E> getGraphEntityList(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("getGraphEntityList func: {}, {}, {}", clazz, namedQuery, parameters);
        Query<E> query = getDynamicEntityGraphQuery(entityManager, clazz, namedQuery, parameters);
        return query.getResultList();
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("getNamedQueryEntityList func: {}, {}, {}", clazz, namedQuery, parameters);
        Query<E> query = getNamedQuery(entityManager, clazz, namedQuery, parameters);
        return query.getResultList();
    }

    @Override
    public <E> List<E> getNamedQueryEntityMap(EntityManager entityManager, Class<E> clazz, String namedQuery, Map<String, List<?>> parameters) {
        LOGGER.info("getNamedQueryEntityMap func: {}, {}, {}", clazz, namedQuery, parameters);
        Query<E> query = (Query<E>) getNamedQueryMap(entityManager, clazz, namedQuery, parameters);
        return query.getResultList();
    }

    @Override
    public <E> E getGraphEntity(EntityManager entityManager, Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("getGraphEntity func: {}, {}, {}", clazz, graph, parameters);
        Query<E> query = getDynamicEntityGraphQuery(entityManager, clazz, graph, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> E getNamedQueryEntity(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("getNamedQueryEntity func: {}, {}, {}", clazz, namedQuery, parameters);
        Query<E> query = getNamedQuery(entityManager, clazz, namedQuery, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(EntityManager entityManager, Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("getGraphOptionalEntity func: {}, {}, {}", clazz, graph, parameters);
        Query<E> query = getDynamicEntityGraphQuery(entityManager, clazz, graph, parameters);
        return Optional.ofNullable(query.getSingleResultOrNull());
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("getNamedQueryOptionalEntity func: {}, {}, {}", clazz, namedQuery, parameters);
        Query<E> query = getNamedQuery(entityManager, clazz, namedQuery, parameters);
        return Optional.ofNullable(query.getSingleResultOrNull());
    }

    @Override
    public <E> E getEntityByDefaultNamedQuery(EntityManager entityManager, Class<E> clazz, Parameter... parameters) {
        LOGGER.info("getEntityByDefaultNamedQuery func: {}, {}", clazz, parameters);
        Query<E> query = getDefaultNamedQuery(entityManager, clazz, parameters);
        return query.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private <E> Query<E> getDynamicEntityGraphQuery(EntityManager entityManager, Class<E> clazz, String graph, Parameter... params) {
        LOGGER.info("getDynamicEntityGraphQuery func: {}, {}", clazz, graph);
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        String jpql = (params == null || params.length == 0)
                ? entityTable.getSelectAllJqlQuery()
                : entityTable.createFindJqlQuery(params);

        Query<E> query = (Query<E>) entityManager.createQuery(jpql, entityTable.getClazz());

        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }

        query.setHint("jakarta.persistence.loadgraph", entityManager.getEntityGraph(graph));
        return query;
    }

    private <E> Query<E> getNamedQuery(EntityManager entityManager, Class<E> clazz, String namedQuery, Parameter... params) {
        LOGGER.info("getNamedQuery func: {}, {}", clazz, namedQuery);
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        Query<E> query = (Query<E>) entityManager.createNamedQuery(namedQuery, clazz);
        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }
        return query;
    }

    private <E> TypedQuery<E> getNamedQueryMap(EntityManager session, Class<E> clazz, String namedQuery, Map<String, List<?>> paramMap) {
        LOGGER.info("getNamedQueryMap func: {}, {}", clazz, namedQuery);
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        Query<E> query = (Query<E>) session.createNamedQuery(namedQuery, clazz);
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

    private <E> Query<E> getDefaultNamedQuery(EntityManager entityManager, Class<E> clazz, Parameter... params) {
        LOGGER.info("getDefaultNamedQuery func: {}", clazz);
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        Query<E> query = (Query<E>) entityManager.createNamedQuery(entityTable.getDefaultNamedQuery(), clazz);
        if (params != null) {
            for (Parameter param : params) {
                query.setParameter(param.getName(), param.getValue());
            }
        }
        return query;
    }

}
