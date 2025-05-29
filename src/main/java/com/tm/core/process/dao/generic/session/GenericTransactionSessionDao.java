package com.tm.core.process.dao.generic.session;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericTransactionSessionDao extends AbstractGenericTransactionSessionDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTransactionSessionDao.class);

    public GenericTransactionSessionDao(SessionFactory sessionFactory, String entityPackage) {
        super(sessionFactory, entityPackage);
    }

    @Override
    public <E> void persistEntity(E entity) {
        transactionHandler.persistEntity(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        transactionHandler.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        transactionHandler.deleteEntity(entity);
    }

    @Override
    public <E> void findEntityAndUpdate(Class<E> clazz, E entity, Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E oldEntity = queryService.getEntityByDefaultNamedQuery(session, clazz, parameters);
            entityFieldHelper.setId(entity, entityFieldHelper.findId(oldEntity));
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void findEntityAndDelete(Class<E> clazz, Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = queryService.getEntityByDefaultNamedQuery(session, clazz, parameters);
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        transactionHandler.persistSupplier(supplier);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        transactionHandler.mergeSupplier(supplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        transactionHandler.deleteSupplier(supplier);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        transactionHandler.executeConsumer(consumer);
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
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryOptionalEntity(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
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
