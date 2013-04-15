package com.wolf.framework.data;

/**
 * data type
 *
 * @author aladdin
 */
public enum DataTypeEnum {

    //Int [-2147483648,2147483647]
    INT {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.NUMBER;
        }
    },
    //BigIntSigned [-9223372036854775808,9223372036854775807]
    LONG {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.NUMBER;
        }
    },
    //Double [-1.7976931348623157×10+308, -4.94065645841246544×10-324]
    DOUBLE {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.NUMBER;
        }
    },
    //DateTime {YYYY-MM-DD HH:MM,YYYY-MM-DD HH:MM:SS}
    DATE_TIME {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.DATE;
        }
    },
    //Date {YYYY-MM-DD,YYYY-m-d}
    DATE {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.DATE;
        }
    },
    //Char36 max length 36,可以包含特殊符号
    UUID {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char10 max length 10,可以包含特殊符号
    CHAR_10 {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char32 max length 32,可以包含特殊符号
    CHAR_32 {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char60 max length 60,可以包含特殊符号
    CHAR_60 {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char120 max length 120,可以包含特殊符号
    CHAR_120 {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char255 max length 255,可以包含特殊符号
    CHAR_255 {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char4000 max length 4000,可以包含特殊符号
    CHAR_4000 {
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    };

    public abstract DataClassEnum getDataClassEnum();
}
