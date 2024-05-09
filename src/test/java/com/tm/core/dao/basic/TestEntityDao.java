package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class TestEntityDao extends AbstractDao implements ITestEntityDao {
    private static final Logger LOG = LoggerFactory.getLogger(TestEntityDao.class);

    public TestEntityDao(SessionFactory sessionFactory) {
        super(sessionFactory);
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
            LOG.warn("get entity error {}", e.getMessage());
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
            LOG.warn("get entity error {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return opt;
    }

    @Override
    public <E> Optional<E> getTestEntityByEmail(String email) {
        return Optional.empty();
    }

}
