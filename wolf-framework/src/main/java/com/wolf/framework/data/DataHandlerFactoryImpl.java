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
        this.dataHandlerMap.put(DataTypeEnum.INT, new IntDataHandlerImpl());
        this.dataHandlerMap.put(DataTypeEnum.LONG, new LongDataHandlerImpl());
        this.dataHandlerMap.put(DataTypeEnum.DOUBLE, null);
        this.dataHandlerMap.put(DataTypeEnum.DATE_TIME, new DateTimeDataHandlerImpl());
        this.dataHandlerMap.put(DataTypeEnum.DATE, new DateDataHandlerImpl());
        this.dataHandlerMap.put(DataTypeEnum.UUID, new CharDataHandlerImpl(" must be uuid", 36, DataTypeEnum.UUID));
        this.dataHandlerMap.put(DataTypeEnum.CHAR_10, new CharDataHandlerImpl(" must be char10", 10, DataTypeEnum.CHAR_10));
        this.dataHandlerMap.put(DataTypeEnum.CHAR_32, new CharDataHandlerImpl(" must be char32", 32, DataTypeEnum.CHAR_32));
        this.dataHandlerMap.put(DataTypeEnum.CHAR_60, new CharDataHandlerImpl(" must be char60", 60, DataTypeEnum.CHAR_60));
        this.dataHandlerMap.put(DataTypeEnum.CHAR_120, new CharDataHandlerImpl(" must be char120", 120, DataTypeEnum.CHAR_120));
        this.dataHandlerMap.put(DataTypeEnum.CHAR_255, new CharDataHandlerImpl(" must be char255", 255, DataTypeEnum.CHAR_255));
        this.dataHandlerMap.put(DataTypeEnum.CHAR_4000, new CharDataHandlerImpl(" must be char4000", 4000, DataTypeEnum.CHAR_4000));
    }
    private final Map<DataTypeEnum, DataHandler> dataHandlerMap = new EnumMap<DataTypeEnum, DataHandler>(DataTypeEnum.class);

    public DataHandler getDataHandler(DataTypeEnum dataTypeEnum) {
        return this.dataHandlerMap.get(dataTypeEnum);
    }
}
