package com.tm.core.configuration;

import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import com.tm.core.configuration.entityManager.IEntityManagerFactoryManager;
import jakarta.persistence.EntityManagerFactory;

public class ConfigureEntityManagerFactoryTest extends ConfigureAbstractDatabaseFactoryTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    public ConfigureEntityManagerFactoryTest() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        IEntityManagerFactoryManager entityManagerFactoryManager = EntityManagerFactoryManager.getInstance(getDatabaseTypeConfiguration());
        return entityManagerFactoryManager.getEntityManagerFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
    }

}
