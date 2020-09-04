package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;
import com.wolf.framework.dao.elasticsearch.annotation.InjectEsEntityDao;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * @author jianying9
 */
public class EsEntityDaoInjecterImpl extends AbstractInjecter<InjectEsEntityDao> implements Injecter {

    private final EsContext esAdminContext;

    public EsEntityDaoInjecterImpl(final EsContext esAdminContext) {
        this.esAdminContext = esAdminContext;
    }

    @Override
    protected Class<InjectEsEntityDao> getAnnotation() {
        return InjectEsEntityDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
        Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
        Class<?> clz = (Class<?>) listActualTypeArguments[0];
        return clz;
    }

    @Override
    protected Object getObject(Class key) {
        return this.esAdminContext.getEsEntityDao(key);
    }
}
