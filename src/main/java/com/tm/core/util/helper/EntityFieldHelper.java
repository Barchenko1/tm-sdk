package com.tm.core.util.helper;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public final class EntityFieldHelper implements IEntityFieldHelper {

    public <E> Long findId(E entity) {
        Class<?> clazz = entity.getClass();
        Long id = null;
        try {
            while (clazz != null) {
                try {
                    Field idField = clazz.getDeclaredField("id");
                    idField.setAccessible(true);
                    id = (Long) idField.get(entity);

                    if (id == null) {
                        try {
                            Field idFlexField = clazz.getDeclaredField("*_id");
                            idFlexField.setAccessible(true);
                            return (Long) idFlexField.get(entity);
                        } catch (NoSuchFieldException ignored) {

                        }
                    }
                } catch (NoSuchFieldException ignored) {}
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access id field" + e.getMessage());
        }
        if (id == null) {
            throw new RuntimeException("No id field found in class hierarchy");
        }
        return id;
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
            throw new RuntimeException("ID field not found in entity class: " + entity.getClass());
        }

        try {
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set ID field" + e.getMessage());
        }
    }
}
