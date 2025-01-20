package com.tm.core.dao.query;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.processor.finder.parameter.Parameter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class SearchWrapper implements ISearchWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchWrapper.class);

    private final SessionFactory sessionFactory;
    private final IEntityIdentifierDao entityIdentifierDao;

    public SearchWrapper(SessionFactory sessionFactory,
                         IEntityIdentifierDao entityIdentifierDao) {
        this.sessionFactory = sessionFactory;
        this.entityIdentifierDao = entityIdentifierDao;
    }

    @Override
    public <E> List<E> getEntityList(Class<?> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return entityIdentifierDao.getEntityList(session, clazz, parameters);
        }
    }

    @Override
    public <E> E getEntity(Class<?> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return entityIdentifierDao.getEntity(session, clazz, parameters);
        }
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Class<?> clazz, Parameter... parameters) {
        try (Session session = sessionFactory.openSession()) {
            return entityIdentifierDao.getOptionalEntity(session, clazz, parameters);
        }
    }

}
