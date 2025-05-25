package com.tm.core.process.dao.fetch;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.query.IQueryService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
        LOGGER.info("Getting graph entity list for class: {}, graph: {}", clazz.getName(), graph);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntityList(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph entity for class: {}, graph: {}", clazz.getName(), graph);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntity(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        LOGGER.info("Getting graph optional entity for class: {}, graph: {}", clazz.getName(), graph);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphOptionalEntity(session, clazz, graph, parameters);
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity list for class: {}, named query: {}", clazz.getName(), namedQuery);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntityList(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntity(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        LOGGER.info("Getting named query optional entity for class: {}, named query: {}", clazz.getName(), namedQuery);
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryOptionalEntity(session, clazz, namedQuery, parameters);
        }
    }

    @Override
    public <E> List<E> getTransactionEntityList(Function<Session, List<E>> function) {
        LOGGER.info("Getting transaction entity list");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<E> result = function.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            LOGGER.error("Error getting transaction entity list", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> E getTransactionEntity(Function<Session, E> function) {
        LOGGER.info("Getting transaction entity");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E result = function.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            LOGGER.error("Error getting transaction entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> Optional<E> getTransactionOptionalEntity(Function<Session, Optional<E>> function) {
        LOGGER.info("Getting transaction optional entity");
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Optional<E> optional = function.apply(session);
            transaction.commit();
            return optional;
        } catch (Exception e) {
            LOGGER.error("Error getting transaction optional entity", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }


}
