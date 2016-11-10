package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.cassandra.annotation.InjectCCounterDao;
import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;
import com.wolf.framework.dao.cassandra.annotation.InjectCEntityDao;

/**
 *
 * @author jianying9
 */
public class CCounterDaoInjecterImpl extends AbstractInjecter<InjectCCounterDao> implements Injecter {

    private final CassandraAdminContext cassandraAdminContext;

    public CCounterDaoInjecterImpl(final CassandraAdminContext cassandraAdminContext) {
        this.cassandraAdminContext = cassandraAdminContext;
    }

    @Override
    protected Class<InjectCCounterDao> getAnnotation() {
        return InjectCCounterDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectCCounterDao dao = field.getAnnotation(InjectCCounterDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.cassandraAdminContext.getCCounterDao(key);
    }
}
