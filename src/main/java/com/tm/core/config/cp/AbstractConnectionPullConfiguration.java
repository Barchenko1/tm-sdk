package com.tm.core.config.cp;

import com.tm.core.properties.ConfigurationFileProvider;
import com.tm.core.properties.IConfigurationFileProvider;
import com.tm.core.util.setting.HikariSetting;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public abstract class AbstractConnectionPullConfiguration implements IConnectionPullConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnectionPullConfiguration.class);

    protected ServiceRegistry serviceRegistry;
    protected SessionFactory sessionFactory;
    protected IConfigurationFileProvider configurationFileProvider = new ConfigurationFileProvider();
    protected Class<?>[] annotatedClasses;

    @Override
    public void setAnnotatedClasses(Class<?>[] annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    protected SessionFactory createSessionFactoryWithXMLConfiguration(String fileName) {
        if (sessionFactory == null) {
            try {
                serviceRegistry = new StandardServiceRegistryBuilder()
                        .configure(fileName)
                        .build();

                Metadata metadata = new MetadataSources(serviceRegistry)
                        .getMetadataBuilder()
                        .build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();

            } catch (Exception e) {
                if (serviceRegistry != null) {
                    StandardServiceRegistryBuilder.destroy(serviceRegistry);
                }
                throw new RuntimeException(e);
            }
        }
        return sessionFactory;
    }

    protected SessionFactory createSessionFactoryWithPropertiesConfiguration(Properties properties) {
        if (sessionFactory == null) {
            try {
                serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(properties)
                        .build();

                Metadata metadata = new MetadataSources(serviceRegistry)
                        .addAnnotatedClasses(annotatedClasses)
                        .getMetadataBuilder()
                        .build();

                sessionFactory = metadata.getSessionFactoryBuilder().build();
            } catch (Exception e) {
                if (serviceRegistry != null) {
                    StandardServiceRegistryBuilder.destroy(serviceRegistry);
                }
                LOG.warn("properties error {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return sessionFactory;
    }

}
