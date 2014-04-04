package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.BasicTypeEnum;
import com.wolf.framework.data.JsonTypeEnum;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterTypeEnum;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public class ParameterHandlerBuilder {

    private final ParameterConfig parameterConfig;
    private final ApplicationContext applicationContext;
    private final ParameterContext parameterContext;
    private final Set<String> reservedWordSet = FrameworkConfig.getReservedWordSet();

    public ParameterHandlerBuilder(
            final ParameterConfig parameterConfig,
            final ApplicationContext applicationContext,
            final ParameterContext parameterContext) {
        this.parameterConfig = parameterConfig;
        this.applicationContext = applicationContext;
        this.parameterContext = parameterContext;
    }

    public ParameterHandler build() {
        ParameterHandler parameterHandler = null;
        final String fieldName = this.parameterConfig.name();
        //保留字验证
        if (reservedWordSet.contains(fieldName)) {
            throw new RuntimeException("Error when build ParameterHandler. Cause: reserved word : ".concat(fieldName));
        }
        //
        //获取描述
        String compileModel = this.applicationContext.getParameter(FrameworkConfig.COMPILE_MODEL);
        final String desc;
        if (compileModel.equals(FrameworkConfig.DEVELOPMENT)) {
            //开发模式，保留参数描述信息
            desc = this.parameterConfig.desc();
        } else {
            desc = "";
        }
        //
        final DataHandlerFactory dataHandlerFactory = this.parameterContext.getDataHandlerFactory();
        final FilterFactory filterFactory = this.parameterContext.getFilterFactory();
        //获取默认值
        final String defaultValue = parameterConfig.defaultValue();
        //获取类型
        ParameterTypeEnum parameterTypeEnum = this.parameterConfig.parameterTypeEnum();
        switch (parameterTypeEnum) {
            case BASIC:
                //基本数据类型
                BasicTypeEnum basicTypeEnum = this.parameterConfig.basicTypeEnum();
                DataHandler dataHandler = dataHandlerFactory.getDataHandler(basicTypeEnum);
                if (dataHandler == null) {
                    throw new RuntimeException("There was an error building FieldHandler. Cause: could not find DataHandler.");
                }
                switch (basicTypeEnum.getDataClassEnum()) {
                    case STRING:
                        Filter[] filters;
                        if (basicTypeEnum != BasicTypeEnum.UUID) {
                            //获取过滤对象
                            FilterTypeEnum[] filterTypeEnums = this.parameterConfig.filterTypes();

                            if (filterTypeEnums.length > 0) {
                                Filter filter;
                                filters = new Filter[filterTypeEnums.length];
                                for (int index = 0; index < filterTypeEnums.length; index++) {
                                    filter = filterFactory.getFilter(filterTypeEnums[index]);
                                    if (filter == null) {
                                        throw new RuntimeException("Error when building FieldHandler. Cause: could not find Filter.");
                                    }
                                    filters[index] = filter;
                                }
                            } else {
                                filters = new Filter[0];
                            }
                        } else {
                            filters = new Filter[0];
                        }
                        parameterHandler = new StringParameterHandlerImpl(fieldName, filters, dataHandler, defaultValue, desc);
                        break;
                    case DATE:
                        parameterHandler = new DateParameterHandlerImpl(fieldName, dataHandler, defaultValue, desc);
                        break;
                    case NUMBER:
                        parameterHandler = new NumberParameterHandlerImpl(fieldName, dataHandler, defaultValue, desc);
                        break;
                }
                break;
            case JSON:
                //json类型
                JsonTypeEnum jsonTypeEnum = this.parameterConfig.jsonTypeEnum();
                parameterHandler = new JsonParameterHandlerImpl(fieldName, jsonTypeEnum.name(), desc, defaultValue);
                break;
        }
        return parameterHandler;
    }
}
