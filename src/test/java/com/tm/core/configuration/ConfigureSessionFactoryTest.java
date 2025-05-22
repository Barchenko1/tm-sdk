package com.tm.core.configuration;

import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.factory.ISessionFactoryManager;
import com.tm.core.configuration.factory.SessionFactoryManager;
import org.hibernate.SessionFactory;

public class ConfigureSessionFactoryTest extends ConfigureAbstractDatabaseFactoryTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    public ConfigureSessionFactoryTest() {
    }

    public static SessionFactory getSessionFactory() {
        ISessionFactoryManager sessionFactoryManager = SessionFactoryManager.getInstance(getDatabaseTypeConfiguration());
        return sessionFactoryManager.getSessionFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
    }

}
