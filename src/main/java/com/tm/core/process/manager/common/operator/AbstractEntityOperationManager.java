package com.tm.core.process.manager.common.operator;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.IEntityDao;
import com.tm.core.process.manager.common.IEntityOperationManager;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractEntityOperationManager implements IEntityOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityOperationManager.class);

    private final IEntityDao entityDao;

    public AbstractEntityOperationManager(IEntityDao entityDao) {
        this.entityDao = entityDao;
    }

    @Override
    public <E> void persistSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        entityDao.persistSupplier(entitySupplier);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Updating entity");
        entityDao.mergeSupplier(entitySupplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Deleting entity");
        entityDao.deleteSupplier(entitySupplier);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        LOGGER.info("Execute consumer");
        entityDao.executeConsumer(consumer);
    }

    @Override
    public <T> T executeFunction(Function<EntityManager, T> function) {
        LOGGER.info("Execute function");
        return entityDao.executeFunction(function);
    }

    @Override
    public <E> void persistEntity(E entity) {
        LOGGER.info("Saving entity");
        entityDao.persistEntity(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        LOGGER.info("Updating entity");
        entityDao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        entityDao.deleteEntity(entity);
    }

    @Override
    public <E> E getGraphEntity(String graph, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return entityDao.getGraphEntity(graph, parameter);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return entityDao.getGraphOptionalEntity(graph, parameter);
    }

    @Override
    public <E> List<E> getGraphEntityList(String graph, Parameter... parameter) {
        LOGGER.info("Getting entity list");
        return entityDao.getGraphEntityList(graph, parameter);
    }

    @Override
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return entityDao.getNamedQueryEntity(namedQuery, parameter);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return entityDao.getNamedQueryOptionalEntity(namedQuery, parameter);
    }

    @Override
    public <E> E getNamedQueryEntity(String namedQuery) {
        LOGGER.info("Getting entity");
        return entityDao.getNamedQueryEntity(namedQuery);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery) {
        LOGGER.info("Getting entity");
        return entityDao.getNamedQueryOptionalEntity(namedQuery);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(String namedQuery) {
        LOGGER.info("Getting entity list");
        return entityDao.getNamedQueryEntityList(namedQuery);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(String graph, Parameter... parameters) {
        return entityDao.getGraphEntityListClose(graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(String graph, Parameter... parameters) {
        return entityDao.getGraphEntityClose(graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(String graph, Parameter... parameters) {
        return entityDao.getGraphOptionalEntityClose(graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        return entityDao.getNamedQueryEntityList(namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(String namedQuery, Parameter... parameters) {
       return entityDao.getNamedQueryEntityListClose(namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(String namedQuery, Parameter... parameters) {
        return entityDao.getNamedQueryEntityClose(namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(String namedQuery, Parameter... parameters) {
        return entityDao.getNamedQueryOptionalEntityClose(namedQuery, parameters);
    }

}
