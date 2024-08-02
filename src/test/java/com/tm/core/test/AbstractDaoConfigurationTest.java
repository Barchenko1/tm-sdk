package com.tm.core.test;

import com.tm.core.dao.basic.ITestEntityDao;
import com.tm.core.processor.ThreadLocalSessionManager;
import com.tm.core.util.TestUtil;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;

import javax.sql.DataSource;

public abstract class AbstractDaoConfigurationTest {

    protected static SessionFactory sessionFactory;
    protected static ThreadLocalSessionManager sessionManager;
    protected static ITestEntityDao testEntityDao;
    protected static DataSource dataSource;

    @AfterAll
    public static void cleanUp() {
        TestUtil.cleanUp(sessionFactory);
    }

    public static void prepareTestEntityDb() {
        TestUtil.prepareTestEntityDb(dataSource, DatabaseOperation.CLEAN_INSERT, "/data/dataset/initDataSet.xml");
    }

}
