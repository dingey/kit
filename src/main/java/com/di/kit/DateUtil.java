package com.di.kit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author di
 */
@SuppressWarnings("all")
public class DateUtil {
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取现在时间
     *
     * @return 当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 获取今天零点时间
     *
     * @return 今天零点时间
     */
    public static Date todayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取今天零点时间
     *
     * @return 今天零点时间
     */
    public static Date todayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 获取指定时间date几(秒、分、小时、天、周、月、年)后的时间
     *
     * @param date     指定时间
     * @param dateType 时间类型
     * @param amount   多少,可负
     * @return 时间
     * @see Calendar.SECOND 秒type
     * @see Calendar.MINUTE 分type
     * @see Calendar.HOUR_OF_DAY 小时type
     * @see Calendar.DAY_OF_MONTH 天type
     * @see Calendar.WEEK_OF_YEAR 周type
     * @see Calendar.MONTH 月type
     * @see Calendar.YEAR 年type
     */
    public static Date add(Date date, int dateType, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(dateType, amount);
        return calendar.getTime();
    }

    /**
     * 获取距当前时间几天后的时间
     *
     * @param days 几天
     * @return 距当前时间几天后的时间
     */
    public static Date addDays(int days) {
        return addDays(now(), days);
    }

    /**
     * 获取指定时间date几天后的时间
     *
     * @param date 指定时间
     * @param days 几天后,如果要几天前，请输入负数
     * @return 指定时间date几天后的时间
     */
    public static Date addDays(Date date, int days) {
        return add(date, Calendar.DAY_OF_MONTH, days);
    }

    /**
     * 获取当前时间几周后的时间
     *
     * @param weeks 几周，负数表示前几周
     * @return 当前时间几周后的时间
     */
    public static Date addWeeks(int weeks) {
        return addWeeks(now(), weeks);
    }

    /**
     * 获取指定时间date几周后的时间
     *
     * @param date  指定时间
     * @param weeks 几周
     * @return 指定时间date几周后的时间
     */
    public static Date addWeeks(Date date, int weeks) {
        return add(date, Calendar.WEEK_OF_YEAR, weeks);
    }

    /**
     * 获取当前时间几月后的时间
     *
     * @param months 几月
     * @return 当前时间几月后的时间
     */
    public static Date addMonths(int months) {
        return addMonths(now(), months);
    }

    /**
     * 获取指定时间date几月后的时间
     *
     * @param date   指定时间
     * @param months 几月
     * @return 指定时间date几月
     */
    public static Date addMonths(Date date, int months) {
        return add(date, Calendar.MONTH, months);
    }

    public static Date addYears(int years) {
        return addYears(now(), years);
    }

    public static Date addYears(Date date, int years) {
        return add(date, Calendar.YEAR, years);
    }

    public static double daysRangeBySecond(Date date1, Date date2) {
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        double between_days = (time2 - time1) / (1000 * 3600 * 24);
        return (double) Math.round(between_days * 10) / 10;
    }

    public static int daysRangeByDay(Date date1, Date date2) {
        Calendar can1 = Calendar.getInstance();
        can1.setTime(date1);
        Calendar can2 = Calendar.getInstance();
        can2.setTime(date2);
        // 拿出两个年份
        int year1 = can1.get(Calendar.YEAR);
        int year2 = can2.get(Calendar.YEAR);
        // 天数
        int days = 0;
        Calendar can;
        // 如果can1 < can2
        // 减去小的时间在这一年已经过了的天数
        // 加上大的时间已过的天数
        if (can1.before(can2)) {
            days -= can1.get(Calendar.DAY_OF_YEAR);
            days += can2.get(Calendar.DAY_OF_YEAR);
            can = can1;
        } else {
            days -= can2.get(Calendar.DAY_OF_YEAR);
            days += can1.get(Calendar.DAY_OF_YEAR);
            can = can2;
        }
        for (int i = 0; i < Math.abs(year2 - year1); i++) {
            // 获取小的时间当前年的总天数
            days += can.getActualMaximum(Calendar.DAY_OF_YEAR);
            // 再计算下一年。
            can.add(Calendar.YEAR, 1);
        }
        return days;
    }

    public static Date addSeconds(int seconds) {
        return new Date(currentTimeMillis() + seconds * 1000);
    }

    public static Date addMinutes(int minutes) {
        return addSeconds(minutes * 60);
    }

    public static Date addHours(int hours) {
        return addMinutes(hours * 60);
    }

    /**
     * 获取本月第一天零点时间
     *
     * @return 零点时间
     */
    public static Date currentMonthStart() {
        return addMonthsStart(0);
    }

    /**
     * 获取本月最后一秒时间
     *
     * @return 本月最后一秒时间
     */
    public static Date currentMonthEnd() {
        return addMonthsEnd(0);
    }

    /**
     * 获取距本月n月后该月第一天零点时间
     *
     * @param months 月数
     * @return 距本月n月后该月第一天零点时间
     */
    public static Date addMonthsStart(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取距本月months月后该月最后一秒时间
     *
     * @param months 月数
     * @return 距本月n月后该月最后一秒时间
     */
    public static Date addMonthsEnd(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, months);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取本月是本年的第几月
     *
     * @return 本月是本年的第月
     */
    public static int currentMonthIndex() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String format = sdf.format(new Date());
        return Integer.valueOf(format);
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    public static long nanoTime() {
        return System.nanoTime();
    }

    public static String format(Date date) {
        return format(date, DATE_TIME_FORMAT);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String date) {
        return parse(date, DATE_TIME_FORMAT);
    }

    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date max(Date date1, Date date2) {
        if (date1 == null)
            return date2;
        if (date2 == null)
            return date1;
        if (date1.after(date2))
            return date1;
        return date2;
    }

    public static Date min(Date date1, Date date2) {
        if (date1 == null)
            return date2;
        if (date2 == null)
            return date1;
        if (date1.before(date2))
            return date1;
        return date2;
    }
}
