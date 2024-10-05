package com.tm.core.dao.query;

import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.scanner.EntityScanner;
import com.tm.core.processor.finder.scanner.IEntityScanner;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SearchWrapper implements ISearchWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchWrapper.class);

    IThreadLocalSessionManager sessionManager;
    private final IEntityIdentifierDao entityIdentifierDao;

    public SearchWrapper(SessionFactory sessionFactory,
                         IEntityIdentifierDao entityIdentifierDao) {
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
        this.entityIdentifierDao = entityIdentifierDao;
    }

    @Override
    public <E> Supplier<List<E>> getEntityListSupplier(Class<?> clazz, Parameter... parameters) {
        return () ->
                entityIdentifierDao.getEntityList(clazz, parameters);
    }

    @Override
    public <E> Supplier<E> getEntitySupplier(Class<?> clazz, Parameter... parameters) {
        return () ->
                entityIdentifierDao.getEntity(clazz, parameters);
    }

    @Override
    public <E> Supplier<Optional<E>> getOptionalEntitySupplier(Class<?> clazz, Parameter... parameters) {
        return () ->
                entityIdentifierDao.getOptionalEntity(clazz, parameters);
    }

}
