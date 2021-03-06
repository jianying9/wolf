package com.wolf.framework.local;

import com.wolf.framework.injecter.AbstractInjecter;
import com.wolf.framework.injecter.Injecter;
import java.lang.reflect.Field;

/**
 * 解析对象是否需要注入其他对象
 *
 * @author aladdin
 */
public class LocalServiceInjecterImpl extends AbstractInjecter<InjectLocalService> implements Injecter {

    private final LocalServiceContext localServiceContextBuilder;

    public LocalServiceInjecterImpl(final LocalServiceContext localServiceContextBuilder) {
        this.localServiceContextBuilder = localServiceContextBuilder;
    }

    @Override
    protected Class<InjectLocalService> getAnnotation() {
        return InjectLocalService.class;
    }

    @Override
    protected Class<?> getObjectKey(Field field) {
        return field.getType();
    }

    @Override
    protected Object getObject(Class key) {
        return this.localServiceContextBuilder.getLocalService(key);
    }
}
