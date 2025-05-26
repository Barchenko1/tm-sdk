package com.tm.core.process.dao.common.session;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.IEntityDao;
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

    public AbstractSessionFactoryDao(SessionFactory sessionFactory,
                                     IQueryService queryService,
                                     Class<?> clazz) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = queryService;
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
    public <E> List<E> getGraphEntityList(String graphName, Parameter... parameters) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (List<E>) queryService.getGraphEntityList(session, this.clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (List<E>) queryService.getNamedQueryEntityList(session, this.clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getGraphEntity(String graphName, Parameter... parameters) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (E) queryService.getGraphEntity(session, this.clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (E) queryService.getNamedQueryEntity(session, this.clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameters) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (Optional<E>) queryService.getGraphOptionalEntity(session, this.clazz, graph, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters) {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (Optional<E>) queryService.getNamedQueryOptionalEntity(session, this.clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
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

}
