package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.post.IPostDao;
import org.hibernate.SessionFactory;

public class BasicPostDao extends AbstractDao implements IPostDao {
    public BasicPostDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
