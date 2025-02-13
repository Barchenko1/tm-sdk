package com.tm.core.process.dao.generic;

import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.process.dao.identifier.QueryService;
import com.tm.core.process.dao.transaction.ITransactionHandler;
import com.tm.core.process.dao.transaction.TransactionHandler;
import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.finder.scanner.EntityScanner;
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

public class AbstractGenericEntityDao implements IGenericEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericEntityDao.class);

    protected final SessionFactory sessionFactory;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;
    protected final ITransactionHandler transactionHandler;

    public AbstractGenericEntityDao(SessionFactory sessionFactory,
                                    String entityPackage) {
        this.sessionFactory = sessionFactory;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = initializerQueryService(entityPackage);
        this.transactionHandler = new TransactionHandler(sessionFactory);
    }

    private IQueryService initializerQueryService(String entityPackage) {
        EntityMappingManager entityMappingManager = new EntityMappingManager();
        new EntityScanner(entityMappingManager, entityPackage);
        return new QueryService(entityMappingManager);
    }

    @Override
    public <E> void persistEntity(E entity) {
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
    public <E> void findEntityAndUpdate(Class<E> clazz, E entity, Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E oldEntity = queryService.getEntity(session, clazz, parameters);
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
    public <E> void findEntityAndDelete(Class<E> clazz, Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = queryService.getEntity(session, clazz, parameters);
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
    public <E> List<E> getEntityList(Class<E> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityList(session, clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> List<E> getEntityGraphList(Class<E> clazz, String graphName, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityListGraph(session, clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> List<E> getEntityNamedQueryList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityListNamedQuery(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> E getEntity(Class<E> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntity(session, clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> E getEntityGraph(Class<E> clazz, String graphName, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityGraph(session, clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> E getEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getEntityNamedQuery(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Class<E> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getOptionalEntity(session, clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntityGraph(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getOptionalEntityGraph(session, clazz, graph, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getOptionalEntityNamedQuery(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
