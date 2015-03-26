package com.wolf.framework.dao.reids;

import com.wolf.framework.dao.reids.REntityDaoContext;
import com.wolf.framework.dao.reids.annotation.InjectRDao;
import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class RDaoInjecterImpl extends AbstractInjecter<InjectRDao> implements Injecter {

    private final REntityDaoContext entityDaoContext;

    public RDaoInjecterImpl(final REntityDaoContext entityDaoContext) {
        this.entityDaoContext = entityDaoContext;
    }

    @Override
    protected Class<InjectRDao> getAnnotation() {
        return InjectRDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectRDao dao = field.getAnnotation(InjectRDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.entityDaoContext.getREntityDao(key);
    }
}
