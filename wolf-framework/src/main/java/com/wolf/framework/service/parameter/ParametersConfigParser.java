package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * 负责解析annotation ExtendedEntityConfig
 *
 * @author aladdin
 */
public class ParametersConfigParser {

    private final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    private final ParametersContext parametersContext;

    public ParametersConfigParser(ParametersContext parametersContext) {
        this.parametersContext = parametersContext;
    }

    /**
     * 解析方法
     *
     * @param <?>
     * @param clazz
     */
    public void parse(final Class<?> clazz) {
        this.logger.debug("--parsing parameter entity {}--", clazz.getName());
        if (clazz.isAnnotationPresent(ParametersConfig.class)) {
            //3.获取该实体所有字段集合
            final Field[] fields = clazz.getDeclaredFields();
            ParameterConfig parameterConfig;
            final Map<String, ParameterHandler> parameterHandlerMap = new HashMap<String, ParameterHandler>(2, 1);
            ParameterHandlerBuilder parameterHandlerBuilder;
            ParameterHandler fieldHandler;
            int modifier;
            for (Field field : fields) {
                modifier = field.getModifiers();
                if (!Modifier.isStatic(modifier) && field.isAnnotationPresent(ParameterConfig.class)) {
                    parameterConfig = field.getAnnotation(ParameterConfig.class);
                    parameterHandlerBuilder = new ParameterHandlerBuilder(this.parametersContext, field.getName(), parameterConfig);
                    fieldHandler = parameterHandlerBuilder.build();
                    parameterHandlerMap.put(field.getName(), fieldHandler);
                }
            }
            //4.保存ExtendedEntityHandler
            ParametersHandlerImpl extendedEntityHandler = new ParametersHandlerImpl(parameterHandlerMap);
            parametersContext.putParametersHandler(clazz, extendedEntityHandler);
            this.logger.debug("--parse parameter entity {} finished--", clazz.getName());
        } else {
            this.logger.error("--parse parameter entity {} missing annotation ParametersConfig-----------------", clazz.getName());
        }
    }
}
