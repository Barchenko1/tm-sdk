package com.tm.core.process.manager.generic;

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

public class GenericTransactionOperationManager extends AbstractGenericTransactionOperationManager implements IGenericOperationManager {
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
    public <E> void updateSupplier(Supplier<E> entitySupplier) {
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
        LOGGER.info("Saving entity");
        genericTransactionDao.executeConsumer(consumer);
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
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionDao.getGraphEntity(clazz, graph, parameter);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionDao.getGraphOptionalEntity(clazz, graph, parameter);
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericTransactionDao.getGraphEntityList(clazz, graph, parameter);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionDao.getNamedQueryEntity(clazz, namedQuery, parameter);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionDao.getNamedQueryOptionalEntity(clazz, namedQuery, parameter);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericTransactionDao.getNamedQueryEntityList(clazz, namedQuery, parameter);
    }

    @Override
    public <E, R> R getGraphEntityDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = genericTransactionDao.getGraphEntity(clazz, graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction).orElse(null);
    }

    @Override
    public <E, R> Optional<R> getGraphOptionalEntityDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = genericTransactionDao.getGraphEntity(clazz, graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction);
    }

    @Override
    public <E, R> List<R> getGraphEntityDtoList(Class<E> clazz, String graph, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = genericTransactionDao.getGraphEntityList(clazz, graph);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

    @Override
    public <E, R> List<R> getSubGraphEntityDtoList(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = genericTransactionDao.getGraphEntityList(clazz, graph, parameter);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

}
