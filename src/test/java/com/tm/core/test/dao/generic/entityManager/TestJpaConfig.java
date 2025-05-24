package com.tm.core.test.dao.generic.entityManager;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import com.tm.core.process.dao.generic.IGenericDao;
import com.tm.core.process.dao.generic.entityManager.GenericEntityManagerDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static com.tm.core.configuration.dbType.DatabaseType.WRITE;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.tm.core")
public class TestJpaConfig {

    private static final String CONFIGURATION_FILE_NAME = "hikari.hibernate.cfg.xml";
    private static final DatabaseTypeConfiguration DATABASE_TYPE_CONFIGURATION = new DatabaseTypeConfiguration(
                DatabaseType.WRITE, new DatabaseConfigurationAnnotationClass[] {
                new DatabaseConfigurationAnnotationClass(CONFIGURATION_FILE_NAME)
            }
    );

    @Bean
    public DataSource dataSource() {
        return getHikariDataSource();
    }

//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        EntityManagerFactoryManager instance = EntityManagerFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
//        return instance.getEntityManagerFactory(WRITE, CONFIGURATION_FILE_NAME);
//    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("com.tm.core.modal.relationship");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties props = new Properties();
        // Database connection
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5438/tm_sdk_db");
        props.setProperty("hibernate.connection.username", "admin");
        props.setProperty("hibernate.connection.password", "secret");

        // SQL output
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");

        // Schema generation
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        // Dialect
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        // Connection pooling via HikariCP
        props.setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        props.setProperty("hibernate.hikari.connectionTimeout", "1000");
        props.setProperty("hibernate.hikari.minimumIdle", "10");
        props.setProperty("hibernate.hikari.maximumPoolSize", "50");
        props.setProperty("hibernate.hikari.idleTimeout", "300000");

        // Isolation level (TRANSACTION_READ_COMMITTED)
        props.setProperty("hibernate.connection.isolation", "2");

        // Optional: disable auto-commit
        props.setProperty("hibernate.connection.autocommit", "false");

        factory.setJpaProperties(props);

        return factory;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        SharedEntityManagerBean proxy = new SharedEntityManagerBean();
        proxy.setEntityManagerFactory(entityManagerFactory);
        proxy.afterPropertiesSet(); // initialize
        return proxy.getObject();   // this EntityManager is transaction-aware

//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        return entityManager;
    }

    @Bean
    public IGenericDao genericDao(EntityManager entityManager) {
        return new GenericEntityManagerDao(entityManager, "com.tm.core.modal.relationship");
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        var txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
