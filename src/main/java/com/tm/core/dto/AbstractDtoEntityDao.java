package com.tm.core.dto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractDtoEntityDao implements IDtoEntityDao {

    protected final SessionFactory sessionFactory;

    public AbstractDtoEntityDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getDto(String sqlQuery, Class<?> clazz) {
        try (Session session = sessionFactory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            TypedQuery<Tuple> nativeQuery = (TypedQuery<Tuple>) (entityManager).createNativeQuery(sqlQuery, Tuple.class);
            Tuple tuple = nativeQuery.getSingleResult();
            Field[] classFields = clazz.getDeclaredFields();
            try {
                E resultObject = (E) clazz.getDeclaredConstructor().newInstance();
                setupResultObject(clazz, tuple, classFields, resultObject);
                return resultObject;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> getDtoList(String sqlQuery, Class<?> clazz) {
        try (Session session = sessionFactory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            TypedQuery<Tuple> nativeQuery = (TypedQuery<Tuple>) (entityManager).createNativeQuery(sqlQuery, Tuple.class);
            List<Tuple> tupleList = nativeQuery.getResultList();
            List<E> result = new ArrayList<>();
            Field[] classFields = clazz.getDeclaredFields();
            for (Tuple tuple : tupleList) {
                Object resultObject;
                try {
                    resultObject = clazz.getDeclaredConstructor().newInstance();
                    setupResultObject(clazz, tuple, classFields, resultObject);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                result.add((E) resultObject);
            }
            return result;
        }
    }

    private void setupResultObject(Class<?> clazz, Tuple tuple, Field[] classFields, Object resultObject) {
        Arrays.stream(classFields)
                .filter(field -> isAliasContains(tuple.getElements(), field))
                .forEach(field -> {
                    setValue(clazz, tuple, field, resultObject);
                });
    }

    private void setValue(Class<?> clazz, Tuple tuple, Field field, Object resultObject) {
        String setterMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        Method setterMethod = null;
        try {
            setterMethod = clazz.getMethod(setterMethodName, field.getType());
            setterMethod.invoke(resultObject, tuple.get(field.getName()));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private boolean isAliasContains(List<TupleElement<?>> tupleElementList, Field field) {
        return tupleElementList.stream()
                        .anyMatch(tupleElement -> tupleElement.getAlias().equalsIgnoreCase(field.getName()));
    }
}
