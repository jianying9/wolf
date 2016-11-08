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
    
    public final void parseSuper(Object object, Class<?> superClass) {
        Class<A> annotation = this.getAnnotation();
        Field[] fileds = superClass.getDeclaredFields();
        Class<?> key;
        Object value;
        for (Field field : fileds) {
            if (field.isAnnotationPresent(annotation)) {
                this.logger.debug("----find {} field {}--", annotation.getName(), field.getName());
                key = this.getObjectKey(field);
                value = this.getObject(key);
                if (value == null) {
                    this.logger.error("Error. Cause: can not find  by class {}", key.getName());
                    throw new RuntimeException("Error when inject field in class: ".concat(superClass.getName()));
                } else {
                    field.setAccessible(true);
                    try {
                        field.set(object, value);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        this.logger.error("Error when inject field:".concat(field.getName()), ex);
                    }
                }
            }
        }
        if(superClass.getSuperclass().equals(Object.class) == false) {
            //父类存在,为父类注入
            this.parseSuper(object, superClass.getSuperclass());
        }
    }

    public final void parse(Object object) {
        Class<?> clazz = object.getClass();
        Class<A> annotation = this.getAnnotation();
        this.logger.debug("----injecting instance {}{}--", annotation.getName(), clazz.getName());
        this.parseSuper(object, clazz);
        this.logger.debug("----injecting instance {} {} finished--", annotation.getName(), clazz.getName());
    }
}
