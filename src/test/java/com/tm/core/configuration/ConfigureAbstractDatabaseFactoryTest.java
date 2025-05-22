package com.tm.core.configuration;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import com.tm.core.configuration.entityManager.IEntityManagerFactoryManager;
import jakarta.persistence.EntityManagerFactory;

public class ConfigureAbstractDatabaseFactoryTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    public ConfigureAbstractDatabaseFactoryTest() {
    }

    protected static DatabaseTypeConfiguration getDatabaseTypeConfiguration() {
        DatabaseConfigurationAnnotationClass[] databaseConfigurationAnnotationClass =
                new DatabaseConfigurationAnnotationClass[] {
                        new DatabaseConfigurationAnnotationClass("hikari.hibernate.cfg.xml")
        };

        return new DatabaseTypeConfiguration(DatabaseType.WRITE, databaseConfigurationAnnotationClass);
    }
}
