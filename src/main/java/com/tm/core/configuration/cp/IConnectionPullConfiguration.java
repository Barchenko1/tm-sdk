package com.tm.core.configuration.cp;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;

public interface IConnectionPullConfiguration {

    SessionFactory createSessionFactoryByProperties(String filename);
    SessionFactory createSessionFactoryByHibernateXML(String filename);

    EntityManagerFactory createEntityManagerByHibernateXML(String filename);
    EntityManagerFactory createEntityManagerByProperties(String filename);
    void setAnnotatedClasses(Class<?>[] classes);
}
