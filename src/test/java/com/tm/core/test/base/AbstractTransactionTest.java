package com.tm.core.test.base;

import com.tm.core.dao.basic.ITestEntityDao;
import com.tm.core.util.TestUtil;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;

import javax.sql.DataSource;

public abstract class AbstractTransactionTest {

    protected static SessionFactory sessionFactory;
    protected static ITestEntityDao testEntityDao;
    protected static DataSource dataSource;

    @AfterAll
    public static void cleanUp() {
        TestUtil.cleanUp(sessionFactory);
    }

    public static void prepareTestEntityDb() {
        TestUtil.prepareTestEntityDb(dataSource, DatabaseOperation.DELETE_ALL, "/data/dataset/initEmptyDataSet.xml");
    }

}