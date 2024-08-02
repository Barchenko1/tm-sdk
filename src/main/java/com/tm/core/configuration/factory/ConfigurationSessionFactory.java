package com.tm.core.configuration.factory;

import com.tm.core.configuration.ConfigDbType;
import com.tm.core.configuration.cp.ConnectionPullHikariConfiguration;
import com.tm.core.configuration.cp.IConnectionPullConfiguration;
import com.tm.core.util.format.FileFormatter;
import com.tm.core.util.format.IFileFormatter;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationSessionFactory implements IConfigurationSessionFactory {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationSessionFactory.class);

    private final IFileFormatter fileFormatter = new FileFormatter();
    private final ConfigDbType configDbType;
    private SessionFactory sessionFactory;
    private Class<?>[] annotationClasses;
    private String fileName;

    public ConfigurationSessionFactory(ConfigDbType configDbType) {
        this.configDbType = configDbType;
    }

    public ConfigurationSessionFactory(String fileName) {
        this.configDbType = getFileType(fileName);
        this.fileName = fileName;
    }

    public ConfigurationSessionFactory(ConfigDbType configDbType, Class<?>[] annotationClasses) {
        this.configDbType = configDbType;
        this.annotationClasses = annotationClasses;
    }

    public ConfigurationSessionFactory(String fileName, Class<?>[] annotationClasses) {
        this.configDbType = getFileType(fileName);
        this.fileName = fileName;
        this.annotationClasses = annotationClasses;
    }

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            this.sessionFactory = configureSessionFactory();
        }
        return configureSessionFactory();
    }

    private SessionFactory configureSessionFactory() {
        IConnectionPullConfiguration connectionPullConfiguration = new ConnectionPullHikariConfiguration();
        return configDbSessionFactory(connectionPullConfiguration);
    }

    private ConfigDbType getFileType(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException();
        }

        if (fileName.endsWith(".xml")) {
            return ConfigDbType.XML;
        } else if (fileName.endsWith(".properties")) {
            return ConfigDbType.PROPERTIES;
        }
        throw new RuntimeException();
    }

    private SessionFactory configDbSessionFactory(IConnectionPullConfiguration connectionPullConfiguration) {
        SessionFactory sessionFactory = null;
        if (ConfigDbType.XML.equals(configDbType)) {
            sessionFactory =
                    connectionPullConfiguration.createSessionFactoryByHibernateXML();
        }
        if (ConfigDbType.PROPERTIES.equals(configDbType)) {
            connectionPullConfiguration.setAnnotatedClasses(annotationClasses);
            sessionFactory =
                    connectionPullConfiguration.createSessionFactoryByProperties();
        }


        if (sessionFactory == null) {
            log.warn("no configuration selected");
            throw new RuntimeException();
        }

        return sessionFactory;
    }
}
