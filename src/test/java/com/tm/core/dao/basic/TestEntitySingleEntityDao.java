package com.tm.core.dao.basic;

import com.tm.core.dao.single.AbstractSingleEntityDao;
import com.tm.core.processor.ThreadLocalSessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TestEntitySingleEntityDao extends AbstractSingleEntityDao implements ITestEntityDao {
    private static final Logger log = LoggerFactory.getLogger(TestEntitySingleEntityDao.class);

    public TestEntitySingleEntityDao(SessionFactory sessionFactory, Class<?> clazz) {
        super(sessionFactory, clazz);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> getAllTestEntities() {
        List<E> users;
        try (Session session = sessionFactory.openSession()) {
            users = (List<E>) session.createNamedQuery("getTestEntityAll", clazz)
                    .list();
        }
        return users;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Optional<E> getTestEntityById(long id) {
        Optional<E> opt;
        try (Session session = sessionFactory.openSession()) {
            opt = (Optional<E>) Optional.ofNullable(session
                    .createNamedQuery("getTestEntityById", clazz)
                    .setParameter(1, id)
                    .getSingleResult());
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return opt;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Optional<E> getTestEntityByUser(String name) {
        Optional<E> opt;
        try (Session session = sessionFactory.openSession()) {
            opt = (Optional<E>) Optional.ofNullable(session
                    .createNamedQuery("getTestEntityByName", clazz)
                    .setParameter(1, name)
                    .getSingleResult());
        } catch (Exception e) {
            log.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return opt;
    }

    @Override
    public <E> Optional<E> getTestEntityByEmail(String email) {
        return Optional.empty();
    }

}
