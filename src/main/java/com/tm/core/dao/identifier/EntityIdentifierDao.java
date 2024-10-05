package com.tm.core.dao.identifier;

import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.scanner.EntityScanner;
import com.tm.core.processor.finder.scanner.IEntityScanner;
import com.tm.core.processor.finder.table.EntityTable;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class EntityIdentifierDao implements IEntityIdentifierDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityIdentifierDao.class);

    private final IThreadLocalSessionManager sessionManager;
    private final IEntityMappingManager entityMappingManager;

    public EntityIdentifierDao(IThreadLocalSessionManager sessionManager,
                               IEntityMappingManager entityMappingManager) {
        this.sessionManager = sessionManager;
        this.entityMappingManager = entityMappingManager;
    }

    @Override
    public <E> List<E> getEntityList(Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(clazz, parameters);
        return query.list();
    }

    @Override
    public <E> E getEntity(Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(clazz, parameters);
        return query.getSingleResult();
    }

    @Override
    public <E> Optional<E> getOptionalEntity(Class<?> clazz, Parameter... parameters) {
        Query<E> query = getEntityQuery(clazz, parameters);
        return query.uniqueResultOptional();
    }

    @SuppressWarnings("unchecked")
    public <E> Query<E> getEntityQuery(Class<?> clazz, Parameter... params) {
        Session session = sessionManager.getSession();
        EntityTable entityTable = entityMappingManager.getEntityTable(clazz);
        if (entityTable == null) {
            throw new RuntimeException("Invalid select class: " + clazz);
        }
        if ((params == null) || (params.length == 0)) {
            String queryStr = entityTable.getSelectAllQuery();
            return (Query<E>) session.createNativeQuery(queryStr, entityTable.getClazz());
        } else {
            String queryStr = entityTable.createFindQuery(params);
            Query<E> query = (Query<E>) session.createNativeQuery(queryStr, entityTable.getClazz());
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i].getValue());
            }
            return query;
        }
    }
}
