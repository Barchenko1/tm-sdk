package com.tm.core.process.dao.generic.session;

import com.tm.core.finder.parameter.Parameter;
import com.tm.core.process.dao.IFetchHandler;
import com.tm.core.process.dao.fetch.SessionFetchHandler;
import com.tm.core.process.dao.generic.IGenericDao;
import com.tm.core.process.dao.query.IQueryService;
import com.tm.core.process.dao.query.QueryService;
import com.tm.core.finder.manager.EntityMappingManager;
import com.tm.core.finder.scanner.EntityScanner;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public abstract class AbstractGenericSessionFactoryDao implements IGenericDao {

    protected final SessionFactory sessionFactory;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IQueryService queryService;
    protected final IFetchHandler fetchHandler;

    public AbstractGenericSessionFactoryDao(SessionFactory sessionFactory,
                                            String entityPackage) {
        this.sessionFactory = sessionFactory;
        this.entityFieldHelper = new EntityFieldHelper();
        this.queryService = initializerQueryService(entityPackage);
        this.fetchHandler = new SessionFetchHandler(sessionFactory, queryService);
    }

    private IQueryService initializerQueryService(String entityPackage) {
        EntityMappingManager entityMappingManager = new EntityMappingManager();
        new EntityScanner(entityMappingManager, entityPackage);
        return new QueryService(entityMappingManager);
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityList(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityList(clazz, namedQuery, parameters);

    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntity(clazz, graph, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntity(clazz, namedQuery, parameters);

    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphOptionalEntity(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityListClose(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphOptionalEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryOptionalEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityListClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryOptionalEntityClose(clazz, namedQuery, parameters);
    }

}
