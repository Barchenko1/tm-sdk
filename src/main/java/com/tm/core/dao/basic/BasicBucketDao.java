package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.bucket.IBucketDao;
import org.hibernate.SessionFactory;

public class BasicBucketDao extends AbstractDao implements IBucketDao {
    public BasicBucketDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
