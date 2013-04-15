package com.wolf.framework.data;

/**
 * 类型处理类
 *
 * @author aladdin
 */
public interface DataHandler {

    public String DEFAULT_CHAR_VALUE = "";
    public String DEFAULT_NUMBER_VALUE = "-1";
    public String DEFAULT_DECIMAL_VALUE = "0";
    public String DEFAULT_DATE_VALUE = "0";

    /**
     * 返回空字符串代表验证成功,否则会返回错误消息
     * @param value
     * @return 
     */
    public String validate(String value);

    public String getDefaultValue();

    public String getRandomValue();
    
    public String getNextValue();
    
    public DataTypeEnum getDataTypeEnum();
    
    public String convertToInput(String value);
    
    public String convertToOutput(String value);
}
