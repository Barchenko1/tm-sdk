package com.tm.core.process.manager.generic;

import com.tm.core.process.dao.generic.session.GenericTransactionSessionDao;
import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.generic.session.IGenericTransactionSessionDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGenericEntityOperationManager implements IGenericOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericEntityOperationManager.class);

    private final IGenericTransactionSessionDao genericTransactionSessionDao;

    public AbstractGenericEntityOperationManager(SessionFactory sessionFactory, String entityPackage) {
        this.genericTransactionSessionDao = new GenericTransactionSessionDao(sessionFactory, entityPackage);
    }

    @Override
    public <E> void persistSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        genericTransactionSessionDao.persistSupplier(entitySupplier);
    }

    @Override
    public <E> void updateSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Updating entity");
        genericTransactionSessionDao.updateSupplier(entitySupplier);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Deleting entity");
        genericTransactionSessionDao.deleteSupplier(entitySupplier);
    }

    @Override
    public void executeConsumer(Consumer<Session> consumer) {
        LOGGER.info("Saving entity");
        genericTransactionSessionDao.executeConsumer(consumer);
    }

    @Override
    public <E> void deleteEntityByParameter(Class<E> clazz, Parameter parameter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Deleting entity: ");
        sb.append(parameter.getName()).append(", ");
        sb.append(parameter.getValue());
        LOGGER.info("Deleting entity: {}", sb);
        genericTransactionSessionDao.findEntityAndDelete(clazz, parameter);
    }

    @Override
    public <E> void saveEntity(E entity) {
        LOGGER.info("Saving entity");
        genericTransactionSessionDao.persistEntity(entity);
    }

    @Override
    public <E> void updateEntity(E entity) {
        LOGGER.info("Updating entity");
        genericTransactionSessionDao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        genericTransactionSessionDao.deleteEntity(entity);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionSessionDao.getGraphEntity(clazz, graph, parameter);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionSessionDao.getGraphOptionalEntity(clazz, graph, parameter);
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericTransactionSessionDao.getGraphEntityList(clazz, graph, parameter);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionSessionDao.getNamedQueryEntity(clazz, namedQuery, parameter);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericTransactionSessionDao.getNamedQueryOptionalEntity(clazz, namedQuery, parameter);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericTransactionSessionDao.getNamedQueryEntityList(clazz, namedQuery, parameter);
    }

    @Override
    public <E, R> R getGraphEntityDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = genericTransactionSessionDao.getGraphEntity(clazz, graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction).orElse(null);
    }

    @Override
    public <E, R> Optional<R> getGraphOptionalEntityDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = genericTransactionSessionDao.getGraphEntity(clazz, graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction);
    }

    @Override
    public <E, R> List<R> getGraphEntityDtoList(Class<E> clazz, String graph, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = genericTransactionSessionDao.getGraphEntityList(clazz, graph);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

    @Override
    public <E, R> List<R> getSubGraphEntityDtoList(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = genericTransactionSessionDao.getGraphEntityList(clazz, graph, parameter);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

    private <E, R> R transformEntityToDto(E entity, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Entity: {}", entity);
        R dto = mapToDtoFunction.apply(entity);
        LOGGER.info("Entity Dto: {}", dto);
        return dto;
    }
}
