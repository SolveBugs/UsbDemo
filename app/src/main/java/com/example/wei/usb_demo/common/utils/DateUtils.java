/*
 * Copyright (c) 2014. Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.utils;

import android.text.format.Time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public final static String yyyyMMddHHmmssSLASH = "yy/MM/dd HH:mm:ss";
    public final static String yyyyYearmmMonthddDay = "yyyy年MM月dd日";
    public final static String yyyyMMddHHmmssGAP = "yyyy-MM-dd HH:mm:ss";
    public final static String yyyyMMddHHmmGAP = "yyyy-MM-dd HH:mm";
    public final static String yyyyMDHHmm = "yyyy年M月d日  HH:mm";
    public final static String yyyyMMdd = "yyyy-MM-dd";
    public final static String yyyyMMGAP = "yyyy-MM";
    public final static String HHmmssCOLON = "HH:mm:ss";
    public final static String MMddGAP = "MM-dd";
    public final static String ddMM = "M/d";
    public final static String HHmmCOLON = "HH:mm";
    public final static String hhmm = "hh:mm";
    public final static String yyyy = "yyyy";
    public final static String MMddDOT = "MM.dd";
    public final static String MMddCHN = "MM月dd日";

    public final static String DATE_FORMAT_yyyyMMdd_DIVIDE_OBLIQUE = "yyyy/MM/dd";
    public final static String DATE_FORMAT_yyyyMM_DIVIDE_OBLIQUE = "yyyy/MM";
    public final static String DATE_FORMAT_yyyyMMdd_DIVIDE_CROSS = "yyyy-MM-dd";
    public final static String DATE_FORMAT_yyyyMM_DIVIDE_CROSS = "yyyy-MM";
    public final static String DATE_FORMAT_MM = "M";

    public final static long MILLIS_ON_MINUTE = 60000;
    public final static long MILLIS_ONE_HOUR = MILLIS_ON_MINUTE * 60;
    public final static long MILLIS_ONE_DAY = MILLIS_ONE_HOUR * 24;


    private DateUtils() {

    }


    public static Date getDateFromyyyyMMddHHmmss(String dateString) throws ParseException {
        if (StringUtils.isEmpty(dateString)) {
            return new Date();
        }
        return new SimpleDateFormat(yyyyMMddHHmmssGAP).parse(dateString);
    }

    /**
     * 根据传入的时间和格式，格式化一个时间. format可以选择本类中的静态变量
     * 后缀含义：GAP:用“-”隔开；CHN:中国化，年月日；SLASH：用“/”分开; COLON：用“：”分开
     *
     * @param date,format
     * @return formatString 返回格式化后的日期字符串
     * @author wdd
     * @date 2014.12.23
     */
    public static String formatDate(Date date, String format) {
        if (date != null && format != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        return "";
    }

    public static String formatDate(long date, String format) {
        return formatDate(new Date(date), format);
    }

    public static Date getDateHour(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 返回当日开始的时间，类型是long
     *
     * @return Today's start,type long
     * @author wdd
     * @date 2014.12.23
     */
    public static long getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    /**
     * 返回当日结束的时间，类型是long
     *
     * @return Today's end,type long
     * @author wdd
     * @date 2014.12.23
     */

    public static long getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime().getTime();
    }


    public static long getThisWeekStartTime() {
        Calendar nowDay = Calendar.getInstance();
        int dayOfWeek = nowDay.get(Calendar.DAY_OF_WEEK);
        nowDay.set(Calendar.HOUR_OF_DAY, 0);
        nowDay.set(Calendar.MINUTE, 0);
        nowDay.set(Calendar.SECOND, 0);
        nowDay.set(Calendar.MILLISECOND, 0);
        if (dayOfWeek != 1) {
            nowDay.add(Calendar.DATE, 2 - dayOfWeek);
        } else {
            nowDay.add(Calendar.DATE, -6);
        }
        return nowDay.getTime().getTime();
    }

    public static long getLastWeekStartTime() {
        return getThisWeekStartTime() - 7 * MILLIS_ONE_DAY;
    }

    public static long get7DayLaunchTime() {
        long now = System.currentTimeMillis();
        now = now + 7 * MILLIS_ONE_DAY;
        return now;
    }

    //获得某天时间的零点
    public static Date getDateZero(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static long getSomeDayEndTime(long mills) {
        return getDate24(mills).getTime();

    }

    public static long getSomeDayStartTime(long mills) {
        return getDateZero(mills).getTime();
    }

    //获得某天时间的零点
    public static Date getDateZero(long date) {
        return getDateZero(new Date(date));
    }


    //获得某一时间距离零点的毫秒数
    public static long getDateDistanceZero(Date date) {
        return date.getTime() - getDateZero(date).getTime();
    }

    //获得某一时间的24点
    public static Date getDate24(long milli) {
        Date date = new Date(milli);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        return calendar.getTime();
    }


    //获得某月结束时间
    public static Date getDateEndMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    //获得某月结束时间
    public static Date getDateEndMonth(long date) {
        return getDateEndMonth(new Date(date));
    }


    //获得某月时间的零点
    public static Date getDateZeroMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //获得某月时间的零点
    public static Date getDateZeroMonth(long date) {
        return getDateZeroMonth(new Date(date));
    }

    //获得指定的时间
    public static Date getAppointDateByTime(Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minute, second);
        return calendar.getTime();
    }

    public static Date getAppointDateByTime(long date, int hour, int minute, int second) {
        return getAppointDateByTime(new Date(date), hour, minute, second);
    }

    public static Date getAppointDateByDate(Date date, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(year, month, day, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        return calendar.getTime();
    }

    public static Date getAppointDateByDate(long date, int year, int month, int day) {
        return getAppointDateByDate(new Date(date), year, month, day);
    }

    public static int getWeekdayIndex() {
        int weekdayIndex = getWeekday() - 2;
        if (weekdayIndex < 0) {
            weekdayIndex = 6;
        }
        return weekdayIndex;
    }

    public static int getWeekdayIndex(long date) {
        int weekdayIndex = getWeekday(date) - 2;
        if (weekdayIndex < 0) {
            weekdayIndex = 6;
        }
        return weekdayIndex;
    }

    public static int getWeekday() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getWeekday(long date) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static Calendar YYYYMMDDString2Calendar(String str) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_yyyyMMdd_DIVIDE_CROSS, Locale.US);
        if (!StringUtils.isEmpty(str)) {
            Date date = null;
            try {
                date = format.parse(str);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            } catch (ParseException e) {
            }
        }
        return null;
    }

    public static String millins2YYYYMMDD(long millins) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_yyyyMMdd_DIVIDE_CROSS, Locale.US);
        Date date = new Date();
        date.setTime(millins);
        try {
            String dateStr = format.format(date);
            return dateStr;
        } catch (Exception e) {
        }
        return "";
    }

    public static String millins2MMdd(long millins) {

        DateFormat format = new SimpleDateFormat(MMddGAP, Locale.US);
        Date date = new Date();
        date.setTime(millins);
        try {
            String dateStr = format.format(date);
            return dateStr;
        } catch (Exception e) {
        }
        return "";
    }

    public static int millis2Age(long millis) {
        if (millis > 0) {
            long nowMill = Calendar.getInstance().getTimeInMillis();
            long path = nowMill / 1000 - millis;
            if (path > 0) {
                long oneYearMillis = 365 * 24 * 60 * 60;
                long ageFloat = path / oneYearMillis;
                int age = (int) ageFloat;
                return age;
            }
        }
        return 0;
    }


    public static String millins2HHMM(long millins) {
        DateFormat format = new SimpleDateFormat(HHmmCOLON, Locale.US);
        Date date = new Date();
        date.setTime(millins);
        try {
            String dateStr = format.format(date);
            return dateStr;
        } catch (Exception e) {
        }
        return "";
    }

    public static long getDelayedTime(long currentTime) {
        currentTime += MILLIS_ON_MINUTE * 10;
        return currentTime;
    }

    public static long getLately7DayMillins() {
        long currentTime = System.currentTimeMillis();
        return currentTime - MILLIS_ONE_DAY * 7;
    }

    public static long changeYearToMills(int year) {
        Time time = new Time("GMT+8");
        time.setToNow();
        int yearNow = time.year;//获取当前年份
        int monthNow = time.month;//获取当前月份
        int dayNow = time.monthDay;//获取当前日

        Calendar nowCalendar = Calendar.getInstance();
        Calendar choiceCalendar = Calendar.getInstance();

        nowCalendar.set(yearNow, monthNow, dayNow, 0, 0, 0);
        choiceCalendar.set(year, monthNow, dayNow, 0, 0, 0);

        Date date = null;
        if (choiceCalendar.getTimeInMillis() > nowCalendar.getTimeInMillis()) {
            date = new Date(nowCalendar.getTimeInMillis());
        } else {
            date = new Date(choiceCalendar.getTimeInMillis());
        }
        return date.getTime() / 1000;
    }

    public static int getYearLong(long mills, boolean floorOrCeil) {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);//获取当前年份
        int monthNow = calendar.get(Calendar.MONTH);//获取当前月份
        int dayNow = calendar.get(Calendar.DATE);//获取当前日

        calendar.setTimeInMillis(mills * 1000);

        int year = yearNow - calendar.get(Calendar.YEAR);
        int month = monthNow - calendar.get(Calendar.MONTH);
        int day = dayNow - calendar.get(Calendar.DATE);
        if (month > 0) {
            return floorOrCeil ? year : year + 1;
        } else if (month == 0) {
            if (day >= 0) {
                return floorOrCeil ? year : year + 1;
            } else {
                return floorOrCeil ? year - 1 : year;
            }
        } else {
            return floorOrCeil ? year - 1 : year;
        }
    }

    public static String printTime(long time) {
        if (time < 10000) {
            return String.valueOf(time);
        } else if (time < 2000000000) {
            return new Date(time * 1000).toLocaleString();
        } else {
            return new Date(time).toLocaleString();

        }
    }

    public final static String yyyyMMddHHmmssfffGAP = "yy/MM/dd HH:mm:ss.SSS";
    private final static SimpleDateFormat sFormatyyyyMMddHHmmssfff = new SimpleDateFormat(yyyyMMddHHmmssfffGAP);


    public static String getTimeStr(int hour, int minute) {
        StringBuilder sBuilder = new StringBuilder();
        if (hour < 10) {
            sBuilder.append("0").append(hour);
        } else {
            sBuilder.append(hour);
        }
        sBuilder.append(":");
        if (minute < 10) {
            sBuilder.append(0).append(minute);
        } else {
            sBuilder.append(minute);
        }
        return sBuilder.toString();
    }


    public static int getYear(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time * 1000);
        return calendar.get(Calendar.YEAR);
    }

    public static Calendar yyyyyMMddStringCalendar(String str) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
        if (!StringUtils.isEmpty(str)) {
            Date date = null;
            try {
                date = format.parse(str);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            } catch (ParseException e) {
            }
        }
        return null;
    }


    public static Date getDateFromString(String dateString, String format) throws ParseException {
        if (StringUtils.isEmpty(dateString)) {
            return new Date();
        }
        return new SimpleDateFormat(format).parse(dateString);
    }

}

