package com.tm.core.process.dao.common.entityManager;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.common.IEntityDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntityManagerDao implements IEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityManagerDao.class);

    protected final Class<?> clazz;
    protected final EntityManager entityManager;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;

    public AbstractEntityManagerDao(EntityManager entityManager,
                                    IQueryService queryService,
                                    Class<?> clazz) {
        this.clazz = clazz;
        this.entityManager = entityManager;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = queryService;
    }

    @Override
    public <E> void persistEntity(E entity) {
        classTypeChecker(entity);
        entityManager.persist(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        classTypeChecker(entity);
        entityManager.merge(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        classTypeChecker(entity);
        entityManager.remove(entity);
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.persist(entity);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.merge(entity);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.remove(entity);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        consumer.accept(entityManager);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getGraphEntityList(String graphName, Parameter... parameters) {
        return (List<E>) queryService.getGraphEntityList(entityManager, clazz, graphName, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> List<E> getNamedQueryEntityList(String namedQuery, Parameter... parameters) {
        return (List<E>) queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getGraphEntity(String graphName, Parameter... parameters) {
        return (E) queryService.getGraphEntity(entityManager, clazz, graphName, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> E getNamedQueryEntity(String namedQuery, Parameter... parameters) {
        return (E) queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getGraphOptionalEntity(String graph, Parameter... parameters) {
        return (Optional<E>) queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<E> getNamedQueryOptionalEntity(String namedQuery, Parameter... parameters) {
        return (Optional<E>) queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
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
