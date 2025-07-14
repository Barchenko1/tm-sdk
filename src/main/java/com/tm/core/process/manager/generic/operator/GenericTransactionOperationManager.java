package com.tm.core.process.manager.generic.operator;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.generic.IGenericTransactionDao;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericTransactionOperationManager extends AbstractGenericTransactionOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTransactionOperationManager.class);

    public GenericTransactionOperationManager(IGenericTransactionDao genericTransactionDao) {
        super(genericTransactionDao);
    }

    @Override
    public <E> void persistSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        genericTransactionDao.persistSupplier(entitySupplier);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Updating entity");
        genericTransactionDao.mergeSupplier(entitySupplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Deleting entity");
        genericTransactionDao.deleteSupplier(entitySupplier);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        LOGGER.info("Execute consumer");
        genericTransactionDao.executeConsumer(consumer);
    }

    @Override
    public <T> T executeFunction(Function<EntityManager, T> function) {
        LOGGER.info("Execute function");
        return genericTransactionDao.executeFunction(function);
    }

    @Override
    public <E> void deleteEntityByParameter(Class<E> clazz, Parameter parameter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Deleting entity: ");
        sb.append(parameter.getName()).append(", ");
        sb.append(parameter.getValue());
        LOGGER.info("Deleting entity: {}", sb);
        genericTransactionDao.findEntityAndDelete(clazz, parameter);
    }

    @Override
    public <E> void persistEntity(E entity) {
        LOGGER.info("Saving entity");
        genericTransactionDao.persistEntity(entity);
    }

    @Override
    public <E> void updateEntity(E entity) {
        LOGGER.info("Updating entity");
        genericTransactionDao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        genericTransactionDao.deleteEntity(entity);
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        return genericTransactionDao.getGraphEntityList(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return genericTransactionDao.getGraphEntity(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return genericTransactionDao.getGraphOptionalEntity(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        return genericTransactionDao.getGraphEntityListClose(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return genericTransactionDao.getGraphEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return genericTransactionDao.getGraphOptionalEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericTransactionDao.getNamedQueryEntityList(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericTransactionDao.getNamedQueryEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericTransactionDao.getNamedQueryOptionalEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericTransactionDao.getNamedQueryEntityListClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericTransactionDao.getNamedQueryEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return genericTransactionDao.getNamedQueryOptionalEntityClose(clazz, namedQuery, parameters);
    }

}
