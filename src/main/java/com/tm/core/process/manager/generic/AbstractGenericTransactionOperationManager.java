package com.tm.core.process.manager.generic;

import com.tm.core.process.dao.generic.IGenericTransactionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public abstract class AbstractGenericTransactionOperationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenericTransactionOperationManager.class);

    protected final IGenericTransactionDao genericTransactionDao;

    public AbstractGenericTransactionOperationManager(IGenericTransactionDao genericTransactionDao) {
        this.genericTransactionDao = genericTransactionDao;
    }

    protected  <E, R> R transformEntityToDto(E entity, Function<E, R> mapToDtoFunction) {
        LOGGER.info("Entity: {}", entity);
        R dto = mapToDtoFunction.apply(entity);
        LOGGER.info("Entity Dto: {}", dto);
        return dto;
    }
}
