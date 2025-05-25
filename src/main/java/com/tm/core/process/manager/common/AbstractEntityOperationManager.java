package com.tm.core.process.manager.common;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.ITransactionEntityDao;
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

    private final ITransactionEntityDao dao;

    public AbstractEntityOperationManager(ITransactionEntityDao dao) {
        this.dao = dao;
    }

    @Override
    public <E> void persistSupplier(Supplier<E> entitySupplier) {
        LOGGER.info("Saving entity");
        dao.persistSupplier(entitySupplier);
    }

    @Override
    public <E> void updateSupplier(Supplier<E> entitySupplier) {
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
    public <E> void saveEntity(E entity) {
        LOGGER.info("Saving entity");
        dao.persistEntity(entity);
    }

    @Override
    public <E> void updateEntity(E entity) {
        LOGGER.info("Updating entity");
        dao.mergeEntity(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        LOGGER.info("Deleting entity");
        dao.deleteEntity(entity);
    }

    @Override
    public <E> E getGraphEntity(String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return dao.getGraphEntity(graph, parameter);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter parameter) {
        LOGGER.info("Getting entity");
        return dao.getGraphOptionalEntity(graph, parameter);
    }

    @Override
    public <E> List<E> getGraphEntityList(String graph, Parameter parameter) {
        LOGGER.info("Getting entity list");
        return dao.getGraphEntityList(graph, parameter);
    }

    @Override
    public <E, R> R getGraphEntityDto(String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = dao.getGraphEntity(graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction).orElse(null);
    }

    @Override
    public <E, R> Optional<R> getGraphOptionalEntityDto(String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = dao.getGraphEntity(graph, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction);
    }

    @Override
    public <E, R> List<R> getGraphEntityDtoList(String graph, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = dao.getGraphEntityList(graph);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

    @Override
    public <E, R> List<R> getSubGraphEntityDtoList(String graph, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = dao.getGraphEntityList(graph, parameter);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
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
    public <E> E getNamedQueryEntity(String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return dao.getNamedQueryEntity(namedQuery, parameter);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter parameter) {
        LOGGER.info("Getting entity");
        return dao.getNamedQueryOptionalEntity(namedQuery, parameter);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(String namedQuery) {
        LOGGER.info("Getting entity list");
        return dao.getNamedQueryEntityList(namedQuery);
    }

    @Override
    public <E, R> R getNamedQueryEntityDto(String namedQuery, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = dao.getNamedQueryEntity(namedQuery, parameter);
        return Optional.ofNullable(entity).map(mapToDtoFunction).orElse(null);
    }

    @Override
    public <E, R> Optional<R> getNamedQueryOptionalEntityDto(String namedQuery, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto");
        E entity = dao.getNamedQueryEntity(namedQuery, parameter);
        return Optional.ofNullable(entity)
                .map(o -> transformEntityToDto(o, mapToDtoFunction));
    }

    @Override
    public <E, R> List<R> getNamedQueryEntityDtoList(String namedQuery, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = dao.getNamedQueryEntityList(namedQuery);
        return entityList.stream()
                .map(entity -> transformEntityToDto(entity, mapToDtoFunction))
                .toList();
    }

    @Override
    public <E, R> List<R> getSubNamedQueryEntityDtoList(String namedQuery, Parameter parameter, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Getting entity dto list");
        List<E> entityList = dao.getNamedQueryEntityList(namedQuery, parameter);
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
