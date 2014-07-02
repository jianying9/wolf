package com.wolf.framework.service.parameter;

import com.wolf.framework.config.FrameworkConfig;
import com.wolf.framework.context.ApplicationContext;
import static com.wolf.framework.data.DataClassEnum.DATE;
import static com.wolf.framework.data.DataClassEnum.NUMBER;
import static com.wolf.framework.data.DataClassEnum.STRING;
import com.wolf.framework.data.DataHandler;
import com.wolf.framework.data.DataHandlerFactory;
import com.wolf.framework.data.TypeEnum;
import java.util.Set;

/**
 *
 * @author aladdin
 */
public class RequestParameterHandlerBuilder {

    private final RequestConfig inputConfig;
    private final ApplicationContext applicationContext;
    private final ParameterContext parameterContext;
    private final Set<String> reservedWordSet = FrameworkConfig.getReservedWordSet();

    public RequestParameterHandlerBuilder(
            final RequestConfig inputConfig,
            final ApplicationContext applicationContext,
            final ParameterContext parameterContext) {
        this.inputConfig = inputConfig;
        this.applicationContext = applicationContext;
        this.parameterContext = parameterContext;
    }

    public RequestParameterHandler build() {
        RequestParameterHandler parameterHandler = null;
        final String fieldName = this.inputConfig.name();
        //保留字验证
        if (reservedWordSet.contains(fieldName)) {
            throw new RuntimeException("Error when build InputParameterHandler. Cause: reserved word : ".concat(fieldName));
        }
        //
        final DataHandlerFactory dataHandlerFactory = this.parameterContext.getDataHandlerFactory();
        //基本数据类型
        TypeEnum typeEnum = this.inputConfig.typeEnum();
        DataHandler dataHandler = dataHandlerFactory.getDataHandler(typeEnum);
        if (dataHandler == null) {
            throw new RuntimeException("Error building InputParameterHandler. Cause: could not find DataHandler:" + typeEnum.name());
        }
        switch (typeEnum.getDataClassEnum()) {
            case JSON:
                throw new RuntimeException("Error building InputParameterHandler. Cause: input not support JSON");
            case STRING:
                parameterHandler = new StringParameterHandlerImpl(fieldName, null, dataHandler);
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
