package com.tm.core.dao.query;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.processor.finder.parameter.Parameter;
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

    public SearchWrapper(IThreadLocalSessionManager sessionManager,
                         IEntityIdentifierDao entityIdentifierDao) {
        this.sessionManager = sessionManager;
        this.entityIdentifierDao = entityIdentifierDao;
    }

    @Override
    public <E> Supplier<List<E>> getEntityListSupplier(Class<?> clazz, Parameter... parameters) {
        try {
            return () -> entityIdentifierDao.getEntityList(clazz, parameters);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Supplier<E> getEntitySupplier(Class<?> clazz, Parameter... parameters) {
        try {
            return () -> entityIdentifierDao.getEntity(clazz, parameters);
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Supplier<Optional<E>> getOptionalEntitySupplier(Class<?> clazz, Parameter... parameters) {
        try {
            return () -> entityIdentifierDao.getOptionalEntity(clazz, parameters);
        } finally {
            sessionManager.closeSession();
        }
    }

}
