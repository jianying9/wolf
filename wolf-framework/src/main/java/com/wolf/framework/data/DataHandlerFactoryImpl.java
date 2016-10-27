package com.wolf.framework.data;

import java.util.EnumMap;
import java.util.Map;

/**
 * 验证类对象工厂
 *
 * @author aladdin
 */
public final class DataHandlerFactoryImpl implements DataHandlerFactory {

    public DataHandlerFactoryImpl() {
        this.dataHandlerMap.put(DataType.INTEGER, new IntegerDataHandlerImpl());
        this.dataHandlerMap.put(DataType.DOUBLE, new DoubleDataHandlerImpl());
        this.dataHandlerMap.put(DataType.DATE_TIME, new DateTimeDataHandlerImpl());
        this.dataHandlerMap.put(DataType.BOOLEAN, new BooleanDataHandlerImpl());
        this.dataHandlerMap.put(DataType.DATE, new DateDataHandlerImpl());
    }
    private final Map<DataType, DataHandler> dataHandlerMap = new EnumMap<>(DataType.class);

    @Override
    public DataHandler getDataHandler(DataType dataTypeEnum) {
        return this.dataHandlerMap.get(dataTypeEnum);
    }
}
