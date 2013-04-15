package com.wolf.framework.utils;

import java.text.DecimalFormat;
import java.util.Random;

/**
 *
 * @author aladdin
 */
public final class NumberUtils {

    private NumberUtils() {
    }
    public final static Random RANDOM = new Random();
    public final static DecimalFormat MONEY_FORMAT = new DecimalFormat("#0.00");
    public final static DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.####");

    public static String moneyFormat(double money) {
        return MONEY_FORMAT.format(money);
    }

    public static int getRandomIntegerValue() {
        return RANDOM.nextInt();
    }

    public static int getRandomIntegerValue(int n) {
        return RANDOM.nextInt(n);
    }

    public static long getRandomLongValue() {
        return RANDOM.nextLong();
    }

    public static double getRandomDoubleValue() {
        int value = RANDOM.nextInt();
        double dvalue = RANDOM.nextDouble();
        return dvalue * value;
    }
}
