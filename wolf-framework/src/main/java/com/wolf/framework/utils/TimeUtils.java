package com.wolf.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间处理辅助类,用于Date类型和String类型时间转换
 *
 * @author aladdin
 */
public final class TimeUtils {

    /**
     * 日志对象
     */
    public final static SimpleDateFormat FM_YYMM = new SimpleDateFormat("yyyyMM");
    public final static SimpleDateFormat FM_YYMMDD = new SimpleDateFormat("yyyyMMdd");
    public final static SimpleDateFormat FM_YY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat FM_YY_MM_DD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static SimpleDateFormat FM_HHMM = new SimpleDateFormat("HH:mm");
    public final static SimpleDateFormat FM_HH = new SimpleDateFormat("HH");

    private TimeUtils() {
    }

    public static String getDateFotmatYYMM() {
        Date currentTime = new Date();
        return TimeUtils.FM_YYMM.format(currentTime);
    }

    /**
     * 获取当前时间yyyyMMdd
     *
     * @return String 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getDateFotmatYYMMDD() {
        Date currentTime = new Date();
        return TimeUtils.FM_YYMMDD.format(currentTime);
    }

    public static long getDateFotmatYYMMDD(long milliseconds) {
        Date currentTime = new Date(milliseconds);
        String date = TimeUtils.FM_YYMMDD.format(currentTime);
        return Long.parseLong(date);
    }

    public static long getYYMMDDnum(String dateStr) {
        long result = 0;
        try {
            Date date = TimeUtils.FM_YY_MM_DD.parse(dateStr);
            String newDateStr = TimeUtils.FM_YYMMDD.format(date);
            result = Long.parseLong(newDateStr);
        } catch (ParseException | NumberFormatException e) {
        }
        return result;
    }

    public static String getYYMMDDstring(long dateNum) {
        String result = "";
        String dateStr = Long.toString(dateNum);
        try {
            Date date = TimeUtils.FM_YYMMDD.parse(dateStr);
            result = TimeUtils.FM_YY_MM_DD.format(date);
        } catch (ParseException | NumberFormatException e) {
        }
        return result;
    }

    /**
     * 获取当前时间yyyy-MM-dd
     *
     * @return String 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getDateFotmatYY_MM_DD() {
        Date currentTime = new Date();
        return TimeUtils.FM_YY_MM_DD.format(currentTime);
    }

    /**
     * 获取当前时间yyyy-MM-dd HH:mm:ss
     *
     * @return String 返回短时间字符串格式yyyy-MM-dd HH:mm:ss
     */
    public static String getDateFotmatYYMMDDHHmmSS() {
        Date currentTime = new Date();
        return TimeUtils.FM_YY_MM_DD_HHMMSS.format(currentTime);
    }

    public static String getDateFotmatHHmm() {
        Date currentTime = new Date();
        return TimeUtils.FM_HHMM.format(currentTime);
    }

    public static String getDateFotmatHH() {
        Date currentTime = new Date();
        return TimeUtils.FM_HH.format(currentTime);
    }

    /**
     * 根据出生日期计算年龄的近似值,最大为127岁,最小为1
     *
     * @param dateOfBirth
     * @return byte
     */
    public static long getAgeByBirth(String dateOfBirth) {
        long age = 0;
        long currentTime = System.currentTimeMillis();
        long birthTime = 0;
        try {
            Date birthDate = TimeUtils.FM_YY_MM_DD.parse(dateOfBirth);
            birthTime = birthDate.getTime();
        } catch (ParseException | NumberFormatException e) {
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
        return TimeUtils.FM_YY_MM_DD_HHMMSS.format(date);
    }

    public static String convertMillisecondToYYYYMMDD(long milliseconds) {
        Date date = new Date(milliseconds);
        return TimeUtils.FM_YY_MM_DD.format(date);
    }

    public static long convertYYYYMMDDToMillisecond(String dateStr) {
        long result = 0;
        try {
            Date date = TimeUtils.FM_YY_MM_DD.parse(dateStr);
            result = date.getTime();
        } catch (ParseException | NumberFormatException e) {
        }
        return result;
    }

    public static long convertYYYYMMDDHHmmSSToMillisecond(String dateStr) {
        long result = 0;
        try {
            Date date = TimeUtils.FM_YY_MM_DD_HHMMSS.parse(dateStr);
            result = date.getTime();
        } catch (ParseException | NumberFormatException e) {
        }
        return result;
    }
}
