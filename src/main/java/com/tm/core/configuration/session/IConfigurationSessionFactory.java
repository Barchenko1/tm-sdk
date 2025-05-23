package com.tm.core.configuration.session;

import org.hibernate.SessionFactory;

public interface IConfigurationSessionFactory {

    SessionFactory configureSessionFactory();
}
