package com.tm.core.transaction;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class AbstractTransactionManager implements ITransactionManager {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTransactionManager.class);

    protected final SessionFactory sessionFactory;

    public AbstractTransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <E> void useTransaction(E value) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            Field[] fields = value.getClass().getDeclaredFields();
            transaction = session.beginTransaction();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                if (field.getType().getName().startsWith("java.util")
                        && !isListEmpty(field, value)) {
                    Collection<?> collection = (Collection<?>) field.get(value);
                    collection.stream()
                            .filter(this::isIdExist)
                            .forEach(session::persist);
                    continue;
                }
                if (!fieldType.getName().startsWith("java.lang")
                        && !fieldType.isPrimitive()
                        && !fieldType.getName().startsWith("java.util")
                        && isFieldValueValid(field, value)) {
                    field.setAccessible(true);
                    Object object;
                    try {
                        object = field.get(value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    session.persist(object);
                }
            }
            session.merge(value);
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private boolean isFieldValueValid(Field field, Object value) {
        field.setAccessible(true);
        Object object;
        try {
            object = field.get(value);
            if (object != null) {
                Field innerField = object.getClass().getDeclaredField("id");
                innerField.setAccessible(true);
                return (long) innerField.get(object) == 0;
            } else {
                return false;
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isIdExist(Object value) {
        try {
            Field innerField = value.getClass().getDeclaredField("id");
            innerField.setAccessible(true);
            return (long) innerField.get(value) == 0;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private <E> boolean isListEmpty(Field field, E value) {
        try {
            Method method = field.getType().getDeclaredMethod("size");
            field.setAccessible(true);
            Collection<?> collection = (Collection<?>) field.get(value);
            Integer arraySize = (Integer) method.invoke(collection);
            return arraySize == 0;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
