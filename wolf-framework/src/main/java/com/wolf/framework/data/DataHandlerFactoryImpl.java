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
        this.dataHandlerMap.put(TypeEnum.INT, new IntDataHandlerImpl());
        this.dataHandlerMap.put(TypeEnum.LONG, new LongDataHandlerImpl());
        this.dataHandlerMap.put(TypeEnum.DOUBLE, null);
        this.dataHandlerMap.put(TypeEnum.DATE_TIME, new DateTimeDataHandlerImpl());
        this.dataHandlerMap.put(TypeEnum.BOOLEAN, new BooleanDataHandlerImpl());
        this.dataHandlerMap.put(TypeEnum.DATE, new DateDataHandlerImpl());
        this.dataHandlerMap.put(TypeEnum.UUID, new CharDataHandlerImpl(" must be uuid", 36, TypeEnum.UUID));
        this.dataHandlerMap.put(TypeEnum.CHAR_10, new CharDataHandlerImpl(" must be char10", 10, TypeEnum.CHAR_10));
        this.dataHandlerMap.put(TypeEnum.CHAR_32, new CharDataHandlerImpl(" must be char32", 32, TypeEnum.CHAR_32));
        this.dataHandlerMap.put(TypeEnum.CHAR_60, new CharDataHandlerImpl(" must be char60", 60, TypeEnum.CHAR_60));
        this.dataHandlerMap.put(TypeEnum.CHAR_120, new CharDataHandlerImpl(" must be char120", 120, TypeEnum.CHAR_120));
        this.dataHandlerMap.put(TypeEnum.CHAR_255, new CharDataHandlerImpl(" must be char255", 255, TypeEnum.CHAR_255));
        this.dataHandlerMap.put(TypeEnum.CHAR_4000, new CharDataHandlerImpl(" must be char4000", 4000, TypeEnum.CHAR_4000));
        this.dataHandlerMap.put(TypeEnum.IMAGE, new CharDataHandlerImpl(" must less then 200K", 409600, TypeEnum.IMAGE));
    }
    private final Map<TypeEnum, DataHandler> dataHandlerMap = new EnumMap<TypeEnum, DataHandler>(TypeEnum.class);

    @Override
    public DataHandler getDataHandler(TypeEnum dataTypeEnum) {
        return this.dataHandlerMap.get(dataTypeEnum);
    }
}
