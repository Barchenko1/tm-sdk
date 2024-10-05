package com.tm.core.configuration;

import com.tm.core.configuration.manager.DatabaseType;
import com.tm.core.configuration.manager.ISessionFactoryManager;
import com.tm.core.configuration.manager.SessionFactoryManager;
import org.hibernate.SessionFactory;

public class ConfigureSessionFactoryTest {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";

    public ConfigureSessionFactoryTest() {
    }

    public static SessionFactory getSessionFactory() {
        ISessionFactoryManager sessionFactoryManager = SessionFactoryManager.getInstance(CONFIGURATION_FILE_NAME);
        return sessionFactoryManager.getSessionFactorySupplier(DatabaseType.WRITE).get();
    }
}
