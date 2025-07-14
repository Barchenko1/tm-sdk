package com.tm.core.process.manager.generic.operator;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.generic.IGenericDao;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericOperationManager extends AbstractGenericOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericOperationManager.class);

    public GenericOperationManager(IGenericDao genericDao) {
        super(genericDao);
    }

    @Override
    public <E> void persistSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        genericDao.persistSupplier(entitySupplier);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Updating entity");
        genericDao.mergeSupplier(entitySupplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Deleting entity");
        genericDao.deleteSupplier(entitySupplier);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        LOGGER.info("Execute consumer");
        genericDao.executeConsumer(consumer);
    }

    @Override
    public <T> T executeFunction(Function<EntityManager, T> function) {
        LOGGER.info("Execute function");
        return genericDao.executeFunction(function);
    }

    @Override
    public <E> void persistEntity(E entity) {
        LOGGER.info("Saving entity");
        genericDao.persistEntity(entity);
    }

    @Override
    public <E> void updateEntity(E entity) {
        LOGGER.info("Updating entity");
        genericDao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        genericDao.deleteEntity(entity);
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        return genericDao.getGraphEntityList(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return genericDao.getGraphEntity(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return genericDao.getGraphOptionalEntity(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        return genericDao.getGraphEntityListClose(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return genericDao.getGraphEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return genericDao.getGraphOptionalEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericDao.getNamedQueryEntityList(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericDao.getNamedQueryEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericDao.getNamedQueryOptionalEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericDao.getNamedQueryEntityListClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericDao.getNamedQueryEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericDao.getNamedQueryOptionalEntityClose(clazz, namedQuery, parameters);
    }

}
