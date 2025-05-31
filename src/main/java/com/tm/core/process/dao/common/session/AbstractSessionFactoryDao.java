package com.tm.core.process.dao.common.session;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.IEntityDao;
import com.tm.core.process.dao.IFetchHandler;
import com.tm.core.process.dao.fetch.SessionFetchHandler;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractSessionFactoryDao implements IEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSessionFactoryDao.class);

    protected final Class<?> clazz;
    protected final SessionFactory sessionFactory;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;
    protected final IFetchHandler fetchHandler;

    public AbstractSessionFactoryDao(SessionFactory sessionFactory,
                                     IQueryService queryService,
                                     Class<?> clazz) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = queryService;
        this.fetchHandler = new SessionFetchHandler(sessionFactory, queryService);
    }

    @Override
    public <E> void persistEntity(E entity) {
        classTypeChecker(entity);
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
        classTypeChecker(entity);
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
        classTypeChecker(entity);
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
    @SuppressWarnings("unchecked")
    public <E> List<E> getGraphEntityList(String graph, Parameter... parameters) {
        return (List<E>) fetchHandler.getGraphEntityList(clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        return (List<E>) fetchHandler.getNamedQueryEntityList(clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getGraphEntity(String graph, Parameter... parameters) {
        return (E) fetchHandler.getGraphEntity(clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters) {
        return (E) fetchHandler.getNamedQueryEntity(clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameters) {
        return (Optional<E>) fetchHandler.getGraphOptionalEntity(clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters) {
        return (Optional<E>) fetchHandler.getNamedQueryOptionalEntity(clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getGraphEntityListClose(String graph, Parameter... parameters) {
        return (List<E>) fetchHandler.getGraphEntityListClose(clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getGraphEntityClose(String graph, Parameter... parameters) {
        return (E) fetchHandler.getGraphEntityClose(clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getGraphOptionalEntityClose(String graph, Parameter... parameters) {
        return (Optional<E>) fetchHandler.getGraphOptionalEntityClose(clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getNamedQueryEntityListClose(String namedQuery, Parameter... parameters) {
        return (List<E>) fetchHandler.getNamedQueryEntityListClose(clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getNamedQueryEntityClose(String namedQuery, Parameter... parameters) {
        return (E) fetchHandler.getNamedQueryEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getNamedQueryOptionalEntityClose(String namedQuery, Parameter... parameters) {
        return (Optional<E>) fetchHandler.getNamedQueryOptionalEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public Class<?> getEntityClass() {
        return this.clazz;
    }

    protected  <E> void classTypeChecker(E entity) {
        if (this.clazz != entity.getClass()) {
            LOGGER.warn("Invalid entity type {} != {}", this.clazz, entity.getClass());
            throw new RuntimeException(
                    String.format("Invalid entity type %s != %s", this.clazz, entity.getClass())
            );
        }
    }

}
