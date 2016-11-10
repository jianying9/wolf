package com.wolf.framework.dao.cassandra;

import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;
import com.wolf.framework.dao.cassandra.annotation.InjectCEntityDao;

/**
 *
 * @author jianying9
 */
public class CEntityDaoInjecterImpl extends AbstractInjecter<InjectCEntityDao> implements Injecter {

    private final CassandraAdminContext cassandraAdminContext;

    public CEntityDaoInjecterImpl(final CassandraAdminContext cassandraAdminContext) {
        this.cassandraAdminContext = cassandraAdminContext;
    }

    @Override
    protected Class<InjectCEntityDao> getAnnotation() {
        return InjectCEntityDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectCEntityDao dao = field.getAnnotation(InjectCEntityDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.cassandraAdminContext.getCEntityDao(key);
    }
}
