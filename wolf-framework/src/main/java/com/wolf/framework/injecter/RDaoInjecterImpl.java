package com.wolf.framework.injecter;

import com.wolf.framework.dao.REntityDaoContext;
import com.wolf.framework.dao.annotation.InjectRDao;
import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class RDaoInjecterImpl extends AbstractInjecter<InjectRDao> implements Injecter {

    private final REntityDaoContext entityDaoContextBuilder;

    public RDaoInjecterImpl(final REntityDaoContext entityDaoContextBuilder) {
        this.entityDaoContextBuilder = entityDaoContextBuilder;
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
        return this.entityDaoContextBuilder.getREntityDao(key);
    }
}
