package com.tm.core.configuration;

import com.tm.core.configuration.manager.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.manager.DatabaseType;
import com.tm.core.configuration.manager.DatabaseTypeConfiguration;
import com.tm.core.configuration.manager.ISessionFactoryManager;
import com.tm.core.configuration.manager.SessionFactoryManager;
import org.hibernate.SessionFactory;

public class ConfigureSessionFactoryTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    public ConfigureSessionFactoryTest() {
    }

    public static SessionFactory getSessionFactory() {
        DatabaseTypeConfiguration databaseTypeConfiguration = new DatabaseTypeConfiguration(
                DatabaseType.WRITE, new DatabaseConfigurationAnnotationClass[]{
                        new DatabaseConfigurationAnnotationClass(CONFIGURATION_FILE_NAME)
                }
        );
        ISessionFactoryManager sessionFactoryManager = SessionFactoryManager.getInstance(databaseTypeConfiguration);
        return sessionFactoryManager.getSessionFactorySupplier(DatabaseType.WRITE, CONFIGURATION_FILE_NAME).get();
    }
}
