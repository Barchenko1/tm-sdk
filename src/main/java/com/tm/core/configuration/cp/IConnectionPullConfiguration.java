package com.tm.core.configuration.cp;

import org.hibernate.SessionFactory;

public interface IConnectionPullConfiguration {

    SessionFactory createSessionFactoryByProperties();
    SessionFactory createSessionFactoryByHibernateXML();
    void setAnnotatedClasses(Class<?>[] classes);
}
