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

public class GenericSessionFactoryDao extends AbstractGenericSessionFactoryDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericSessionFactoryDao.class);

    public GenericSessionFactoryDao(SessionFactory sessionFactory, String entityPackage) {
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
}
