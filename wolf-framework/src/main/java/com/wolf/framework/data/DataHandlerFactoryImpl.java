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
        this.dataHandlerMap.put(BasicTypeEnum.INT, new IntDataHandlerImpl());
        this.dataHandlerMap.put(BasicTypeEnum.LONG, new LongDataHandlerImpl());
        this.dataHandlerMap.put(BasicTypeEnum.DOUBLE, null);
        this.dataHandlerMap.put(BasicTypeEnum.DATE_TIME, new DateTimeDataHandlerImpl());
        this.dataHandlerMap.put(BasicTypeEnum.DATE, new DateDataHandlerImpl());
        this.dataHandlerMap.put(BasicTypeEnum.UUID, new CharDataHandlerImpl(" must be uuid", 36, BasicTypeEnum.UUID));
        this.dataHandlerMap.put(BasicTypeEnum.CHAR_10, new CharDataHandlerImpl(" must be char10", 10, BasicTypeEnum.CHAR_10));
        this.dataHandlerMap.put(BasicTypeEnum.CHAR_32, new CharDataHandlerImpl(" must be char32", 32, BasicTypeEnum.CHAR_32));
        this.dataHandlerMap.put(BasicTypeEnum.CHAR_60, new CharDataHandlerImpl(" must be char60", 60, BasicTypeEnum.CHAR_60));
        this.dataHandlerMap.put(BasicTypeEnum.CHAR_120, new CharDataHandlerImpl(" must be char120", 120, BasicTypeEnum.CHAR_120));
        this.dataHandlerMap.put(BasicTypeEnum.CHAR_255, new CharDataHandlerImpl(" must be char255", 255, BasicTypeEnum.CHAR_255));
        this.dataHandlerMap.put(BasicTypeEnum.CHAR_4000, new CharDataHandlerImpl(" must be char4000", 4000, BasicTypeEnum.CHAR_4000));
    }
    private final Map<BasicTypeEnum, DataHandler> dataHandlerMap = new EnumMap<BasicTypeEnum, DataHandler>(BasicTypeEnum.class);

    public DataHandler getDataHandler(BasicTypeEnum dataTypeEnum) {
        return this.dataHandlerMap.get(dataTypeEnum);
    }
}
