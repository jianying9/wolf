package com.wolf.framework.utils;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;

/**
 * 时间处理辅助类,用于Date类型和String类型时间转换
 *
 * @author aladdin
 */
public final class TimeUtils {

    /**
     * 日志对象
     */
    private final static Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);
    public final static SimpleDateFormat FM_YYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat FM_YYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TimeUtils() {
    }

    /**
     * 获取当前时间yyyy-MM-dd
     *
     * @return String 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getDateFotmatYYMMDD() {
        Date currentTime = new Date();
        return TimeUtils.FM_YYMMDD.format(currentTime);
    }

    /**
     * 获取当前时间yyyy-MM-dd HH:mm:ss
     *
     * @return String 返回短时间字符串格式yyyy-MM-dd HH:mm:ss
     */
    public static String getDateFotmatYYMMDDHHmmSS() {
        Date currentTime = new Date();
        return TimeUtils.FM_YYMMDD_HHMMSS.format(currentTime);
    }

    /**
     * 根据出生日期计算年龄的近似值,最大为127岁,最小为1
     *
     * @param birthDate
     * @return byte
     */
    public static long getAgeByBirth(String dateOfBirth) {
        long age = 0;
        long currentTime = System.currentTimeMillis();
        long birthTime = 0;
        try {
            Date birthDate = TimeUtils.FM_YYMMDD.parse(dateOfBirth);
            birthTime = birthDate.getTime();
        } catch (ParseException e) {
            logger.error(dateOfBirth, e);
        } catch (NumberFormatException ne) {
            logger.error(dateOfBirth, ne);
        }
        if (currentTime > birthTime) {
            long diffetenceDay = (currentTime - birthTime) / 86400000;
            age = diffetenceDay / 365 + 1;
            if (age == 0) {
                age = 1;
            }
        }
        return age;
    }

    public static String convertMillisecondToYYYYMMDDHHmmSS(long milliseconds) {
        Date date = new Date(milliseconds);
        return TimeUtils.FM_YYMMDD_HHMMSS.format(date);
    }

    public static String convertMillisecondToYYYYMMDD(long milliseconds) {
        Date date = new Date(milliseconds);
        return TimeUtils.FM_YYMMDD.format(date);
    }

    public static long convertYYYYMMDDToMillisecond(String dateStr) {
        long result = 0;
        try {
            Date date = TimeUtils.FM_YYMMDD.parse(dateStr);
            result = date.getTime();
        } catch (ParseException e) {
            logger.error(dateStr, e);
        } catch (NumberFormatException ne) {
            logger.error(dateStr, ne);
        }
        return result;
    }
    
    public static long convertYYYYMMDDHHmmSSToMillisecond(String dateStr) {
        long result = 0;
        try {
            Date date = TimeUtils.FM_YYMMDD_HHMMSS.parse(dateStr);
            result = date.getTime();
        } catch (ParseException e) {
            logger.error(dateStr, e);
        } catch (NumberFormatException ne) {
            logger.error(dateStr, ne);
        }
        return result;
    }
}
