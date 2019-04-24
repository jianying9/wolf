package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;
import com.wolf.framework.dao.elasticsearch.annotation.InjectEsEntityDao;

/**
 *
 * @author jianying9
 */
public class EsEntityDaoInjecterImpl extends AbstractInjecter<InjectEsEntityDao> implements Injecter {

    private final EsAdminContext esAdminContext;

    public EsEntityDaoInjecterImpl(final EsAdminContext esAdminContext) {
        this.esAdminContext = esAdminContext;
    }

    @Override
    protected Class<InjectEsEntityDao> getAnnotation() {
        return InjectEsEntityDao.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        InjectEsEntityDao dao = field.getAnnotation(InjectEsEntityDao.class);
        return dao.clazz();
    }

    @Override
    protected Object getObject(Class key) {
        return this.esAdminContext.getEsEntityDao(key);
    }
}
