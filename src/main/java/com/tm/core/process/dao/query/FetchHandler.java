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
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntityList(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntity(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphOptionalEntity(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntityList(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntity(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryOptionalEntity(session, clazz, namedQuery, parameters);
        }
    }

}
