package com.tm.core.dao.general;

import com.tm.core.modal.RelationshipNode;
import com.tm.core.modal.RelationshipEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class AbstractRelationshipEntityDao implements IRelationshipEntityDao {

    private static final Logger log = LoggerFactory.getLogger(AbstractRelationshipEntityDao.class);
    protected final SessionFactory sessionFactory;
    private final Executor executor;

    public AbstractRelationshipEntityDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.executor = Executors.newFixedThreadPool(4);
    }

    @Override
    public void saveRelationshipEntity(RelationshipEntity relationshipEntity) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<Integer> keyList = new ArrayList<>(relationshipEntity.getKeys());
            for (int key : keyList) {
                List<RelationshipNode> relationshipNodeList = relationshipEntity.getValues(key);
                relationshipNodeList.forEach(node -> {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                        session.persist(node.getEntity());
                    }, executor);
                    futures.add(completableFuture);
                });
            }
            // Wait for all async operations to complete
            CompletableFuture<Void> allOf =
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();  // Block until all futures are done

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
    public void updateRelationshipEntity(RelationshipEntity relationshipEntity) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<Integer> keyList = new ArrayList<>(relationshipEntity.getKeys());
            for (int key : keyList) {
                List<RelationshipNode> relationshipNodeList = relationshipEntity.getValues(key);
                relationshipNodeList.forEach(node -> {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {

                        session.merge(node.getEntity());
                    }, executor);
                    futures.add(completableFuture);
                });
            }
            // Wait for all async operations to complete
            CompletableFuture<Void> allOf =
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();  // Block until all futures are done

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
    public void deleteRelationshipEntity(RelationshipEntity relationshipEntity) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<Integer> keyList = new ArrayList<>(relationshipEntity.getKeys());
            for (int key : keyList) {
                List<RelationshipNode> relationshipNodeList = relationshipEntity.getValues(key);
                relationshipNodeList.forEach(node -> {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {

                        session.remove(node.getEntity());
                    }, executor);
                    futures.add(completableFuture);
                });
            }
            // Wait for all async operations to complete
            CompletableFuture<Void> allOf =
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();  // Block until all futures are done

            transaction.commit();
        } catch (Exception e) {
            log.warn("transaction error {}", e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
