package com.tm.core.configuration;

import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import com.tm.core.configuration.entityManager.IEntityManagerFactoryManager;
import com.tm.core.configuration.session.ISessionFactoryManager;
import com.tm.core.configuration.session.SessionFactoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;

public class ConfigureSessionFactoryTest extends ConfigureAbstractDatabaseFactoryTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    public ConfigureSessionFactoryTest() {
    }

    public static EntityManager getEntityManager() {
        IEntityManagerFactoryManager entityManagerFactoryManager = EntityManagerFactoryManager.getInstance(getDatabaseTypeConfiguration());
        EntityManagerFactory entityManagerFactory = entityManagerFactoryManager.getEntityManagerFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
        return entityManagerFactory.createEntityManager();
    }

    public static SessionFactory getSessionFactory() {
        ISessionFactoryManager sessionFactoryManager = SessionFactoryManager.getInstance(getDatabaseTypeConfiguration());
        return sessionFactoryManager.getSessionFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
    }

}
