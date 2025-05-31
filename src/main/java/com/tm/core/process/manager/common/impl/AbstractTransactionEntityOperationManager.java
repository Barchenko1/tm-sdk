package com.tm.core.process.manager.common.impl;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.ITransactionEntityDao;
import com.tm.core.process.manager.common.ITransactionEntityOperationManager;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractTransactionEntityOperationManager implements ITransactionEntityOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionEntityOperationManager.class);

    private final ITransactionEntityDao dao;

    public AbstractTransactionEntityOperationManager(ITransactionEntityDao dao) {
        this.dao = dao;
    }

    @Override
    public <E> void persistSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        dao.persistSupplier(entitySupplier);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Updating entity");
        dao.mergeSupplier(entitySupplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Deleting entity");
        dao.deleteSupplier(entitySupplier);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        LOGGER.info("Saving entity");
        dao.executeConsumer(consumer);
    }

    @Override
    public void deleteEntityByParameter(Parameter parameter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Deleting entity: ");
        sb.append(parameter.getName()).append(", ");
        sb.append(parameter.getValue());
        LOGGER.info("Deleting entity: {}", sb);
        dao.findEntityAndDelete(parameter);
    }

    @Override
    public <E> void persistEntity(E entity) {
        LOGGER.info("Saving entity");
        dao.persistEntity(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        LOGGER.info("Updating entity");
        dao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        dao.deleteEntity(entity);
    }

    @Override
    public <E> E getGraphEntity(String graph, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return dao.getGraphEntity(graph, parameter);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return dao.getGraphOptionalEntity(graph, parameter);
    }

    @Override
    public <E> List<E> getGraphEntityList(String graph, Parameter... parameter) {
        LOGGER.info("Getting entity list");
        return dao.getGraphEntityList(graph, parameter);
    }

    @Override
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return dao.getNamedQueryEntity(namedQuery, parameter);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameter) {
        LOGGER.info("Getting entity");
        return dao.getNamedQueryOptionalEntity(namedQuery, parameter);
    }

    @Override
    public <E> E getNamedQueryEntity(String namedQuery) {
        LOGGER.info("Getting entity");
        return dao.getNamedQueryEntity(namedQuery);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery) {
        LOGGER.info("Getting entity");
        return dao.getNamedQueryOptionalEntity(namedQuery);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(String namedQuery) {
        LOGGER.info("Getting entity list");
        return dao.getNamedQueryEntityList(namedQuery);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(String graph, Parameter... parameters) {
        return dao.getGraphEntityListClose(graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(String graph, Parameter... parameters) {
        return dao.getGraphEntityClose(graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(String graph, Parameter... parameters) {
        return dao.getGraphOptionalEntityClose(graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        return dao.getNamedQueryEntityList(namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(String namedQuery, Parameter... parameters) {
       return dao.getNamedQueryEntityListClose(namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(String namedQuery, Parameter... parameters) {
        return dao.getNamedQueryEntityClose(namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(String namedQuery, Parameter... parameters) {
        return dao.getNamedQueryOptionalEntityClose(namedQuery, parameters);
    }

}
