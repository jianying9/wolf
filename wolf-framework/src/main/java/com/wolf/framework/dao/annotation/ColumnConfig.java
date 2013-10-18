package com.wolf.framework.dao.annotation;

import com.wolf.framework.data.BasicTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于描述entity中各个field的信息
 *
 * @author aladdin
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ColumnConfig {

    /**
     * 数据类型
     *
     * @return
     */
    public BasicTypeEnum dataTypeEnum();

    /**
     * 列类型
     *
     * @return
     */
    public ColumnTypeEnum columnTypeEnum() default ColumnTypeEnum.COLUMN;

    /**
     * 描述
     *
     * @return
     */
    public String desc();
}
