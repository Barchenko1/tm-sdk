package com.tm.core.dao;

public abstract class AbstractEntityDao {

    protected Class<?> clazz;

    public AbstractEntityDao(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected <E> void classTypeChecker(E entity) {
        if (this.clazz != entity.getClass()) {
            throw new RuntimeException(
                    String.format("Invalid entity type %s != %s", this.clazz, entity.getClass())
            );
        }
    }
}
