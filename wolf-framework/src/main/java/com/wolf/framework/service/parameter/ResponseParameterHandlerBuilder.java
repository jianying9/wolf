package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import com.wolf.framework.data.DataClassEnum;
import static com.wolf.framework.data.DataClassEnum.DATE;
import static com.wolf.framework.data.DataClassEnum.JSON;
import static com.wolf.framework.data.DataClassEnum.NUMBER;
import static com.wolf.framework.data.DataClassEnum.STRING;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.TypeEnum;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterTypeEnum;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public class ResponseParameterHandlerBuilder {

    private final ResponseConfig outputConfig;
    private final ApplicationContext applicationContext;
    private final ParameterContext parameterContext;
    private final Set<String> reservedWordSet = FrameworkConfig.getReservedWordSet();

    public ResponseParameterHandlerBuilder(
            final ResponseConfig outputConfig,
            final ApplicationContext applicationContext,
            final ParameterContext parameterContext) {
        this.outputConfig = outputConfig;
        this.applicationContext = applicationContext;
        this.parameterContext = parameterContext;
    }

    public ResponseParameterHandler build() {
        ResponseParameterHandler parameterHandler = null;
        final String fieldName = this.outputConfig.name();
        //保留字验证
        if (reservedWordSet.contains(fieldName)) {
            throw new RuntimeException("Error when build OutputParameterHandler. Cause: reserved word : ".concat(fieldName));
        }
        //
        final FilterFactory filterFactory = this.parameterContext.getFilterFactory();
        //
        final DataHandlerFactory dataHandlerFactory = this.parameterContext.getDataHandlerFactory();
        //基本数据类型
        TypeEnum typeEnum = this.outputConfig.typeEnum();
        DataHandler dataHandler = dataHandlerFactory.getDataHandler(typeEnum);
        if (dataHandler == null && typeEnum.getDataClassEnum().equals(DataClassEnum.JSON) == false) {
            throw new RuntimeException("Error when building OutputParameterHandler. Cause: could not find DataHandler:" + typeEnum.name());
        }
        switch (typeEnum.getDataClassEnum()) {
            case JSON:
                String defaultValue = "{}";
                if (typeEnum.equals(TypeEnum.ARRAY)) {
                    defaultValue = "[]";
                }
                parameterHandler = new JsonParameterHandlerImpl(fieldName, typeEnum.name(), defaultValue);
                break;
            case STRING:
                Filter[] filters;
                if (typeEnum != TypeEnum.UUID) {
                    //获取过滤对象
                    FilterTypeEnum[] filterTypeEnums = this.outputConfig.filterTypes();
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
                parameterHandler = new StringParameterHandlerImpl(fieldName, filters, dataHandler);
                break;
            case DATE:
                parameterHandler = new DateParameterHandlerImpl(fieldName, dataHandler);
                break;
            case NUMBER:
                parameterHandler = new NumberParameterHandlerImpl(fieldName, dataHandler);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanParameterHandlerImpl(fieldName, dataHandler);
                break;
        }
        return parameterHandler;
    }
}
