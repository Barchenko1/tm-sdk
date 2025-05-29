package com.tm.core.process.dao.fetch;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.IFetchHandler;
import com.tm.core.process.dao.query.IQueryService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class EntityManagerFetchHandler implements IFetchHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerFetchHandler.class);

    private final EntityManager entityManager;
    private final IQueryService queryService;

    public EntityManagerFetchHandler(EntityManager entityManager,
                                     IQueryService queryService) {
        this.entityManager = entityManager;
        this.queryService = queryService;
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity list for class: {}, graph: {}", clazz.getName(), graph);
        return queryService.getGraphEntityList(entityManager, clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity for class: {}, graph: {}", clazz.getName(), graph);
        return queryService.getGraphEntity(entityManager, clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph optional entity for class: {}, graph: {}", clazz.getName(), graph);
        return queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity list for class: {}, graph: {}", clazz.getName(), graph);
        try {
            return queryService.getGraphEntityList(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity for class: {}, graph: {}", clazz.getName(), graph);
        try {
            return queryService.getGraphEntity(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph optional entity for class: {}, graph: {}", clazz.getName(), graph);
        try {
            return queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity list for class: {}, named query: {}", clazz.getName(), namedQuery);
        return queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        return queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query optional entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        return queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity list for class: {}, named query: {}", clazz.getName(), namedQuery);
        try {
            return queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        try {
            return queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query optional entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        try {
            return queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

}
