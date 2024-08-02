package com.tm.core.processor;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ThreadLocalSessionManager {

    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();
    private final SessionFactory sessionFactory;

    public ThreadLocalSessionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        Session session = threadLocalSession.get();
        if (session == null) {
            session = sessionFactory.openSession();
            threadLocalSession.set(session);
        }
        return session;
    }

    public void closeSession() {
        Session session = threadLocalSession.get();
        if (session != null) {
            session.close();
            threadLocalSession.remove();
        }
    }
}