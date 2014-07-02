package com.wolf.framework.data;

/**
 * data type
 *
 * @author aladdin
 */
public enum TypeEnum {

    //Int [-2147483648,2147483647]
    INT {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.NUMBER;
        }
    },
    //BigIntSigned [-9223372036854775808,9223372036854775807]
    LONG {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.NUMBER;
        }
    },
    //Double [-1.7976931348623157×10+308, -4.94065645841246544×10-324]
    DOUBLE {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.NUMBER;
        }
    },
    //DateTime {YYYY-MM-DD HH:MM,YYYY-MM-DD HH:MM:SS}
    DATE_TIME {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.DATE;
        }
    },
    //Date {YYYY-MM-DD,YYYY-m-d}
    DATE {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.DATE;
        }
    },
    //Char36 max length 36,可以包含特殊符号
    UUID {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char10 max length 10,可以包含特殊符号
    CHAR_10 {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char32 max length 32,可以包含特殊符号
    CHAR_32 {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char60 max length 60,可以包含特殊符号
    CHAR_60 {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char120 max length 120,可以包含特殊符号
    CHAR_120 {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char255 max length 255,可以包含特殊符号
    CHAR_255 {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //Char4000 max length 4000,可以包含特殊符号
    CHAR_4000 {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    //file max length 409600 dataUrl 最大200k左右图片
    IMAGE {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.STRING;
        }
    },
    BOOLEAN {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.BOOLEAN;
        }
    },
    //json对象
    OBJECT {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.JSON;
        }
    },
    //json数组
    ARRAY {
        @Override
        public DataClassEnum getDataClassEnum() {
            return DataClassEnum.JSON;
        }
    };

    public abstract DataClassEnum getDataClassEnum();
}
