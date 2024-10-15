package com.tm.core.test.thread;

import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThreadLocalSessionManagerTest {

    @Mock
    private SessionFactory sessionFactoryMock;
    @Mock
    private Session sessionMock;
    @InjectMocks
    private ThreadLocalSessionManager threadLocalSessionManager;

    @BeforeEach
    void setUp() {
        threadLocalSessionManager = new ThreadLocalSessionManager(sessionFactoryMock);
        try {
            Field threadLocalField = ThreadLocalSessionManager.class.getDeclaredField("threadLocalSession");
            threadLocalField.setAccessible(true);

            ThreadLocal<?> threadLocalSession = (ThreadLocal<?>) threadLocalField.get(threadLocalSessionManager);

            threadLocalSession.remove();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSession_shouldOpenNewSessionWhenNoneExists() {
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);

        Session session = threadLocalSessionManager.getSession();

        assertEquals(sessionMock, session);
        verify(sessionFactoryMock, times(1)).openSession();
    }

    @Test
    void closeSession_shouldDoNothingIfNoSessionExists() {
        IThreadLocalSessionManager threadLocalSessionManager
                = new ThreadLocalSessionManager(sessionFactoryMock);
        threadLocalSessionManager.closeSession();

        verify(sessionMock, never()).close();
    }

    @Test
    void cleanThreadLocal_shouldRemoveSessionFromThreadLocal() {
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
        threadLocalSessionManager.getSession();

        threadLocalSessionManager.cleanThreadLocal();

        threadLocalSessionManager.getSession();
        verify(sessionFactoryMock, times(2)).openSession();
    }
}
