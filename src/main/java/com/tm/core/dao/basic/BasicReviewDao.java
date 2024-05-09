package com.tm.core.dao.basic;

import com.tm.core.dao.AbstractDao;
import com.tm.core.dao.review.IReviewDao;
import org.hibernate.SessionFactory;

public class BasicReviewDao extends AbstractDao implements IReviewDao {
    public BasicReviewDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
