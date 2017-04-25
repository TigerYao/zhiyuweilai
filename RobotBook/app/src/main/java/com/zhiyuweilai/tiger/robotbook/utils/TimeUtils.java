package com.zhiyuweilai.tiger.robotbook.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtils {
    public TimeUtils() {
    }

    public static boolean isBeforeDate(int year, int month, int day) {
        Calendar deadline = Calendar.getInstance();
        deadline.set(year, month - 1, day, 0, 0, 0);
        Calendar now = Calendar.getInstance();
        return now.before(deadline);
    }

    public static boolean isBeforeTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar deadline = Calendar.getInstance();
        deadline.set(year, month - 1, day, hour, minute, second);
        Calendar now = Calendar.getInstance();
        return now.before(deadline);
    }

    public static boolean isAfterDate(int year, int month, int day) {
        Calendar deadline = Calendar.getInstance();
        deadline.set(year, month - 1, day, 0, 0, 0);
        Calendar now = Calendar.getInstance();
        return now.after(deadline);
    }

    public static boolean isAfterTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar deadline = Calendar.getInstance();
        deadline.set(year, month - 1, day, hour, minute, second);
        Calendar now = Calendar.getInstance();
        return now.after(deadline);
    }

    public static boolean isUnreached(long startTime) {
        return startTime != -1L && System.currentTimeMillis() < startTime;
    }

    public static boolean isExpired(long endTime) {
        return endTime != -1L && System.currentTimeMillis() > endTime;
    }

    public static boolean isExpired(long endTime, long overTime) {
        return endTime != -1L && System.currentTimeMillis() > endTime + overTime;
    }

    public static boolean isSameDay(long time) {
        return isSameDay(System.currentTimeMillis(), time);
    }

    public static boolean isSameDay(long time1, long time2) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String time1Str = sf.format(Long.valueOf(time1));
        String time2Str = sf.format(Long.valueOf(time2));
        return time1Str.equals(time2Str);
    }

    public static String getFormatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(new Date(time));
    }

    public static String getFormatDate(String formater, long time) {
        SimpleDateFormat format = new SimpleDateFormat(formater, Locale.getDefault());
        return format.format(new Date(time));
    }
}