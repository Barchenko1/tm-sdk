package com.tm.core.test.config;

import com.tm.core.configuration.cp.ConnectionPullHikariConfiguration;
import com.tm.core.util.properties.IConfigurationFileProvider;
import com.tm.core.util.CoreConstants;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HikariCPConfigurationPropertiesTest {

    @Mock
    private IConfigurationFileProvider configurationFileProvider;

    @InjectMocks
    private ConnectionPullHikariConfiguration connectionPullHikariConfiguration;

    private final Properties properties = new Properties();

    @BeforeEach
    public void setUp() {
        properties.setProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.setProperty("jakarta.persistence.jdbc.url", "jdbc:postgresql://127.0.0.1:5438/tm_sdk_db");
        properties.setProperty("jakarta.persistence.jdbc.user", "admin");
        properties.setProperty("jakarta.persistence.jdbc.password", "secret");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");


    }

    @Test
    public void testCreateSessionFactoryByProperties() {
        when(configurationFileProvider.loadHibernateConfigurationFile(CoreConstants.HIKARI_PROPERTIES_FILE_NAME))
                .thenReturn(properties);
        connectionPullHikariConfiguration.setAnnotatedClasses(new Class[]{
                Object.class
        });
        // Invoke the method to test
        SessionFactory sessionFactory =
                connectionPullHikariConfiguration.createSessionFactoryByProperties(CoreConstants.HIKARI_PROPERTIES_FILE_NAME);


        assertNotNull(sessionFactory);

        verify(configurationFileProvider, times(1))
                .loadHibernateConfigurationFile(CoreConstants.HIKARI_PROPERTIES_FILE_NAME);
    }

    @Test
    public void testCreateSessionFactoryWithProperties_ExceptionThrown() {
        doThrow(RuntimeException.class)
                .when(configurationFileProvider)
                .loadHibernateConfigurationFile(CoreConstants.HIKARI_PROPERTIES_FILE_NAME);

        assertThrows(RuntimeException.class,
                () -> connectionPullHikariConfiguration.createSessionFactoryByProperties(CoreConstants.HIKARI_PROPERTIES_FILE_NAME));
    }

}
