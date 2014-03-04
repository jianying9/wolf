package com.wolf.framework.data;

/**
 * 数据对象工厂
 *
 * @author aladdin
 */
public interface DataHandlerFactory {

    public DataHandler getDataHandler(final BasicTypeEnum dataTypeEnum);
}
