package com.tm.core.test.config;

import com.tm.core.configuration.cp.ConnectionPullHikariConfiguration;
import com.tm.core.util.CoreConstants;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class HikariCPConfigurationXMLTest {

    @InjectMocks
    private ConnectionPullHikariConfiguration connectionPullHikariConfiguration;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testCreateSessionFactoryByHibernateXML() {
        SessionFactory sessionFactory =
                connectionPullHikariConfiguration.createSessionFactoryByHibernateXML(CoreConstants.HIKARI_HIBERNATE_XML_FILE_NAME);

        assertNotNull(sessionFactory);
    }
}
