package com.tm.core.process.dao.generic.session;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericSessionDao extends AbstractGenericSessionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericSessionDao.class);

    public GenericSessionDao(SessionFactory sessionFactory, String entityPackage) {
        super(sessionFactory, entityPackage);
    }

    @Override
    public <E> void persistEntity(E entity) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.persist(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void mergeEntity(E entity) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.merge(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void deleteEntity(E entity) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.remove(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        try {
            Session session = sessionFactory.getCurrentSession();
            E entity = supplier.get();
            session.persist(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        try {
            Session session = sessionFactory.getCurrentSession();
            E entity = supplier.get();
            session.merge(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        try {
            Session session = sessionFactory.getCurrentSession();
            E entity = supplier.get();
            session.remove(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        try {
            Session session = sessionFactory.getCurrentSession();
            consumer.accept(session);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityList(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityList(clazz, namedQuery, parameters);

    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntity(clazz, graph, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntity(clazz, namedQuery, parameters);

    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphOptionalEntity(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityListClose(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphOptionalEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryOptionalEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityListClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryOptionalEntityClose(clazz, namedQuery, parameters);
    }
}
