package com.tm.core.test.dao;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.connection.ConnectionHolder;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.tm.core.dao.identifier.EntityIdentifierDao;
import com.tm.core.dao.query.SearchWrapper;
import com.tm.core.dao.identifier.IEntityIdentifierDao;
import com.tm.core.modal.single.SingleTestEntity;
import com.tm.core.processor.finder.manager.EntityMappingManager;
import com.tm.core.processor.finder.manager.IEntityMappingManager;
import com.tm.core.processor.finder.parameter.Parameter;
import com.tm.core.processor.finder.table.EntityTable;
import com.tm.core.processor.thread.IThreadLocalSessionManager;
import com.tm.core.processor.thread.ThreadLocalSessionManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.tm.core.configuration.ConfigureSessionFactoryTest.getSessionFactory;
import static com.tm.core.configuration.DataSourcePool.getHikariDataSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
public class SearchWrapperTest {

    private static IEntityIdentifierDao entityIdentifierDao;

    private static SearchWrapper searchWrapper;

    private static ConnectionHolder connectionHolder;

    @BeforeAll
    public static void setUpAll() {
        DataSource dataSource = getHikariDataSource();
        connectionHolder = dataSource::getConnection;

        SessionFactory sessionFactory = getSessionFactory();

        IThreadLocalSessionManager sessionManager = new ThreadLocalSessionManager(sessionFactory);
        IEntityMappingManager entityMappingManager = new EntityMappingManager();
        entityMappingManager.addEntityTable(new EntityTable(SingleTestEntity.class, "singletestentity"));
        entityIdentifierDao = new EntityIdentifierDao(sessionManager, entityMappingManager);
        searchWrapper = new SearchWrapper(sessionManager, entityIdentifierDao);
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

        Supplier<List<SingleTestEntity>> singleTestEntityResult =
                searchWrapper.getEntityListSupplier(SingleTestEntity.class, parameter);

        List<SingleTestEntity> result = singleTestEntityResult.get();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Test Entity 1", result.get(0).getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getOptionalEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        Supplier<Optional<SingleTestEntity>> optionalEntitySupplier =
                searchWrapper.getOptionalEntitySupplier(SingleTestEntity.class, parameter);

        Optional<SingleTestEntity> optional = optionalEntitySupplier.get();

        assertTrue(optional.isPresent());
        SingleTestEntity result = optional.get();
        assertEquals(1L, result.getId());
        assertEquals("Test Entity 1", result.getName());
    }

    @Test
    @DataSet("datasets/single/testSingleEntityDataSet.yml")
    void getEntity_success() {
        Parameter parameter = new Parameter("id", 1L);

        Supplier<SingleTestEntity> singleTestEntitySupplier =
                searchWrapper.getEntitySupplier(SingleTestEntity.class, parameter);

        SingleTestEntity result = singleTestEntitySupplier.get();
        assertEquals(1L, result.getId());
        assertEquals("Test Entity 1", result.getName());
    }

}
