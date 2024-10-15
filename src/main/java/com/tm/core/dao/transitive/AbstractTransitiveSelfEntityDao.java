package com.tm.core.dao.transitive;

import com.tm.core.dao.AbstractEntityDao;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import com.tm.core.util.TransitiveSelfEnum;
import com.tm.core.util.helper.EntityFieldHelper;
import com.tm.core.util.helper.IEntityFieldHelper;
import jakarta.persistence.NoResultException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTransitiveSelfEntityDao extends AbstractEntityDao implements ITransitiveSelfEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransitiveSelfEntityDao.class);
    protected final SessionFactory sessionFactory;
    protected final IThreadLocalSessionManager sessionManager;
    protected final IEntityIdentifierDao entityIdentifierDao;
    protected final IEntityFieldHelper entityFieldHelper;

    protected AbstractTransitiveSelfEntityDao(SessionFactory sessionFactory,
                                              IEntityIdentifierDao entityIdentifierDao,
                                              Class<?> clazz) {
        super(clazz);
        this.sessionFactory = sessionFactory;
        this.sessionManager = new ThreadLocalSessionManager(sessionFactory);
        this.entityIdentifierDao = entityIdentifierDao;
        this.entityFieldHelper = new EntityFieldHelper();
    }

    @Override
    public <E extends TransitiveSelfEntity> void saveEntityTree(E entity) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Optional.ofNullable(entity.getParent()).ifPresent(session::persist);
            Optional.of(entity).ifPresent(session::persist);
            Optional.ofNullable(entity.getChildNodeList()).ifPresent(list -> list.forEach(session::persist));

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
    public <E extends TransitiveSelfEntity> void updateEntityTree(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try {
            Session session = sessionManager.getSession();
            transaction = session.beginTransaction();

            E oldEntity = entityIdentifierDao.getEntity(this.clazz, parameters);

            Optional<List<E>> optionalDbChildList = Optional.ofNullable(oldEntity.getChildNodeList());
            Optional<E> optionalDbRoot = Optional.of(oldEntity);
            Optional<E> optionalDbParent = Optional.ofNullable(oldEntity.getParent());

            Optional<List<E>> optionalChildList = Optional.ofNullable(entity.getChildNodeList());
            Optional<E> optionalRoot = Optional.of(entity);
            Optional<E> optionalParent = Optional.ofNullable(entity.getParent());

            Optional.ofNullable(entity.getChildNodeList())
                    .flatMap(childList -> Optional.ofNullable(oldEntity.getChildNodeList()))
                    .ifPresent(oldChildList -> oldChildList.forEach(session::remove));
            Optional.of(oldEntity).ifPresent(session::remove);
            Optional.ofNullable(entity.getParent()).ifPresent(parent -> {
                Optional.ofNullable(oldEntity.getParent()).ifPresent(session::remove);
                session.flush();
                session.clear();
                session.persist(parent);
            });
            if (optionalDbParent.isPresent() && optionalParent.isEmpty()) {
                entity.setParent(optionalDbParent.get());
                session.flush();
                session.clear();
            }
            Optional.of(entity).ifPresent(root -> {
                session.persist(root);
            });
            if(optionalDbChildList.isPresent() && optionalChildList.isEmpty()) {
                optionalDbChildList.get()
                        .forEach(oldChild-> {
                            entityFieldHelper.setId(oldChild, null);
                            oldChild.setParent(entity);
                            session.persist(oldChild);
                        });
            }
            Optional.ofNullable(entity.getChildNodeList()).ifPresent(childList -> {
                childList.forEach(session::persist);
            });
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
    public <E extends TransitiveSelfEntity> void deleteEntityTree(Parameter... parameters) {
        Transaction transaction = null;
        try {
            Session session = sessionManager.getSession();
            transaction = session.beginTransaction();
            E entity = entityIdentifierDao.getEntity(this.clazz, parameters);

            Optional.ofNullable(entity.getChildNodeList()).ifPresent(list -> list.forEach(session::remove));
            Optional.of(entity).ifPresent(session::remove);

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
    public <E extends TransitiveSelfEntity> List<E> getTransitiveSelfEntityList(Parameter... parameters) {
        try {
            List<E> resultEntityList = entityIdentifierDao.getEntityList(this.clazz, parameters);

            if (!resultEntityList.isEmpty()) {
                resultEntityList.forEach(resultEntity -> {
                    Hibernate.initialize(resultEntity.getParent());
                    Hibernate.initialize(resultEntity.getChildNodeList());
                });
            }
            return resultEntityList;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        } finally {
            sessionManager.closeSession();
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> E getTransitiveSelfEntity(Parameter... parameters) {
        try {
            E resultEntity = entityIdentifierDao.getEntity(this.clazz, parameters);
            if (resultEntity != null) {
                Hibernate.initialize(resultEntity.getParent());
                Hibernate.initialize(resultEntity.getChildNodeList());
            }
            return resultEntity;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> Optional<E> getOptionalTransitiveSelfEntity(Parameter... parameters) {
        try {
            Optional<E> optionalEntity = entityIdentifierDao.getOptionalEntity(this.clazz, parameters);
            optionalEntity.ifPresent(e -> {
                Hibernate.initialize(e.getParent());
                Hibernate.initialize(e.getChildNodeList());
            });
            return optionalEntity;
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> List<E> getTransitiveSelfEntityList(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getEntityList(clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> E getTransitiveSelfEntity(Class<?> clazz, Parameter... parameters) {
        try {
            E entity =  entityIdentifierDao.getEntity(clazz, parameters);
            sessionManager.closeSession();
            return entity;
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E> Optional<E> getOptionalTransitiveSelfEntity(Class<?> clazz, Parameter... parameters) {
        try {
            return entityIdentifierDao.getOptionalEntity(clazz, parameters);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> Map<TransitiveSelfEnum, List<E>> getTransitiveSelfEntitiesTree() {
        List<E> transitiveSelfEntityList = entityIdentifierDao.getEntityList(this.clazz);
        List<E> result = new ArrayList<>();
        for (Object obj : transitiveSelfEntityList) {
            if (obj instanceof List) {
                result.addAll((Collection<? extends E>) obj);
            } else if (obj instanceof TransitiveSelfEntity) {
                result.add((E) obj);
            }
        }
        return result.stream()
                .peek(transitiveSelfEntity -> {
                    Hibernate.initialize(transitiveSelfEntity.getParent());
                    Hibernate.initialize(transitiveSelfEntity.getChildNodeList());
                })
                .collect(Collectors.groupingBy(transitiveSelfEntity -> {
                    if (transitiveSelfEntity.getParent() == null) {
                        return TransitiveSelfEnum.PARENT;
                    } else if (transitiveSelfEntity.getParent() != null && transitiveSelfEntity.getChildNodeList().isEmpty()) {
                        return TransitiveSelfEnum.CHILD;
                    } else if (transitiveSelfEntity.getParent() != null && transitiveSelfEntity.getChildNodeList() != null){
                        return TransitiveSelfEnum.ROOT;
                    } else throw new RuntimeException();
                }));
    }

}
