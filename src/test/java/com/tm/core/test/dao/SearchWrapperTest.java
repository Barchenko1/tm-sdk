package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.dsl.RiderDSL;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.query.SearchWrapper;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.single.SingleTestEntity;
import com.tm.core.processor.finder.parameter.Parameter;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
public class SearchWrapperTest {

    @Mock
    private static IEntityIdentifierDao entityIdentifierDao;

    @InjectMocks
    private static SearchWrapper searchWrapper;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;
        try {
            RiderDSL.withConnection(connectionHolder.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SessionFactory sessionFactory = getSessionFactory();
        searchWrapper = new SearchWrapper(sessionFactory, entityIdentifierDao);
    }

    @BeforeEach
    public void setUp() {
        try {
            Field sessionManagerField = SearchWrapper.class.getDeclaredField("entityIdentifierDao");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(searchWrapper, entityIdentifierDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntityListSupplier_success() {
        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(1L);
        singleTestEntity.setName("Simple Entity");

        when(entityIdentifierDao.getEntityList(eq(SingleTestEntity.class), eq(parameter))).thenReturn(List.of(singleTestEntity));
        Supplier<List<SingleTestEntity>> singleTestEntityResult = searchWrapper.getEntityListSupplier(SingleTestEntity.class, parameter);

        List<SingleTestEntity> result = singleTestEntityResult.get();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Simple Entity", result.get(0).getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(1L);
        singleTestEntity.setName("Simple Entity");
        when(entityIdentifierDao.getOptionalEntity(eq(SingleTestEntity.class), eq(parameter))).thenReturn(Optional.of(singleTestEntity));

        Supplier<Optional<SingleTestEntity>> optionalEntitySupplier = searchWrapper.getOptionalEntitySupplier(SingleTestEntity.class, parameter);

        Optional<SingleTestEntity> optional = optionalEntitySupplier.get();

        assertTrue(optional.isPresent());
        SingleTestEntity result = optional.get();
        assertEquals(1L, result.getId());
        assertEquals("Simple Entity", result.getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        SingleTestEntity singleTestEntity = new SingleTestEntity();
        singleTestEntity.setId(1L);
        singleTestEntity.setName("Simple Entity");
        when(entityIdentifierDao.getEntity(eq(SingleTestEntity.class), eq(parameter))).thenReturn(singleTestEntity);

        Supplier<SingleTestEntity> singleTestEntitySupplier = searchWrapper.getEntitySupplier(SingleTestEntity.class, parameter);

        SingleTestEntity result = singleTestEntitySupplier.get();
        assertEquals(1L, result.getId());
        assertEquals("Simple Entity", result.getName());
    }

}
