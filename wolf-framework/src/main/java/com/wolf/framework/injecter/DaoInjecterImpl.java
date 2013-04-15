package com.wolf.framework.injecter;

import com.wolf.framework.dao.annotation.DAO;
import com.wolf.framework.dao.EntityDaoContext;
import java.lang.reflect.Field;

/**
 * 解析对象是否需要注入其他对象
 *
 * @author aladdin
 */
public class DaoInjecterImpl extends AbstractInjecter<DAO> implements Injecter {

    private final EntityDaoContext entityDaoContextBuilder;

    public DaoInjecterImpl(final EntityDaoContext entityDaoContextBuilder) {
        this.entityDaoContextBuilder = entityDaoContextBuilder;
    }

    @Override
    protected Class<DAO> getAnnotation() {
        return DAO.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        DAO dao = field.getAnnotation(DAO.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.entityDaoContextBuilder.getEntityDao(key);
    }
}
