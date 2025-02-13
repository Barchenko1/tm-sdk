package com.tm.core.process.dao.transitive;

import com.tm.core.process.dao.AbstractEntityChecker;
import com.tm.core.process.dao.identifier.IQueryService;
import com.tm.core.modal.TransitiveSelfEntity;
import com.tm.core.finder.parameter.Parameter;
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

public abstract class AbstractTransitiveSelfEntityDao extends AbstractEntityChecker implements ITransitiveSelfEntityDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransitiveSelfEntityDao.class);
    protected final SessionFactory sessionFactory;
    protected final IQueryService queryService;
    protected final IEntityFieldHelper entityFieldHelper;

    protected AbstractTransitiveSelfEntityDao(SessionFactory sessionFactory,
                                              IQueryService queryService,
                                              Class<?> clazz) {
        super(clazz);
        this.sessionFactory = sessionFactory;
        this.queryService = queryService;
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
    public <E extends TransitiveSelfEntity> void updateEntityTreeOldMain(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E oldEntity = (E) this.queryService.getEntity(session, this.clazz, parameters);
            Optional<List<E>> optionalOldChildList = Optional.ofNullable(oldEntity.getChildNodeList());
            Optional<E> optionalOldRoot = Optional.of(oldEntity);
            Optional<E> optionalOldParent = Optional.ofNullable(oldEntity.getParent());

            Optional<List<E>> optionalChildList = Optional.ofNullable(entity.getChildNodeList());
            Optional<E> optionalRoot = Optional.of(entity);
            Optional<E> optionalParent = Optional.ofNullable(entity.getParent());

            optionalChildList
                    .flatMap(childList -> Optional.ofNullable(oldEntity.getChildNodeList()))
                    .ifPresent(oldChildList ->
                            oldChildList.forEach(session::remove));
            optionalOldRoot.ifPresent(oldRoot -> {
                oldRoot.setChildNodeList(new ArrayList<>());
                oldRoot.setParent(null);
                session.remove(oldRoot);
            });
            optionalOldRoot.ifPresent(session::remove);
            optionalOldParent.ifPresent(oldParent -> {
                Optional.of(oldParent).ifPresent(session::remove);
                session.flush();
                session.clear();
                oldParent.setChildNodeList(new ArrayList<>());
            });
            if (optionalOldChildList.isPresent()
                    && !optionalOldChildList.get().isEmpty()
                    && optionalChildList.isPresent()
                    && optionalChildList.get().isEmpty()) {
                optionalOldChildList.get().forEach(oldChild -> {
                    oldChild.setParent(null);
                    entity.addChildTransitiveEntity(oldChild);
                });
            }
            if (optionalParent.isPresent()) {
                TransitiveSelfEntity parent = optionalParent.get();
                session.merge(parent);
            }
            if (optionalOldParent.isPresent() && optionalParent.isEmpty()) {
                TransitiveSelfEntity oldParent = optionalOldParent.get();
                oldParent.addChildTransitiveEntity(entity);
                session.merge(oldParent);
            }
            if (optionalOldParent.isEmpty() && optionalParent.isEmpty()) {
                session.merge(entity);
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
    public <E extends TransitiveSelfEntity> void updateEntityTreeNewMain(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            // requirements
            // need update only entity by param
            // if entity update have null parent old parent is main
            // if entity update have null child old child is main
            // if entity parent or child not null, update only entity, parent and child will be old
            E oldEntity = (E) this.queryService.getEntity(session, this.clazz, parameters);

            Optional<List<E>> optionalOldChildList = Optional.ofNullable(oldEntity.getChildNodeList());
            Optional<E> optionalOldRoot = Optional.of(oldEntity);
            Optional<E> optionalOldParent = Optional.ofNullable(oldEntity.getParent());

            Optional<List<E>> optionalChildList = Optional.ofNullable(entity.getChildNodeList());
            Optional<E> optionalRoot = Optional.of(entity);
            Optional<E> optionalParent = Optional.ofNullable(entity.getParent());

            optionalOldChildList.ifPresent(oldChildList -> {
                oldChildList.forEach(session::remove);
            });
            optionalOldRoot.ifPresent(session::remove);
            optionalOldParent.ifPresent(session::remove);
            session.flush();
            session.clear();
            optionalOldParent.ifPresent(parent -> parent.addChildTransitiveEntity(entity));
            optionalChildList.ifPresent(oldChildList ->
                    oldChildList.forEach(entity::addChildTransitiveEntity));
            session.merge(entity.getParent());

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
    public <E extends TransitiveSelfEntity> void deleteEntityTree(Parameter... parameters) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            E entity = (E) this.queryService.getEntity(session, this.clazz, parameters);

            Optional.ofNullable(entity.getChildNodeList()).ifPresent(list -> list.forEach(session::remove));
            Optional.of(entity).ifPresent(session::remove);

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
    public <E extends TransitiveSelfEntity> void addEntityToChildList(E entity, Parameter... parameters) {
        classTypeChecker(entity);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            E oldEntity = (E) this.queryService.getEntity(session, this.clazz, parameters);

            Optional<List<E>> optionalOldChildList = Optional.ofNullable(oldEntity.getChildNodeList());
            Optional<E> optionalOldParent = Optional.ofNullable(oldEntity.getParent());

            optionalOldParent.ifPresent(session::remove);
            session.flush();
            session.clear();
            optionalOldParent.ifPresent(oldParent -> {
                oldParent.setChildNodeList(new ArrayList<>());
                oldParent.setParent(null);
                oldParent.addChildTransitiveEntity(oldEntity);
            });
            optionalOldChildList.ifPresent(oldChildList -> oldChildList.forEach(oldChild -> {
                oldChild.setChildNodeList(new ArrayList<>());
            }));
            oldEntity.addChildTransitiveEntity(entity);
            session.merge(oldEntity.getParent());
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
    public <E extends TransitiveSelfEntity> List<E> getTransitiveSelfEntityList(Parameter... parameters) {
        try {
            List<E> resultEntityList = (List<E>) this.queryService.getEntityList(sessionFactory.openSession(), this.clazz, parameters);

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
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> E getTransitiveSelfEntity(Parameter... parameters) {
        try {
            return (E) this.queryService.getEntity(sessionFactory.openSession(), this.clazz, parameters);
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> Optional<E> getOptionalTransitiveSelfEntity(Parameter... parameters) {
        try {
            return (Optional<E>) this.queryService.getOptionalEntity(sessionFactory.openSession(), this.clazz, parameters);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.warn("get entity error {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public <E extends TransitiveSelfEntity> Map<TransitiveSelfEnum, List<E>> getTransitiveSelfEntitiesTree() {
        try (Session session = sessionFactory.openSession()) {
            List<E> transitiveSelfEntityList = (List<E>) this.queryService.getEntityList(session, this.clazz);
            List<E> result = new ArrayList<>();
            for (Object obj : transitiveSelfEntityList) {
                if (obj instanceof List) {
                    result.addAll((Collection<? extends E>) obj);
                } else if (obj instanceof TransitiveSelfEntity) {
                    result.add((E) obj);
                }
            }
            return result.stream()
                    .collect(Collectors.groupingBy(transitiveSelfEntity -> {
                        if (transitiveSelfEntity.getParent() == null) {
                            return TransitiveSelfEnum.PARENT;
                        } else if (transitiveSelfEntity.getParent() != null && transitiveSelfEntity.getChildNodeList().isEmpty()) {
                            return TransitiveSelfEnum.CHILD;
                        } else if (transitiveSelfEntity.getParent() != null && transitiveSelfEntity.getChildNodeList() != null) {
                            return TransitiveSelfEnum.ROOT;
                        } else throw new RuntimeException();
                    }));
        }
    }

}
