package com.tm.core.util.helper;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public final class EntityFieldHelper implements IEntityFieldHelper {

    public <E> Long findId(E entity) {
        Class<?> clazz = entity.getClass();
        try {
            while (clazz != null) {
                try {
                    Field idField = clazz.getDeclaredField("id");
                    idField.setAccessible(true);
                    Long id = (Long) idField.get(entity);

                    if (id == null) {
                        try {
                            Field idFlexField = clazz.getDeclaredField("*_id");
                            idFlexField.setAccessible(true);
                            return (Long) idFlexField.get(entity);
                        } catch (NoSuchFieldException ignored) {}
                    }
                    return id;
                } catch (NoSuchFieldException ignored) {
                }
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access id field", e);
        }
        throw new RuntimeException("No id field found in class hierarchy");
    }

    public void setId(Object entity, Object id) {
        Class<?> clazz = entity.getClass();
        Field idField = null;

        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    idField = field;
                    break;
                }
            }
            if (idField != null) {
                break;
            }
            clazz = clazz.getSuperclass();
        }

        if (idField == null) {
            throw new RuntimeException("ID field not found in entity class: " + entity.getClass().getName());
        }

        try {
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set ID field", e);
        }
    }
}
