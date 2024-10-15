package com.tm.core.util.helper;

import jakarta.persistence.Id;

import java.lang.reflect.Field;

public final class EntityFieldHelper implements IEntityFieldHelper {

    public <E> Long findId(E entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true); // In case the field is private
            Long id = (Long) idField.get(entity);
            if (id == null) {
                Field idFlexField = entity.getClass().getDeclaredField("*_id");
                idFlexField.setAccessible(true);
                Long idFlex = (Long) idFlexField.get(entity);
                return idFlex;
            }
            return id;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to access id field", e);
        }
    }

    public void setId(Object entity, Object id) {
        try {
            Field idField = null;
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    idField = field;
                    break;
                }
            }
            if (idField == null) {
                throw new RuntimeException("ID field not found in entity class: " + entity.getClass().getName());
            }
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set ID field {}", e);
        }
    }
}
