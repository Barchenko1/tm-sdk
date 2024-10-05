package com.tm.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityDao.class);

    protected Class<?> clazz;

    public AbstractEntityDao(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected <E> void classTypeChecker(E entity) {
        if (this.clazz != entity.getClass()) {
            LOGGER.warn("Invalid entity type {} != {}", this.clazz, entity.getClass());
            throw new RuntimeException(
                    String.format("Invalid entity type %s != %s", this.clazz, entity.getClass())
            );
        }
    }
}
