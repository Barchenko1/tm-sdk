package com.tm.core.process.dao.query;

import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.finder.parameter.Parameter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class FetchHandler implements IFetchHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchHandler.class);

    private final SessionFactory sessionFactory;
    private final IQueryService queryService;

    public FetchHandler(SessionFactory sessionFactory,
                        IQueryService queryService) {
        this.sessionFactory = sessionFactory;
        this.queryService = queryService;
    }

    @Override
    public <E> List<E> getEntityList(Class<E> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityList(session, clazz, parameters);
        }
    }

    @Override
    public <E> E getEntity(Class<E> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntity(session, clazz, parameters);
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Class<E> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getOptionalEntity(session, clazz, parameters);
        }
    }

    @Override
    public <E> List<E> getEntityListGraph(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityListGraph(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> E getEntityGraph(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityGraph(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntityGraph(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getOptionalEntityGraph(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> List<E> getEntityNamedQueryList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityListNamedQuery(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> E getEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityNamedQuery(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getOptionalEntityNamedQuery(session, clazz, namedQuery, parameters);
        }
    }

}
