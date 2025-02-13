package com.tm.core.process.manager.generic;

import com.tm.core.process.dao.generic.GenericEntityDao;
import com.tm.core.process.dao.generic.IGenericEntityDao;
import com.tm.core.finder.parameter.Parameter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractGenericEntityOperationManager implements IGenericOperationManager{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericEntityOperationManager.class);

    private final IGenericEntityDao genericEntityDao;

    public AbstractGenericEntityOperationManager(SessionFactory sessionFactory, String entityPackage) {
        this.genericEntityDao = new GenericEntityDao(sessionFactory, entityPackage);
    }

    @Override
    public <E> void saveEntity(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        genericEntityDao.saveEntity(entitySupplier);
    }

    @Override
    public <E> void updateEntity(Supplier<E> entitySupplier) {
        LOGGER.info("Updating entity");
        genericEntityDao.updateEntity(entitySupplier);
    }

    @Override
    public <E> void deleteEntity(Supplier<E> entitySupplier) {
        LOGGER.info("Deleting entity");
        genericEntityDao.deleteEntity(entitySupplier);
    }

    @Override
    public void executeConsumer(Consumer<Session> consumer) {
        LOGGER.info("Saving entity");
        genericEntityDao.executeConsumer(consumer);
    }

    @Override
    public <E> void deleteEntityByParameter(Class<E> clazz, Parameter parameter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Deleting entity: ");
        sb.append(parameter.getName()).append(", ");
        sb.append(parameter.getValue());
        LOGGER.info("Deleting entity: {}", sb);
        genericEntityDao.findEntityAndDelete(clazz, parameter);
    }

    @Override
    public <E> void saveEntity(E entity) {
        LOGGER.info("Saving entity");
        genericEntityDao.persistEntity(entity);
    }

    @Override
    public <E> void updateEntity(E entity) {
        LOGGER.info("Updating entity");
        genericEntityDao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        genericEntityDao.deleteEntity(entity);
    }

    @Override
    public <E> E getEntity(Class<E> clazz, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericEntityDao.getEntity(clazz, parameter);
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Class<E> clazz, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericEntityDao.getOptionalEntity(clazz, parameter);
    }

    @Override
    public <E> List<E> getEntityList(Class<E> clazz, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericEntityDao.getEntityList(clazz, parameter);
    }

    @Override
    public <E> E getEntityGraph(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericEntityDao.getEntityGraph(clazz, graph, parameter);
    }

    @Override
    public <E> Optional<E> getOptionalEntityGraph(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericEntityDao.getOptionalEntityGraph(clazz, graph, parameter);
    }

    @Override
    public <E> List<E> getEntityGraphList(Class<E> clazz, String graph, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericEntityDao.getEntityGraphList(clazz, graph, parameter);
    }

    @Override
    public <E> E getEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericEntityDao.getEntityNamedQuery(clazz, namedQuery, parameter);
    }

    @Override
    public <E> Optional<E> getOptionalEntityNamedQuery(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return genericEntityDao.getOptionalEntityNamedQuery(clazz, namedQuery, parameter);
    }

    @Override
    public <E> List<E> getEntityNamedQueryList(Class<E> clazz, String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return genericEntityDao.getEntityNamedQueryList(clazz, namedQuery, parameter);
    }

    @Override
    public <E, R> R getEntityGraphDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = genericEntityDao.getEntityGraph(clazz, graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction).orElse(null);
    }

    @Override
    public <E, R> Optional<R> getOptionalEntityGraphDto(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = genericEntityDao.getEntityGraph(clazz, graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction);
    }

    @Override
    public <E, R> List<R> getEntityGraphDtoList(Class<E> clazz, String graph, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = genericEntityDao.getEntityGraphList(clazz, graph);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

    @Override
    public <E, R> List<R> getSubEntityGraphDtoList(Class<E> clazz, String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = genericEntityDao.getEntityGraphList(clazz, graph, parameter);
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
