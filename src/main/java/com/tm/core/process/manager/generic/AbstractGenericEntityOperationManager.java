package com.tm.core.process.manager.generic;

import com.tm.core.process.dao.generic.IGenericTransactionDao;
import com.tm.core.process.dao.generic.session.GenericTransactionSessionDao;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public abstract class AbstractGenericEntityOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericEntityOperationManager.class);

    protected final IGenericTransactionDao genericTransactionDao;

    public AbstractGenericEntityOperationManager(SessionFactory sessionFactory, String entityPackage) {
        this.genericTransactionDao = new GenericTransactionSessionDao(sessionFactory, entityPackage);
    }

    protected  <E, R> R transformEntityToDto(E entity, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Entity: {}", entity);
        R dto = mapToDtoFunction.apply(entity);
        LOGGER.info("Entity Dto: {}", dto);
        return dto;
    }
}
