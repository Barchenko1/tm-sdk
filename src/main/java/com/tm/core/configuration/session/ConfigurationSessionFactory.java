package com.tm.core.configuration.session;

import com.tm.core.configuration.ConfigDbType;
import com.tm.core.configuration.cp.ConnectionPullHikariConfiguration;
import com.tm.core.configuration.cp.IConnectionPullConfiguration;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tm.core.util.Util.getFileType;

public class ConfigurationSessionFactory implements IConfigurationSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationSessionFactory.class);

    private final IConnectionPullConfiguration connectionPullConfiguration = new ConnectionPullHikariConfiguration();
    private final String fileName;
    private final Class<?>[] annotationClasses;

    public ConfigurationSessionFactory(String fileName) {
        this.fileName = fileName;
        this.annotationClasses = null;
    }

    public ConfigurationSessionFactory(String fileName, Class<?>[] annotationClasses) {
        this.fileName = fileName;
        this.annotationClasses = annotationClasses;
    }

    @Override
    public SessionFactory configureSessionFactory() {
        LOGGER.info("Configuring session factory for file: {}", fileName);
        SessionFactory sessionFactory = null;
        ConfigDbType configDbType = getFileType(fileName);
        if (ConfigDbType.XML.equals(configDbType)) {
            sessionFactory = connectionPullConfiguration.createSessionFactoryByHibernateXML(fileName);
        }
        if (ConfigDbType.PROPERTIES.equals(configDbType)) {
            connectionPullConfiguration.setAnnotatedClasses(annotationClasses);
            sessionFactory = connectionPullConfiguration.createSessionFactoryByProperties(fileName);
        }

        return sessionFactory;
    }

}
