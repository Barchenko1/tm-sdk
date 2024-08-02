package com.tm.core.dao.transitive;

import com.tm.core.dao.AbstractEntityDao;
import com.tm.core.processor.EntityFinder;
import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.util.TransitiveSelfEnum;
import jakarta.persistence.NoResultException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTransitiveSelfEntityDao extends AbstractEntityDao implements ITransitiveSelfEntityDao {
    private static final Logger log = LoggerFactory.getLogger(AbstractTransitiveSelfEntityDao.class);
    protected final SessionFactory sessionFactory;

    protected AbstractTransitiveSelfEntityDao(SessionFactory sessionFactory, Class<?> clazz) {
        super(clazz);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public <E extends TransitiveSelfEntity> void saveEntityTree(E entity) {
        classTypeChecker(entity);

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional.of(entity.getParent()).ifPresent(session::persist);
            Optional.of(entity).ifPresent(session::persist);
            Optional.ofNullable(entity.getChildNodeList()).ifPresent(list -> list.forEach(session::persist));

            transaction.commit();
        } catch (Exception e) {
            log.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> void updateEntityTree(EntityFinder entityFinder, E newEntity) {
        classTypeChecker(newEntity);

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<E> selectQuery = entityFinder.executeSelectQuery(session, clazz);
            E oldEntity = selectQuery.getSingleResult();

            Optional.ofNullable(oldEntity.getChildNodeList()).ifPresent(list -> list.forEach(session::remove));
            Optional.ofNullable(newEntity.getChildNodeList()).ifPresent(list -> list.forEach(session::persist));

            Optional.of(oldEntity).ifPresent(session::remove);
            Optional.of(newEntity).ifPresent(session::persist);

            Optional.ofNullable(oldEntity.getParent()).ifPresent(session::remove);
            Optional.ofNullable(newEntity.getParent()).ifPresent(session::persist);

            transaction.commit();
        } catch (Exception e) {
            log.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> void deleteEntityTree(EntityFinder entityFinder) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<E> selectQuery = entityFinder.executeSelectQuery(session, clazz);
            E entity = selectQuery.getSingleResult();

            Optional.ofNullable(entity.getChildNodeList()).ifPresent(list -> list.forEach(session::remove));
            Optional.of(entity).ifPresent(session::remove);

            transaction.commit();
        } catch (Exception e) {
            log.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> E getEntityBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            E resultEntity = (E) session.createNativeQuery(sqlQuery, clazz)
                    .getSingleResult();

            if (resultEntity != null) {
                Hibernate.initialize(resultEntity.getParent());
                Hibernate.initialize(resultEntity.getChildNodeList());
            }
            return resultEntity;
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> Optional<E> getOptionalEntityBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            Optional<E> optionalEntity =  (Optional<E>) session.createNativeQuery(sqlQuery, clazz)
                    .uniqueResultOptional();

            optionalEntity.ifPresent(e -> {
                Hibernate.initialize(e.getParent());
                Hibernate.initialize(e.getChildNodeList());
            });
            return optionalEntity;
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> List<E> getEntityListBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            List<E> resultEntityList = (List<E>) session.createNativeQuery(sqlQuery, clazz)
                    .list();

            if (!resultEntityList.isEmpty()) {
                resultEntityList.forEach(resultEntity -> {
                    Hibernate.initialize(resultEntity.getParent());
                    Hibernate.initialize(resultEntity.getChildNodeList());
                });
            }
            return resultEntityList;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> List<E> getEntityListBySQLQueryWithParams(String sqlQuery, Object... params) {
        try (Session session = sessionFactory.openSession()) {
            Query<E> query = (Query<E>) session.createNativeQuery(sqlQuery, clazz);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            List<E> resultEntityList = query.list();

            if (!resultEntityList.isEmpty()) {
                resultEntityList.forEach(resultEntity -> {
                    Hibernate.initialize(resultEntity.getParent());
                    Hibernate.initialize(resultEntity.getChildNodeList());
                });
            }
            return resultEntityList;
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> E getEntityBySQLQueryWithParams(String sqlQuery, Object... params) {
        try (Session session = sessionFactory.openSession()) {
            Query<E> query = (Query<E>) session.createNativeQuery(sqlQuery, clazz);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            E resultEntity = query.getSingleResult();
            if (resultEntity != null) {
                Hibernate.initialize(resultEntity.getParent());
                Hibernate.initialize(resultEntity.getChildNodeList());
            }
            return resultEntity;
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> Optional<E> getOptionalEntityBySQLQueryWithParams(String sqlQuery, Object... params) {
        try (Session session = sessionFactory.openSession()) {
            Query<E> query = (Query<E>) session.createNativeQuery(sqlQuery, clazz);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            Optional<E> optionalEntity = query.uniqueResultOptional();
            optionalEntity.ifPresent(e -> {
                Hibernate.initialize(e.getParent());
                Hibernate.initialize(e.getChildNodeList());
            });
            return optionalEntity;
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends TransitiveSelfEntity> Map<TransitiveSelfEnum, List<E>> getEntitiesTreeBySQLQuery(String sqlQuery) {
        try (Session session = sessionFactory.openSession()) {
            List<E> categoryList = (List<E>) session.createNativeQuery(sqlQuery, clazz)
                    .list();

            return categoryList.stream()
                    .peek(category -> {
                        Hibernate.initialize(category.getParent());
                        Hibernate.initialize(category.getChildNodeList());
                    })
                    .collect(Collectors.groupingBy(category -> {
                        if (category.getParent() == null) {
                            return TransitiveSelfEnum.PARENT;
                        } else if (category.getParent() != null && category.getChildNodeList().isEmpty()) {
                            return TransitiveSelfEnum.BASE;
                        } else {
                            return TransitiveSelfEnum.MIDDLE;
                        }
                    }));
        }
    }

    private <E extends TransitiveSelfEntity> void deleteEntityFromMap(E entity, Map<String, E> map) {
        map.remove(entity.getRootField());
        if (entity.getChildNodeList() != null) {
            entity.getChildNodeList().forEach(childNode ->
                    map.remove(childNode.getRootField()));
        }
    }

    private <E extends TransitiveSelfEntity> void addNewEntityTreeToMap(E entity, Map<String, E> map) {
        if (entity.getParent() != null) {
            map.putIfAbsent(entity.getParent().getRootField(), entity.getParent());
        }
        map.put(entity.getRootField(), entity);
        if (entity.getChildNodeList() != null) {
            entity.getChildNodeList().forEach(childNode ->
                    map.put(childNode.getRootField(), (E) childNode));
        }
    }

    private <E extends TransitiveSelfEntity> void updateEntityTreeMap(Map<String, E> map, E oldEntity, E newEntity) {
        if (oldEntity.getParent() != null) {
            map.remove(oldEntity.getParent().getRootField());
            map.put(newEntity.getParent().getRootField(), newEntity.getParent());
        }

        map.remove(oldEntity.getRootField());
        map.put(newEntity.getRootField(), newEntity);

        List<E> oldChildList = oldEntity.getChildNodeList();
        List<E> newChildList = newEntity.getChildNodeList();
        if (oldEntity.getChildNodeList() != null) {
            for (int i = 0; i < oldChildList.size(); i++) {
                map.remove(oldChildList.get(i).getRootField());
                map.put(newChildList.get(i).getRootField(), (E) newEntity.getChildNodeList().get(i));
            }
        }
    }

}
