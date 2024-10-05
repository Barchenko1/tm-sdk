package com.tm.core.configuration.cp;

import com.tm.core.util.properties.ConfigurationFileProvider;
import com.tm.core.util.properties.IConfigurationFileProvider;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public abstract class AbstractConnectionPullConfiguration implements IConnectionPullConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConnectionPullConfiguration.class);

    protected IConfigurationFileProvider configurationFileProvider = new ConfigurationFileProvider();
    protected Class<?>[] annotatedClasses;

    @Override
    public void setAnnotatedClasses(Class<?>[] annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    protected SessionFactory createSessionFactoryWithXMLConfiguration(String fileName) {
        synchronized (AbstractConnectionPullConfiguration.class) {
            try {
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .configure(fileName)
                        .build();

                Metadata metadata = new MetadataSources(serviceRegistry)
                        .getMetadataBuilder()
                        .build();

                return metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                LOGGER.warn("SessionFactory creation failed: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    protected SessionFactory createSessionFactoryWithPropertiesConfiguration(Properties properties) {
        synchronized (AbstractConnectionPullConfiguration.class) {
            try (ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(properties)
                    .build()){

                Metadata metadata = new MetadataSources(serviceRegistry)
                        .addAnnotatedClasses(annotatedClasses)
                        .getMetadataBuilder()
                        .build();

                return metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                LOGGER.warn("SessionFactory creation failed: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

}
