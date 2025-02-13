package com.tm.core.process.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEntityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityChecker.class);

    protected Class<?> clazz;

    public AbstractEntityChecker(Class<?> clazz) {
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
