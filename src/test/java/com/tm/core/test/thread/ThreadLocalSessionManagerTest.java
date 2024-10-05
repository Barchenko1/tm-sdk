package com.tm.core.test.thread;

import com.tm.core.dao.single.AbstractSingleEntityDao;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

            // Remove the value for the current thread
            threadLocalSession.remove();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSession_shouldReturnExistingSessionFromThreadLocal() {
        // Given: Session is already managed by the ThreadLocal (simulated via first call)
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
        // First call to getSession will open and set the session
        Session firstSession = threadLocalSessionManager.getSession();
        assertEquals(sessionMock, firstSession);

        // When: Calling getSession again
        Session session = threadLocalSessionManager.getSession();

        // Then: Should return the same session (thread-local behavior)
        assertEquals(firstSession, session);
        verify(sessionFactoryMock, times(1)).openSession(); // Only one open session call
    }

    @Test
    void getSession_shouldOpenNewSessionWhenNoneExists() {
        // Given: No session exists in ThreadLocal
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);

        // When: Calling getSession
        Session session = threadLocalSessionManager.getSession();

        // Then: A new session should be opened and returned
        assertEquals(sessionMock, session);
        verify(sessionFactoryMock, times(1)).openSession(); // Ensure openSession was called
    }

    @Test
    void closeSession_shouldCloseAndRemoveSessionFromThreadLocal() {
        // Given: A session exists in ThreadLocal
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
        threadLocalSessionManager.getSession();  // Set the session first

        // When: Calling closeSession
        threadLocalSessionManager.closeSession();

        // Then: Session should be closed and removed from ThreadLocal
        verify(sessionMock, times(1)).close();
        // The subsequent call should open a new session
        threadLocalSessionManager.getSession();
        verify(sessionFactoryMock, times(2)).openSession(); // Called again after close
    }

    @Test
    void closeSession_shouldDoNothingIfNoSessionExists() {
        // Given: No session in ThreadLocal
        IThreadLocalSessionManager threadLocalSessionManager
                = new ThreadLocalSessionManager(sessionFactoryMock);
        // When: Calling closeSession
        threadLocalSessionManager.closeSession();

        // Then: No session should be closed
        verify(sessionMock, never()).close(); // close should not be called
    }

    @Test
    void cleanThreadLocal_shouldRemoveSessionFromThreadLocal() {
        // Given: A session exists in ThreadLocal
        when(sessionFactoryMock.openSession()).thenReturn(sessionMock);
        threadLocalSessionManager.getSession();  // Set the session first

        // When: Calling cleanThreadLocal
        threadLocalSessionManager.cleanThreadLocal();

        // Then: The session should be removed from ThreadLocal and not retrievable
        threadLocalSessionManager.getSession();  // This should create a new session
        verify(sessionFactoryMock, times(2)).openSession(); // Open session called twice
    }
}
