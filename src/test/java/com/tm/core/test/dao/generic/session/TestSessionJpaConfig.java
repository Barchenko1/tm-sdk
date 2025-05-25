package com.tm.core.test.dao.generic.session;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import com.tm.core.configuration.session.ISessionFactoryManager;
import com.tm.core.configuration.session.SessionFactoryManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.tm.core")
public class TestSessionJpaConfig {

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

    @Bean
    @Primary
    public EntityManagerFactory entityManagerFactory() {
        EntityManagerFactoryManager emfm = EntityManagerFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
        return emfm.getEntityManagerFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
    }

    @Bean(name = "hibernateSessionFactory")
    public SessionFactory sessionFactory() {
        ISessionFactoryManager sessionFactoryManager = SessionFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
        return sessionFactoryManager.getSessionFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
    }

    @Bean(name = "hibernateTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("hibernateSessionFactory") SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(@Qualifier("hibernateTransactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
