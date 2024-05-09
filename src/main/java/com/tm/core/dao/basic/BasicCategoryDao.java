package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.product.ICategoryDao;
import org.hibernate.SessionFactory;

public class BasicCategoryDao extends AbstractDao implements ICategoryDao {
    public BasicCategoryDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
