package com.tm.core.process.dao.common;

import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.dao.transaction.TransactionHandler;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AbstractEntityDao implements IEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityDao.class);

    protected final Class<?> clazz;
    protected final SessionFactory sessionFactory;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;
    protected final ITransactionHandler transactionHandler;

    public AbstractEntityDao(SessionFactory sessionFactory,
                             IQueryService queryService,
                             Class<?> clazz) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = queryService;
        this.transactionHandler = new TransactionHandler(sessionFactory);
    }

    @Override
    public <E> void persistEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void mergeEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void deleteEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void findEntityAndUpdate(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E oldEntity = (E) queryService.getEntityByDefaultNamedQuery(session, this.clazz, parameters);
            entityFieldHelper.setId(entity, entityFieldHelper.findId(oldEntity));
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void findEntityAndDelete(Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = (E) queryService.getEntityByDefaultNamedQuery(session, this.clazz, parameters);
            classTypeChecker(entity);
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public <E> void saveEntity(Supplier<E> supplier) {
        transactionHandler.saveEntity(supplier);
    }

    @Override
    public <E> void updateEntity(Supplier<E> supplier) {
        transactionHandler.updateEntity(supplier);
    }

    @Override
    public <E> void deleteEntity(Supplier<E> supplier) {
        transactionHandler.deleteEntity(supplier);
    }

    @Override
    public void executeConsumer(Consumer<Session> consumer) {
        transactionHandler.executeConsumer(consumer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getGraphEntityList(String graphName, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return (List<E>) queryService.getGraphEntityList(session, this.clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return (List<E>) queryService.getNamedQueryEntityList(session, this.clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getGraphEntity(String graphName, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return (E) queryService.getGraphEntity(session, this.clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return (E) queryService.getNamedQueryEntity(session, this.clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return (Optional<E>) queryService.getGraphOptionalEntity(session, this.clazz, graph, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return (Optional<E>) queryService.getNamedQueryOptionalEntity(session, this.clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    private <E> void classTypeChecker(E entity) {
        if (this.clazz != entity.getClass()) {
            LOGGER.warn("Invalid entity type {} != {}", this.clazz, entity.getClass());
            throw new RuntimeException(
                    String.format("Invalid entity type %s != %s", this.clazz, entity.getClass())
            );
        }
    }

    @Override
    public Class<?> getEntityClass() {
        return this.clazz;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
