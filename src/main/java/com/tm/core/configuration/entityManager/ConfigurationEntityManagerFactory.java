package com.tm.core.configuration.entityManager;

import com.tm.core.configuration.ConfigDbType;
import com.tm.core.configuration.cp.ConnectionPullHikariConfiguration;
import com.tm.core.configuration.cp.IConnectionPullConfiguration;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tm.core.util.Util.getFileType;

public class ConfigurationEntityManagerFactory implements IConfigurationEntityManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationEntityManagerFactory.class);

    private final IConnectionPullConfiguration connectionPullConfiguration = new ConnectionPullHikariConfiguration();
    private final String fileName;
    private final Class<?>[] annotationClasses;

    public ConfigurationEntityManagerFactory(String fileName) {
        this.fileName = fileName;
        this.annotationClasses = null;
    }

    public ConfigurationEntityManagerFactory(String fileName, Class<?>[] annotationClasses) {
        this.fileName = fileName;
        this.annotationClasses = annotationClasses;
    }

    @Override
    public EntityManagerFactory configureEntityManagerFactory() {
        LOGGER.info("Configuring entity manager factory for file: {}", fileName);
        EntityManagerFactory entityManagerFactory = null;
        ConfigDbType configDbType = getFileType(fileName);
        if (ConfigDbType.XML.equals(configDbType)) {
            entityManagerFactory = connectionPullConfiguration.createEntityManagerByHibernateXML(fileName);
        }
        if (ConfigDbType.PROPERTIES.equals(configDbType)) {
            connectionPullConfiguration.setAnnotatedClasses(annotationClasses);
            entityManagerFactory = connectionPullConfiguration.createEntityManagerByProperties(fileName);
        }
        if (entityManagerFactory == null) {
            throw new RuntimeException();
        }

        return entityManagerFactory;
    }

}
