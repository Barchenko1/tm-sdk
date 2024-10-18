package com.tm.core.util.helper;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HibernateInitializer implements IHibernateInitializer {

    SessionFactory sessionFactory;

    public HibernateInitializer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E initializeEntity(Class<?> clazz, long id) {
        E entity;
        try (Session session = sessionFactory.openSession()){
            entity = (E) session.get(clazz, id);
            Hibernate.initialize(entity);
        }
        return entity;
    }

    @Override
    public  <E> void initializeInnerEntities(E entity) {
        List<Field> fieldsToInitialize = getNonPrimitiveFields(entity.getClass());
        for (Field field : fieldsToInitialize) {
            field.setAccessible(true);

            Object fieldValue = null;
            try {
                fieldValue = field.get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (fieldValue != null) {
                Hibernate.initialize(fieldValue);
            }
        }
    }

    private List<Field> getNonPrimitiveFields(Class<?> clazz) {
        List<Field> nonPrimitiveFields = new ArrayList<>();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            if (!fieldType.isPrimitive() && !isDefaultJavaType(fieldType)) {
                nonPrimitiveFields.add(field);
            }
        }

        return nonPrimitiveFields;
    }

    private boolean isDefaultJavaType(Class<?> fieldType) {
        List<Class<?>> defaultJavaTypes = Arrays.asList(
                String.class,
                Integer.class, Long.class, Double.class, Boolean.class, Float.class, Byte.class,
                Short.class, Character.class, Void.class
        );

        return defaultJavaTypes.contains(fieldType);
    }
}
