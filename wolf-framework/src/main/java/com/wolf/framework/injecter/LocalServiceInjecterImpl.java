package com.wolf.framework.injecter;

import com.wolf.framework.local.LocalService;
import com.wolf.framework.local.LocalServiceContextBuilder;
import java.lang.reflect.Field;

/**
 * 解析对象是否需要注入其他对象
 *
 * @author aladdin
 */
public class LocalServiceInjecterImpl extends AbstractInjecter<LocalService> implements Injecter {

    private final LocalServiceContextBuilder localServiceContextBuilder;

    public LocalServiceInjecterImpl(final LocalServiceContextBuilder localServiceContextBuilder) {
        this.localServiceContextBuilder = localServiceContextBuilder;
    }

    @Override
    protected Class<LocalService> getAnnotation() {
        return LocalService.class;
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
