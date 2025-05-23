package com.tm.core.configuration.cp;

import com.tm.core.util.setting.HikariSetting;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ConnectionPullHikariConfiguration extends AbstractConnectionPullConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionPullHikariConfiguration.class);

    @Override
    public SessionFactory createSessionFactoryByProperties(String filename) {
        LOGGER.info("call createSessionFactoryByProperties, filename: {}", filename);
        Properties properties = configurationFileProvider.loadHibernateConfigurationFile(filename);
        Properties settings = addParams(properties);
        return super.createSessionFactoryWithPropertiesConfiguration(settings);
    }

    @Override
    public SessionFactory createSessionFactoryByHibernateXML(String filename) {
        LOGGER.info("call createSessionFactoryByHibernateXML, file name = {}", filename);
        return super.createSessionFactoryWithXMLConfiguration(filename);
    }

    @Override
    public EntityManagerFactory createEntityManagerByHibernateXML(String filename) {
        LOGGER.info("call createEntityManagerByHibernateXML, file name = {}", filename);
        return super.createEntityManagerByXMLConfiguration(filename);
    }

    @Override
    public EntityManagerFactory createEntityManagerByProperties(String filename) {
        LOGGER.info("call createEntityManagerByProperties, filename: {}", filename);
        Properties properties = configurationFileProvider.loadHibernateConfigurationFile(filename);
        Properties settings = addParams(properties);
        return super.createEntityManagerByWithPropertiesConfiguration(settings);
    }

    private Properties addParams(Properties properties) {
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, properties.getProperty(Environment.DRIVER));
        settings.put(Environment.URL, properties.getProperty(Environment.URL));
        settings.put(Environment.USER, properties.getProperty(Environment.USER));
        settings.put(Environment.PASS, properties.getProperty(Environment.PASS));
        settings.put(Environment.DIALECT, properties.getProperty(Environment.DIALECT));

        settings.put(Environment.HBM2DDL_AUTO, properties.getProperty(Environment.HBM2DDL_AUTO));
        settings.put(Environment.AUTOCOMMIT, properties.getOrDefault(Environment.AUTOCOMMIT, "false"));

        // Maximum waiting time for a connection from the pool
        settings.put(HikariSetting.HIBERNATE_HIKARI_CONNECTION_TIMEOUT,
                properties.getOrDefault(HikariSetting.HIBERNATE_HIKARI_CONNECTION_TIMEOUT, "20000"));
        // Minimum number of ideal connections in the pool
        settings.put(HikariSetting.HIBERNATE_HIKARI_MINIMUM_IDLE,
                properties.getOrDefault(HikariSetting.HIBERNATE_HIKARI_MINIMUM_IDLE, "10"));
        // Maximum number of actual connection in the pool
        settings.put(HikariSetting.HIBERNATE_HIKARI_MAXIMUM_PULL_SIZE,
                properties.getOrDefault(HikariSetting.HIBERNATE_HIKARI_MAXIMUM_PULL_SIZE, "20"));
        settings.put(Environment.CONNECTION_PROVIDER,
                properties.getOrDefault(Environment.CONNECTION_PROVIDER, HikariCPConnectionProvider.class));

        // Maximum time that a connection is allowed to sit ideal in the pool
        settings.put(HikariSetting.HIBERNATE_HIKARI_IDLE_TIMEOUT,
                properties.getOrDefault(HikariSetting.HIBERNATE_HIKARI_IDLE_TIMEOUT, "300000"));

        return settings;
    }

}
