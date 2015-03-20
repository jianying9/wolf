package com.wolf.framework.dao.cassandra;

import com.wolf.framework.dao.cassandra.annotation.InjectCDao;
import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;

/**
 *
 * @author jianying9
 */
public class CDaoInjecterImpl extends AbstractInjecter<InjectCDao> implements Injecter {

    private final CEntityDaoContext cEntityDaoContext;

    public CDaoInjecterImpl(final CEntityDaoContext cEntityDaoContext) {
        this.cEntityDaoContext = cEntityDaoContext;
    }

    @Override
    protected Class<InjectCDao> getAnnotation() {
        return InjectCDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectCDao dao = field.getAnnotation(InjectCDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.cEntityDaoContext.getCEntityDao(key);
    }
}
