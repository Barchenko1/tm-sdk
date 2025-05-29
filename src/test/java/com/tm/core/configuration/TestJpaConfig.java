package com.tm.core.configuration;

import com.tm.core.configuration.dbType.DatabaseConfigurationAnnotationClass;
import com.tm.core.configuration.dbType.DatabaseType;
import com.tm.core.configuration.dbType.DatabaseTypeConfiguration;
import com.tm.core.configuration.entityManager.EntityManagerFactoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;

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

    @Bean
    @Primary
    public EntityManagerFactory entityManagerFactory() {
        EntityManagerFactoryManager emfm = EntityManagerFactoryManager.getInstance(DATABASE_TYPE_CONFIGURATION);
        return emfm.getEntityManagerFactory(DatabaseType.WRITE, CONFIGURATION_FILE_NAME);
    }

    @Bean
    public EntityManager entityManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    @Bean(name = "jpaTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate(@Qualifier("jpaTransactionManager") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

}
