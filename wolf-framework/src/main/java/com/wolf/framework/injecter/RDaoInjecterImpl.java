package com.wolf.framework.injecter;

import com.wolf.framework.dao.REntityDaoContext;
import com.wolf.framework.dao.annotation.InjectHDao;
import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class RDaoInjecterImpl extends AbstractInjecter<InjectHDao> implements Injecter {

    private final REntityDaoContext entityDaoContextBuilder;

    public RDaoInjecterImpl(final REntityDaoContext entityDaoContextBuilder) {
        this.entityDaoContextBuilder = entityDaoContextBuilder;
    }

    @Override
    protected Class<InjectHDao> getAnnotation() {
        return InjectHDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectHDao dao = field.getAnnotation(InjectHDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.entityDaoContextBuilder.getREntityDao(key);
    }
}
