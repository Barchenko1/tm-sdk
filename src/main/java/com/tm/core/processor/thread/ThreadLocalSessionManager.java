package com.tm.core.processor.thread;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class ThreadLocalSessionManager implements IThreadLocalSessionManager {

    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();
    private final SessionFactory sessionFactory;

    public ThreadLocalSessionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Session getSession() {
        Session session = threadLocalSession.get();
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
            threadLocalSession.set(session);
        }
        return session;
    }

    @Override
    public void closeSession() {
        Session session = threadLocalSession.get();
        if (session != null && session.isOpen()) {
            session.close();
            threadLocalSession.remove();
        }
    }

    public void cleanThreadLocal() {
        threadLocalSession.remove();
    }
}