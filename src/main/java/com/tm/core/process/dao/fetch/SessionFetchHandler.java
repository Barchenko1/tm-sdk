package com.tm.core.process.dao.fetch;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.IFetchHandler;
import com.tm.core.process.dao.query.IQueryService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SessionFetchHandler implements IFetchHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionFetchHandler.class);

    private final SessionFactory sessionFactory;
    private final IQueryService queryService;

    public SessionFetchHandler(SessionFactory sessionFactory,
                               IQueryService queryService) {
        this.sessionFactory = sessionFactory;
        this.queryService = queryService;
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity list for class: {}, graph: {}", clazz.getName(), graph);
        Session session = sessionFactory.getCurrentSession();
        return queryService.getGraphEntityList(session, clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity for class: {}, graph: {}", clazz.getName(), graph);
        Session session = sessionFactory.getCurrentSession();
        return queryService.getGraphEntity(session, clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph optional entity for class: {}, graph: {}", clazz.getName(), graph);
        Session session = sessionFactory.getCurrentSession();
        return queryService.getGraphOptionalEntity(session, clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity list for class: {}, graph: {}", clazz.getName(), graph);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntityList(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity for class: {}, graph: {}", clazz.getName(), graph);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntity(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph optional entity for class: {}, graph: {}", clazz.getName(), graph);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphOptionalEntity(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity list for class: {}, named query: {}", clazz.getName(), namedQuery);
        Session session = sessionFactory.getCurrentSession();
        return queryService.getNamedQueryEntityList(session, clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        Session session = sessionFactory.getCurrentSession();
        return queryService.getNamedQueryEntity(session, clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query optional entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        Session session = sessionFactory.getCurrentSession();
        return queryService.getNamedQueryOptionalEntity(session, clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity list for class: {}, named query: {}", clazz.getName(), namedQuery);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntityList(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntity(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query optional entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryOptionalEntity(session, clazz, namedQuery, parameters);
        }
    }

}
