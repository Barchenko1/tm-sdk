package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.post.ICommentDao;
import org.hibernate.SessionFactory;

public class BasicCommentDao extends AbstractDao implements ICommentDao {
    public BasicCommentDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
