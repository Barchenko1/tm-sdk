package com.tm.core.test.dao.generic.entityManager;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

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
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
//        factory.setDataSource(dataSource());
//        factory.setPackagesToScan("com.tm.core.modal.relationship");
//        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//
//        Properties props = new Properties();
//        // Database connection
//        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
//        props.setProperty("hibernate.connection.url", "jdbc:postgresql://127.0.0.1:5438/tm_sdk_db");
//        props.setProperty("hibernate.connection.username", "admin");
//        props.setProperty("hibernate.connection.password", "secret");
//
//        // SQL output
//        props.setProperty("hibernate.show_sql", "true");
//        props.setProperty("hibernate.format_sql", "true");
//
//        // Schema generation
//        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//
//        // Dialect
//        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//
//        // Connection pooling via HikariCP
//        props.setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
//        props.setProperty("hibernate.hikari.connectionTimeout", "1000");
//        props.setProperty("hibernate.hikari.minimumIdle", "10");
//        props.setProperty("hibernate.hikari.maximumPoolSize", "50");
//        props.setProperty("hibernate.hikari.idleTimeout", "300000");
//
//        // Transaction settings
//        props.setProperty("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
//        props.setProperty("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform");
//        props.setProperty("hibernate.connection.isolation", "2");
//        props.setProperty("hibernate.connection.autocommit", "false");
//        props.setProperty("hibernate.transaction.flush_before_completion", "true");
//        props.setProperty("hibernate.transaction.auto_close_session", "false");
//
//        factory.setJpaProperties(props);
//        return factory;
//    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        EntityManagerFactoryManager emfm = EntityManagerFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
        EntityManagerFactory emf = emfm.getEntityManagerFactory(WRITE, CONFIGURATION_FILE_NAME);
        return emf;
    }

    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
//        SharedEntityManagerBean sharedEntityManagerBean = new SharedEntityManagerBean();
//        sharedEntityManagerBean.setEntityManagerFactory(entityManagerFactory);
//        try {
//            sharedEntityManagerBean.afterPropertiesSet();
//            return sharedEntityManagerBean.getObject();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create shared EntityManager bean", e);
//        }
//
        return entityManagerFactory.createEntityManager();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
