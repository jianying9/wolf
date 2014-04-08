package com.wolf.framework.derby;

import com.wolf.framework.data.TypeEnum;

/**
 *
 * @author aladdin
 */
public class AbstractDerbyHandler {

    protected final String SELECT = "SELECT ";
    protected final String FROM = " FROM ";
    protected final String UPDATE = "UPDATE ";
    protected final String INSERT = "INSERT INTO ";
    protected final String DELETE = "DELETE FROM ";
    protected final String VALUES = " VALUES ";
    protected final String WHERE = " WHERE ";
    protected final String SET = " SET ";
    protected final String AND = " AND ";
    protected final String OR = " OR ";
    protected final String IN = " IN ";
    protected final String LIKE = " LIKE ";
    protected final String NOT_LIKE = " NOT LIKE ";
    protected final String EQUAL = "=";
    protected final String NOT_EQUAL = " <> ";
    protected final String GREATER = ">";
    protected final String GREATER_OR_EQUAL = ">=";
    protected final String LESS = "<";
    protected final String LESS_OR_EQUAL = "<=";
    protected final String ASC = " ASC";
    protected final String DESC = " DESC";
    protected final String ORDER_BY = " ORDER BY ";
    protected final String ON = " ON ";
    protected final String COUNT = " COUNT(*) ";
    protected final String CREATE_TABLE = "CREATE TABLE ";
    protected final String KEY = "PRIMARY KEY ";
    protected final String CREATE_INDEX = "CREATE INDEX ";
    protected final String OFFSET = " OFFSET ? ROWS ";
    protected final String OFFSET_PREFIX = " OFFSET ";
    protected final String OFFSET_SURFIX = " ROWS ";
    protected final String FETCH_FIRST = " FETCH FIRST ? ROWS ONLY";
    protected final String FETCH_FIRST_PREFIX = " FETCH FIRST ";
    protected final String FETCH_FIRST_SURFIX= " ROWS ONLY";
    protected final String FETCH_NEXT = " FETCH NEXT ? ROWS ONLY";
    protected final String FETCH_NEXT_PREFIX = " FETCH NEXT ";
    protected final String FETCH_NEXT_SURFIX = " ROWS ONLY";
    //
    protected final String INT = " INT ";
    protected final String BIGINT = " BIGINT ";
    protected final String DOUBLE = " DOUBLE ";
    protected final String VARCHAR10 = " VARCHAR(10) ";
    protected final String VARCHAR32 = " VARCHAR(32) ";
    protected final String VARCHAR60 = " VARCHAR(60) ";
    protected final String VARCHAR120 = " VARCHAR(120) ";
    protected final String VARCHAR255 = " VARCHAR(255) ";
    protected final String VARCHAR4000 = " VARCHAR(4000) ";
    protected final String CHAR36 = " CHAR(36) ";
    
    protected final String getSqlType(TypeEnum dataTypeEnum) {
        String result = "UNDEFINED";
        switch(dataTypeEnum) {
                case INT:
                    result = this.INT;
                    break;
                case LONG:
                    result = this.BIGINT;
                    break;
                case DOUBLE:
                    result = this.DOUBLE;
                    break;
                case DATE:
                    result = this.BIGINT;
                    break;
                case DATE_TIME:
                    result = this.BIGINT;
                    break;
                case UUID:
                    result = this.CHAR36;
                    break;
                case CHAR_10:
                    result = this.VARCHAR10;
                    break;
                case CHAR_32:
                    result = this.VARCHAR32;
                    break;
                case CHAR_60:
                    result = this.VARCHAR60;
                    break;
                case CHAR_120:
                    result = this.VARCHAR120;
                    break;
                case CHAR_255:
                    result = this.VARCHAR255;
                    break;
                case CHAR_4000:
                    result = this.VARCHAR4000;
                    break;
            }
        return result;
    }
}
