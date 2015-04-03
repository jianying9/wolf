package com.wolf.framework.data;

/**
 * 类型处理类
 *
 * @author aladdin
 */
public interface DataHandler {

    /**
     * 返回空字符串代表验证成功,否则会返回错误消息
     * @param value
     * @return 
     */
    public boolean validate(String value);
    
    public String getErrorInfo();

    public DataType getDataType();
}
