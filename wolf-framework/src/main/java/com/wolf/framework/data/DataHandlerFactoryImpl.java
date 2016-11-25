package com.wolf.framework.data;

import java.util.EnumMap;
import java.util.Map;

/**
 * 验证类对象工厂
 *
 * @author jianying9
 */
public final class DataHandlerFactoryImpl implements DataHandlerFactory {

    public DataHandlerFactoryImpl() {
        this.dataHandlerMap.put(DataType.LONG, new LongDataHandlerImpl());
        this.dataHandlerMap.put(DataType.DOUBLE, new DoubleDataHandlerImpl());
        this.dataHandlerMap.put(DataType.DATE_TIME, new DateTimeDataHandlerImpl());
        this.dataHandlerMap.put(DataType.BOOLEAN, new BooleanDataHandlerImpl());
        this.dataHandlerMap.put(DataType.DATE, new DateDataHandlerImpl());
        this.dataHandlerMap.put(DataType.CHINA_MOBILE, new ChinaMobileDataHandlerImpl());
        this.dataHandlerMap.put(DataType.EMAIL, new EmailDataHandlerImpl());
    }
    private final Map<DataType, DataHandler> dataHandlerMap = new EnumMap<>(DataType.class);

    @Override
    public DataHandler getDataHandler(DataType dataType) {
        return this.dataHandlerMap.get(dataType);
    }
}
