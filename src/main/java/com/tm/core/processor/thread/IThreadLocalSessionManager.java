package com.tm.core.processor.thread;

import org.hibernate.Session;

public interface IThreadLocalSessionManager {
    Session getSession();
    void closeSession();
    void cleanThreadLocal();

}
