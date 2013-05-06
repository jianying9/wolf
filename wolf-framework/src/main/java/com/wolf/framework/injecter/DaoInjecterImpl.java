package com.wolf.framework.injecter;

import com.wolf.framework.dao.EntityDaoContext;
import com.wolf.framework.dao.annotation.InjectDao;
import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class DaoInjecterImpl extends AbstractInjecter<InjectDao> implements Injecter {

    private final EntityDaoContext entityDaoContextBuilder;

    public DaoInjecterImpl(final EntityDaoContext entityDaoContextBuilder) {
        this.entityDaoContextBuilder = entityDaoContextBuilder;
    }

    @Override
    protected Class<InjectDao> getAnnotation() {
        return InjectDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectDao dao = field.getAnnotation(InjectDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.entityDaoContextBuilder.getEntityDao(key);
    }
}
