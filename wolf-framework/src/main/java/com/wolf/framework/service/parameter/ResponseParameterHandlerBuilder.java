package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataType;
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
    private final ParameterContext parameterContext;
    private final Set<String> reservedWordSet = FrameworkConfig.getReservedWordSet();

    public ResponseParameterHandlerBuilder(
            final ResponseConfig outputConfig,
            final ParameterContext parameterContext) {
        this.outputConfig = outputConfig;
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
        DataType dataType = this.outputConfig.dataType();
        DataHandler dataHandler = dataHandlerFactory.getDataHandler(dataType);
        if (dataHandler == null && dataType.equals(DataType.CHAR) == false) {
            throw new RuntimeException("Error when building OutputParameterHandler. Cause: could not find DataHandler:" + dataType.name());
        }
        switch (dataType) {
            case OBJECT:
                parameterHandler = new JsonParameterHandlerImpl(fieldName, dataType.name(), "{}");
                break;
            case ARRAY:
                parameterHandler = new JsonParameterHandlerImpl(fieldName, dataType.name(), "[]");
                break;
            case CHAR:
                Filter[] filters = null;
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
                }
                parameterHandler = new StringParameterHandlerImpl(fieldName, filters, 1, 0);
                break;
            case DATE:
                parameterHandler = new DateParameterHandlerImpl(fieldName, dataHandler);
                break;
            case DATE_TIME:
                parameterHandler = new DateParameterHandlerImpl(fieldName, dataHandler);
                break;
            case INTEGER:
                parameterHandler = new NumberParameterHandlerImpl(fieldName, dataHandler, 1, 0);
                break;
            case DOUBLE:
                parameterHandler = new NumberParameterHandlerImpl(fieldName, dataHandler, 1, 0);
                break;
            case BOOLEAN:
                parameterHandler = new BooleanParameterHandlerImpl(fieldName, dataHandler);
                break;
        }
        return parameterHandler;
    }
}
