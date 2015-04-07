package com.wolf.framework.injecter;

import com.wolf.framework.config.FrameworkLogger;
import com.wolf.framework.logger.LogFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 * @param <A>
 */
public abstract class AbstractInjecter<A extends Annotation> {

    private final Logger logger = LogFactory.getLogger(FrameworkLogger.FRAMEWORK);

    protected abstract Class<A> getAnnotation();

    protected abstract Class<?> getObjectKey(Field field);

    protected abstract Object getObject(Class<?> key);

    public final void parse(Object object) {
        Class<?> clazz = object.getClass();
        Class<A> annotation = this.getAnnotation();
        this.logger.debug("----injecting instance {}{}--", annotation.getName(), clazz.getName());
        Field[] fileds = clazz.getDeclaredFields();
        Class<?> key;
        Object value;
        for (Field field : fileds) {
            if (field.isAnnotationPresent(annotation)) {
                this.logger.debug("----find {} field {}--", annotation.getName(), field.getName());
                key = this.getObjectKey(field);
                value = this.getObject(key);
                if (value == null) {
                    this.logger.error("Error when instancing field. Cause: can not find  by class {}", key.getName());
                    throw new RuntimeException("Error when instancing field in class: ".concat(clazz.getName()));
                } else {
                    field.setAccessible(true);
                    try {
                        field.set(object, value);
                    } catch (IllegalArgumentException ex) {
                        this.logger.error("Error when instancing field:".concat(field.getName()), ex);
                    } catch (IllegalAccessException ex) {
                        this.logger.error("Error when instancing field:".concat(field.getName()), ex);
                    }
                }
            }
        }
        this.logger.debug("----injecting instance {} {} finished--", annotation.getName(), clazz.getName());
    }
}
