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
        try (Session session = sessionFactory.openSession()) {
            session.persist(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void mergeEntity(E entity) {
        try (Session session = sessionFactory.openSession()) {
            session.merge(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void deleteEntity(E entity) {
        try (Session session = sessionFactory.openSession()) {
            session.remove(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        try (Session session = sessionFactory.openSession()) {
            E entity = supplier.get();
            session.persist(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        try (Session session = sessionFactory.openSession()) {
            E entity = supplier.get();
            session.merge(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        try (Session session = sessionFactory.openSession()) {
            E entity = supplier.get();
            session.remove(entity);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        try (Session session = sessionFactory.openSession()) {
            consumer.accept(session);
        } catch (Exception e) {
            LOGGER.warn("transaction error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graphName, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntityList(session, clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntityList(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graphName, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphEntity(session, clazz, graphName, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryEntity(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getGraphOptionalEntity(session, clazz, graph, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return queryService.getNamedQueryOptionalEntity(session, clazz, namedQuery, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
