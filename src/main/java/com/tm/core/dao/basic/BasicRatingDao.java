package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.rating.IRatingDao;
import org.hibernate.SessionFactory;

public class BasicRatingDao extends AbstractDao implements IRatingDao {
    public BasicRatingDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
