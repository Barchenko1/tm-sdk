package com.tm.core.configuration.manager;

import com.tm.core.configuration.factory.ConfigurationSessionFactory;
import com.tm.core.configuration.factory.IConfigurationSessionFactory;
import org.hibernate.SessionFactory;

import java.util.function.Supplier;

import static com.tm.core.configuration.manager.DatabaseType.COMMON;
import static com.tm.core.configuration.manager.DatabaseType.READ;
import static com.tm.core.configuration.manager.DatabaseType.WRITE;

public class SessionFactoryManager implements ISessionFactoryManager {

    private static SessionFactoryManager instance;
    private final SessionFactory readSessionFactory;
    private final SessionFactory writeSessionFactory;
    private final IConfigurationSessionFactory configurationReadSessionFactory;
    private final IConfigurationSessionFactory configurationWriteSessionFactory;

    private SessionFactoryManager(String configFileName) {
        this.configurationReadSessionFactory = null;
        this.configurationWriteSessionFactory = new ConfigurationSessionFactory(configFileName);
        this.readSessionFactory = null;
        this.writeSessionFactory = configurationWriteSessionFactory.configureSessionFactory();
    }

    private SessionFactoryManager(String configFileName, Class<?>[] annotationClasses) {
        this.configurationReadSessionFactory = null;
        this.configurationWriteSessionFactory = createSessionFactory(configFileName, annotationClasses);
        this.readSessionFactory = null;
        this.writeSessionFactory = configurationWriteSessionFactory.configureSessionFactory();
    }

    private SessionFactoryManager(String readConfigFileName, String writeConfigFileName) {
        this.configurationReadSessionFactory = new ConfigurationSessionFactory(readConfigFileName);
        this.configurationWriteSessionFactory = new ConfigurationSessionFactory(writeConfigFileName);
        this.readSessionFactory = configurationReadSessionFactory.configureSessionFactory();
        this.writeSessionFactory = configurationWriteSessionFactory.configureSessionFactory();
    }

    private SessionFactoryManager(String readConfigFileName, Class<?>[] readAnnotationClasses,
                                 String writeConfigFileName, Class<?>[] writeAnnotationClasses) {
        this.configurationReadSessionFactory = createSessionFactory(readConfigFileName, readAnnotationClasses);
        this.configurationWriteSessionFactory = createSessionFactory(writeConfigFileName, writeAnnotationClasses);
        this.readSessionFactory = configurationReadSessionFactory.configureSessionFactory();
        this.writeSessionFactory = configurationWriteSessionFactory.configureSessionFactory();
    }

    public static synchronized SessionFactoryManager getInstance(String configFileName) {
        if (instance == null) {
            instance = new SessionFactoryManager(configFileName);
        }
        return instance;
    }

    public static synchronized SessionFactoryManager getInstance(String configFileName, Class<?>[] annotationClasses) {
        if (instance == null) {
            instance = new SessionFactoryManager(configFileName, annotationClasses);
        }
        return instance;
    }

    public static synchronized SessionFactoryManager getInstance(String readConfigFileName, String writeConfigFileName) {
        if (instance == null) {
            instance = new SessionFactoryManager(readConfigFileName, writeConfigFileName);
        }
        return instance;
    }

    public static synchronized SessionFactoryManager getInstance(String readConfigFileName, Class<?>[] readAnnotationClasses,
                                                                 String writeConfigFileName, Class<?>[] writeAnnotationClasses) {
        if (instance == null) {
            instance = new SessionFactoryManager(readConfigFileName, readAnnotationClasses, writeConfigFileName, writeAnnotationClasses);
        }
        return instance;
    }

    @Override
    public Supplier<SessionFactory> getSessionFactorySupplier(DatabaseType databaseType) {
        if (READ.equals(databaseType)) {
            return () -> this.readSessionFactory;
        }
        if (WRITE.equals(databaseType) || COMMON.equals(databaseType)) {
            return () -> writeSessionFactory;
        }
        throw new IllegalArgumentException("Unsupported database type: " + databaseType);
    }

    private IConfigurationSessionFactory createSessionFactory(String configFileName, Class<?>[] annotationClasses) {
        if (annotationClasses != null) {
            return new ConfigurationSessionFactory(configFileName, annotationClasses);
        } else {
            return new ConfigurationSessionFactory(configFileName);
        }
    }

}
