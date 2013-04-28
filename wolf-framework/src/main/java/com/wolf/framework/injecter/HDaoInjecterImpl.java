package com.wolf.framework.injecter;

import com.wolf.framework.dao.HEntityDaoContext;
import com.wolf.framework.dao.annotation.HDAO;
import java.lang.reflect.Field;

/**
 *
 * @author aladdin
 */
public class HDaoInjecterImpl extends AbstractInjecter<HDAO> implements Injecter {

    private final HEntityDaoContext entityDaoContextBuilder;

    public HDaoInjecterImpl(final HEntityDaoContext entityDaoContextBuilder) {
        this.entityDaoContextBuilder = entityDaoContextBuilder;
    }

    @Override
    protected Class<HDAO> getAnnotation() {
        return HDAO.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        HDAO dao = field.getAnnotation(HDAO.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.entityDaoContextBuilder.getHEntityDao(key);
    }
}
