package com.tm.core.dao;

import com.tm.core.util.converter.ISqlParamsConverter;
import com.tm.core.util.converter.SqlParamsConverter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractDao {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);
    protected Class<?> clazz;
    protected final SessionFactory sessionFactory;
    protected final ISqlParamsConverter sqlParamsConverter;

    public AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.sqlParamsConverter = new SqlParamsConverter();
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public <E> void saveEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public <E> void updateEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> void updateEntityWithSQL(String sqlQuery, Object param) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            nativeQuery.setParameter(1, param);
            int result = nativeQuery.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


    public <E> void deleteEntity(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> void deleteEntityWithSQL(String sqlQuery, Object param) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            nativeQuery.setParameter(1, param);
            int result = nativeQuery.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getEntityListBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            return (List<E>) session
                    .createNativeQuery(sqlQuery, clazz)
                    .list();
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> E getEntityBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            return (E) session
                    .createNativeQuery(sqlQuery, clazz)
                    .getSingleResult();
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<E> getOptionEntityBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable((E) session
                    .createNativeQuery(sqlQuery, clazz)
                    .getSingleResult());
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getEntityListBySQLQueryWithParams(String sqlQuery, List<Object> params) {
            Map<Integer, Object> paramMap = sqlParamsConverter.getObjectParamsMap(params);
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return nativeQuery.list();
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<E> getOptionalEntityBySQLQueryWithParams(String sqlQuery, List<Object> params) {
        Map<Integer, Object> paramMap = sqlParamsConverter.getObjectParamsMap(params);
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return Optional.ofNullable(nativeQuery.getSingleResult());
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getEntityBySQLQueryWithStringParam(String sqlQuery, String param) {
        Map<Integer, String> paramMap = sqlParamsConverter.getObjectParamsMap(param);
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (Map.Entry<Integer, String> entry : paramMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return nativeQuery.list();
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<E> getOptionalEntityBySQLQueryWithStringParam(String sqlQuery, String param) {
        Map<Integer, String> paramMap = sqlParamsConverter.getObjectParamsMap(param);
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (Map.Entry<Integer, String> entry : paramMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return Optional.ofNullable(nativeQuery.getSingleResult());
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> List<E> getEntityBySQLQueryWithNumberParam(String sqlQuery, Number param) {
        Map<Integer, Number> paramMap = sqlParamsConverter.getObjectParamsMap(param);
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (Map.Entry<Integer, Number> entry : paramMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return nativeQuery.list();
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<E> getOptionalEntityBySQLQueryWithNumberParam(String sqlQuery, Number param) {
        Map<Integer, Number> paramMap = sqlParamsConverter.getObjectParamsMap(param);
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<E> nativeQuery = (NativeQuery<E>) session.createNativeQuery(sqlQuery, clazz);
            for (Map.Entry<Integer, Number> entry : paramMap.entrySet()) {
                nativeQuery.setParameter(entry.getKey(), entry.getValue());
            }
            return Optional.ofNullable(nativeQuery.getSingleResult());
        } catch (Exception e) {
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void updateEntityBySQLQueryWithParams(String sqlQuery, Object... params) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query query = session.createNamedQuery(sqlQuery, Void.class);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            LOG.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private <E> void classTypeChecker(E entity) {
        if (this.clazz != entity.getClass()) {
            throw new RuntimeException("Invalid entity type");
        }
    }
}
