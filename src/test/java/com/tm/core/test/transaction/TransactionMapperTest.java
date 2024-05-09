package com.tm.core.test.transaction;

import com.tm.core.config.ConfigDbType;
import com.tm.core.config.factory.ConfigurationSessionFactory;
import com.tm.core.dto.ChildClass;
import com.tm.core.dto.ParentClass;
import com.tm.core.dto.TestClass;
import com.tm.core.dto.TestDto;
import com.tm.core.mapper.DtoEntityBind;
import com.tm.core.mapper.DtoEntityMapper;
import com.tm.core.mapper.IDtoEntityBind;
import com.tm.core.mapper.IDtoEntityMapper;
import com.tm.core.test.base.AbstractTransactionTest;
import com.tm.core.transaction.BasicTransactionManager;
import com.tm.core.transaction.ITransactionManager;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.tm.core.util.DataSourcePool.getHikariDataSource;

@ExtendWith(DBUnitExtension.class)
public class TransactionMapperTest extends AbstractTransactionTest {
    private static ConnectionHolder connectionHolder;
    private static IDtoEntityBind dtoEntityBind;
    private static IDtoEntityMapper dtoEntityMapper;
    private static ITransactionManager transactionManager;

    @BeforeAll
    public static void getSessionFactory() {
        ConfigurationSessionFactory configurationSessionFactory = new ConfigurationSessionFactory(
                ConfigDbType.XML
        );
        sessionFactory = configurationSessionFactory.getSessionFactory();
        transactionManager = new BasicTransactionManager(sessionFactory);

        dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        dtoEntityBind = new DtoEntityBind("bind");
        dtoEntityMapper = new DtoEntityMapper(dtoEntityBind);
    }

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true)
    void dtoMapperTest() {
        ParentClass parentClass = new ParentClass();
        parentClass.setName("parent");
        parentClass.setAge(80);

        ChildClass childClass = new ChildClass();
        childClass.setName("child");
        childClass.setAge(2);

        TestDto testDto = new TestDto();
        testDto.setName("test");
        testDto.setDtoAge(11);
        testDto.setParent(parentClass);
        testDto.getChildClassList().add(childClass);

        TestClass testClass = new TestClass();
        dtoEntityMapper.mapDtoToEntity(testDto, testClass, "testDtoBind");
        transactionManager.useTransaction(testClass);
    }
}
