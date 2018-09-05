package com.aiitec.openapi.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期操作工具类.
 */

@SuppressLint("SimpleDateFormat")
public class DateUtil {
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE = "yyyy-MM-dd";
    public static final String TIME = "HH:mm";
    private static final String YEAR = "yyyy";
    private static final String MONTH = "MM";
    private static final String DAY = "dd";
    private static final String HOUR = "HH";
    private static final String MINUTE = "mm";
    private static final String SEC = "ss";
    private static Locale defaultLocale = Locale.CHINESE;

    public static Date str2Date(String str) {
        return str2Date(str, null);
    }

    public static Date str2Date(String str, String format) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (format == null || format.length() == 0) {
            format = FORMAT;
        }
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, defaultLocale);
            date = sdf.parse(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;

    }

    public static Calendar str2Calendar(String str) {
        return str2Calendar(str, null);

    }

    public static Calendar str2Calendar(String str, String format) {

        Date date = str2Date(str, format);
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c;

    }

    public static String date2Str(Calendar c) {// yyyy-MM-dd HH:mm:ss
        return date2Str(c, null);
    }

    public static String date2Str(Calendar c, String format) {
        if (c == null) {
            return null;
        }
        return date2Str(c.getTime(), format);
    }

    public static String date2Str(Date d) {// yyyy-MM-dd HH:mm:ss
        return date2Str(d, null);
    }

    public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
        if (d == null) {
            return null;
        }
        if (format == null || format.length() == 0) {
            format = FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, defaultLocale);
        String s = sdf.format(d);
        return s;
    }

    /**
     * 转换时间格式
     *
     * @param time      时间字符串
     * @param inFormat  原来的字符串格式
     * @param outFotmat 希望输出的字符串格式
     * @return
     */
    public static String formatStr(String time, String inFormat, String outFotmat) {
        return date2Str(str2Date(time, inFormat), outFotmat);
    }

    /**
     * 获得当前日期的字符串格式
     *
     * @param format
     * @return
     */
    public static String getCurDateStr(String format) {
        Calendar c = Calendar.getInstance();
        return date2Str(c, format);
    }

    // 格式到天
    public static String getDay(long time) {

        String str = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
        return str;
    }

    /**
     * 获取中文星期几
     *
     * @param c
     * @return
     */
    public static String getChineseWeek(Calendar c) {
        int week = c.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = "天";
                break;

            case 2:
                weekStr = "一";
                break;

            case 3:
                weekStr = "二";
                break;

            case 4:
                weekStr = "三";
                break;

            case 5:
                weekStr = "四";
                break;

            case 6:
                weekStr = "五";
                break;

            case 7:
                weekStr = "六";
                break;

            default:
                break;


        }
        return "星期" + weekStr;

    }


    /**
     * 获取中文周几
     *
     * @param c
     * @return
     */
    public static String getChineseWeek2(Calendar c) {
        int week = c.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = "日";
                break;

            case 2:
                weekStr = "一";
                break;

            case 3:
                weekStr = "二";
                break;

            case 4:
                weekStr = "三";
                break;

            case 5:
                weekStr = "四";
                break;

            case 6:
                weekStr = "五";
                break;

            case 7:
                weekStr = "六";
                break;

            default:
                break;

        }
        return "周" + weekStr;

    }


    /**
     * 获取某天是星期几
     *
     * @param dayTime
     * @return
     */
    public static String getWeek(String dayTime) {
        Date date = str2Date(dayTime, "yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getChineseWeek(calendar);
    }

    /**
     * 获取某天是周几
     */
    public static String getWeekZhou(String dayTime) {
        Date date = str2Date(dayTime, "yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getChineseWeek2(calendar);
    }


    //检查输入的日期格式是否正确
    public static boolean checkDate(String date) {
        String DatePattern = "^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|"
                + "((?:0?[13578]|1[02])-31)))|"
                + "([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|"
                + "(((?:(\\d\\d(?:0[48]|[2468][048]|[13579][26]))|"
                + "(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$";
        Pattern p = Pattern.compile(DatePattern);
        Matcher m = p.matcher(date);
        boolean b = m.matches();
        return b;
    }

    /**
     * 获取当月份月第一天
     */
    public static String getFirstDayOfMonth() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", defaultLocale);
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1);//设为当前月的1 号
        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * 获得当月份最后一天
     */
    public static String getEndDayOfMonth() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", defaultLocale);
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, 1);//加一个月
        lastDate.set(Calendar.DATE, 1);//把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1);//日期回滚一天，也就是本月最后一天
        str = sdf.format(lastDate.getTime());
        return str;
    }


    /**
     * 获取当前年
     */
    public static String getYear() {
        return new SimpleDateFormat(YEAR).format(new Date());
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static String getMonth() {
        return new SimpleDateFormat(MONTH).format(new Date());
    }

    // 获取当前日
    public static String getDay() {
        return new SimpleDateFormat(DAY).format(new Date());
    }

    /**
     * 获取当前时
     */
    public static String getHour() {
        return new SimpleDateFormat(HOUR).format(new Date());
    }

    /**
     * 获取当前分
     */
    public static String getMinute() {
        return new SimpleDateFormat(MINUTE).format(new Date());
    }

    /**
     * 获取当前秒
     */
    public static String getSec() {
        return new SimpleDateFormat(SEC).format(new Date());
    }

    /**
     * 获得明年
     */
    public static String getNextYearFirst() {
        String str = "";
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR);
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.YEAR, 1);
        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * 获取某年某月的最后一天
     */
    public static int getLastDayOfMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
                || month == 10 || month == 12) {
            return 31;
        }
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }
        if (month == 2) {
            if (isLeapYear(year)) {
                return 29;
            } else {
                return 28;
            }
        }
        return 0;
    }

    /**
     * 是否闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static Date timeStamp2Date(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat(DATE, defaultLocale);
        Date date = null;

        try {
            date = format.parse(timeStamp);
        } catch (ParseException var4) {
            var4.printStackTrace();
        }

        return date;
    }


}