package com.tm.core.dao.general;

import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.dao.transaction.ITransactionWrapper;
import com.tm.core.dao.transaction.TransactionWrapper;
import com.tm.core.modal.GeneralEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractGeneralEntityDao implements IGeneralEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeneralEntityDao.class);

    private final Class<?> clazz;
    protected final SessionFactory sessionFactory;
    protected final IThreadLocalSessionManager sessionManager;
    protected final IEntityFieldHelper entityFieldHelper;
    protected final IEntityIdentifierDao entityIdentifierDao;
    protected final ITransactionWrapper transactionWrapper;

    public AbstractGeneralEntityDao(SessionFactory sessionFactory,
                                    IEntityIdentifierDao entityIdentifierDao,
                                    Class<?> clazz) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
        this.entityFieldHelper = new EntityFieldHelper();
        this.entityIdentifierDao = entityIdentifierDao;
        this.transactionWrapper = new TransactionWrapper(sessionFactory);
    }

    @Override
    public void saveGeneralEntity(GeneralEntity generalEntity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<Integer> keyList = new ArrayList<>(generalEntity.getKeys());
            for (int key : keyList) {
                List<Object> objectList = generalEntity.getValues(key);
                objectList.forEach(session::persist);
            }
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public void saveGeneralEntity(Consumer<Session> consumer) {
        this.transactionWrapper.saveEntity(consumer);
    }

    @Override
    public <E> void updateGeneralEntity(Supplier<E> supplier) {
        this.transactionWrapper.updateEntity(supplier);
    }

    @Override
    public void updateGeneralEntity(Consumer<Session> consumer) {
        this.transactionWrapper.updateEntity(consumer);
    }

    @Override
    public void deleteGeneralEntity(GeneralEntity generalEntity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<Integer> keyList = new ArrayList<>(generalEntity.getKeys());
            for (int key : keyList) {
                List<Object> objectList = generalEntity.getValues(key);
                objectList.forEach(session::remove);
            }
            transaction.commit();
        } catch (Exception e) {
            LOGGER.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public void deleteGeneralEntity(Consumer<Session> consumer) {
        this.transactionWrapper.deleteEntity(consumer);
    }

    @Override
    public <E> void deleteGeneralEntity(Class<?> clazz, Parameter... parameters) {
        Transaction transaction = null;
        try {
            Session session = sessionManager.getSession();
            transaction = session.beginTransaction();
            List<E> entityList = entityIdentifierDao.getEntityList(clazz, parameters);
            entityList.forEach(session::remove);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.warn("transaction error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> void deleteGeneralEntity(Parameter... parameters) {
        Transaction transaction = null;
        try {
            Session session = sessionManager.getSession();
            transaction = session.beginTransaction();
            List<E> entityList = entityIdentifierDao.getEntityList(this.clazz, parameters);
            entityList.forEach(session::remove);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            LOGGER.warn("transaction error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> List<E> getGeneralEntityList(Class<?> clazz, Parameter... parameters) {
        try {
            List<E> resultList = entityIdentifierDao.getEntityList(clazz, parameters);
            resultList.forEach(this::initializeInnerEntities);
            return resultList;
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> E getGeneralEntity(Class<?> clazz, Parameter... parameters) {
        try {
            E entity = entityIdentifierDao.getEntity(clazz, parameters);
            initializeInnerEntities(entity);
            return entity;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Optional<E> getOptionalGeneralEntity(Class<?> clazz, Parameter... parameters) {
        try {
            Optional<E> optionalResult = entityIdentifierDao.getOptionalEntity(clazz, parameters);
            optionalResult.ifPresent(this::initializeInnerEntities);
            return optionalResult;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> List<E> getGeneralEntityList(Parameter... parameters) {
        try {
            List<E> resultList = entityIdentifierDao.getEntityList(clazz, parameters);
            resultList.forEach(this::initializeInnerEntities);
            return resultList;
        } catch (Exception e) {
            LOGGER.warn("get entity list error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> E getGeneralEntity(Parameter... parameters) {
        try {
            E entity = entityIdentifierDao.getEntity(this.clazz, parameters);
            initializeInnerEntities(entity);
            return entity;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E> Optional<E> getOptionalGeneralEntity(Parameter... parameters) {
        try {
            Optional<E> optionalResult = entityIdentifierDao.getOptionalEntity(this.clazz, parameters);
            optionalResult.ifPresent(this::initializeInnerEntities);
            return optionalResult;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
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

    private <E> void initializeInnerEntities(E entity) {
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
