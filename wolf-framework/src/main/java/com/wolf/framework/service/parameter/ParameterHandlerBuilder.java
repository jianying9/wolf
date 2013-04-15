package com.wolf.framework.service.parameter;

import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.DataTypeEnum;
import com.wolf.framework.service.parameter.filter.Filter;
import com.wolf.framework.service.parameter.filter.FilterFactory;
import com.wolf.framework.service.parameter.filter.FilterTypeEnum;

/**
 *
 * @author aladdin
 */
public class ParameterHandlerBuilder {

    private final String fieldName;
    private final ParameterConfig parameterConfig;
    private final DataHandlerFactory dataHandlerFcatory;
    private final FilterFactory filterFactory;
    private final String reservedWord = "|act|returnNames|noPseudo|loginCode|loginEmpId|entry|seed|";

    public ParameterHandlerBuilder(final ParameterContextBuilder paraCtxBuilder, final String fieldName, final ParameterConfig fieldConfig) {
        this.fieldName = fieldName;
        this.parameterConfig = fieldConfig;
        this.filterFactory = paraCtxBuilder.getFilterFactory();
        this.dataHandlerFcatory = paraCtxBuilder.getDataHandlerFactory();
    }

    public ParameterHandler build() {
        ParameterHandler parameterHandler = null;
        //保留字验证
        StringBuilder validateBuilder = new StringBuilder(fieldName.length() + 2);
        validateBuilder.append('|').append(this.fieldName).append('|');
        if (this.reservedWord.indexOf(validateBuilder.toString()) > -1) {
            throw new RuntimeException("There was an error building FieldHandler. Cause: reserved word : ".concat(fieldName));
        }
        //获取描述
        final String desc = this.parameterConfig.desc();
        //获取默认值
        final String defaultValue = parameterConfig.defaultValue();
        //获取类型
        ParameterTypeEnum parameterTypeEnum = this.parameterConfig.parameterTypeEnum();
        switch (parameterTypeEnum) {
            case BASIC:
                //基本数据类型
                DataTypeEnum dataTypeEnum = this.parameterConfig.dateTypeEnum();
                DataHandler dataHandler = this.dataHandlerFcatory.getDataHandler(dataTypeEnum);
                if (dataHandler == null) {
                    throw new RuntimeException("There was an error building FieldHandler. Cause: could not find DataHandler.");
                }
                switch (dataTypeEnum.getDataClassEnum()) {
                    case STRING:
                        Filter[] filters;
                        if (dataTypeEnum != DataTypeEnum.UUID) {
                            //获取过滤对象
                            FilterTypeEnum[] filterTypeEnums = this.parameterConfig.filterTypes();

                            if (filterTypeEnums.length > 0) {
                                Filter filter;
                                filters = new Filter[filterTypeEnums.length];
                                for (int index = 0; index < filterTypeEnums.length; index++) {
                                    filter = this.filterFactory.getFilter(filterTypeEnums[index]);
                                    if (filter == null) {
                                        throw new RuntimeException("There was an error building FieldHandler. Cause: could not find Filter.");
                                    }
                                    filters[index] = filter;
                                }
                            } else {
                                filters = new Filter[0];
                            }
                        } else {
                            filters = new Filter[0];
                        }
                        parameterHandler = new StringParameterHandlerImpl(this.fieldName, filters, dataHandler, defaultValue, desc);
                        break;
                    case DATE:
                        parameterHandler = new DateParameterHandlerImpl(this.fieldName, dataHandler, defaultValue, desc);
                        break;
                    case NUMBER:
                        parameterHandler = new NumberParameterHandlerImpl(this.fieldName, dataHandler, defaultValue, desc);
                        break;
                }
                break;
            case JSON:
                parameterHandler = new JsonParameterHandlerImpl(this.fieldName, desc, defaultValue);
                break;
        }
        return parameterHandler;
    }
}
