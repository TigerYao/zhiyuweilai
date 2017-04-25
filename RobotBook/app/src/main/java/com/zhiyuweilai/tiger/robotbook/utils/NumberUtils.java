package com.zhiyuweilai.tiger.robotbook.utils;

import android.annotation.SuppressLint;
import android.util.Pair;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class NumberUtils {
    private NumberUtils() {
    }

    public static String sizeToString(long length) {
        Pair result = sizeToStringPair(length);
        return (String)result.first + (String)result.second;
    }

    public static Pair<String, String> sizeToStringPair(long length) {
        int count = 0;

        double size;
        for(size = (double)length; size >= 1024.0D; size /= 1024.0D) {
            ++count;
        }

        String resultNumber;
        String resultUnit;
        switch(count) {
        case 0:
            resultNumber = length + "";
            resultUnit = "B";
            break;
        case 1:
            resultNumber = LocaleUtils.formatStringIgnoreLocale("%.0f", new Object[]{Double.valueOf(size)});
            resultUnit = "KB";
            break;
        case 2:
            resultNumber = LocaleUtils.formatStringIgnoreLocale("%.1f", new Object[]{Double.valueOf(size)});
            resultUnit = "MB";
            break;
        case 3:
            resultNumber = LocaleUtils.formatStringIgnoreLocale("%.2f", new Object[]{Double.valueOf(size)});
            resultUnit = "GB";
            break;
        default:
            resultNumber = length + "";
            resultUnit = "B";
        }

        return Pair.create(resultNumber, resultUnit);
    }

    public static String durationToString(long duration) {
        return duration >= 60000L?duration / 60000L + "\'" + duration % 60000L / 1000L + "\"":duration / 1000L + "\"";
    }

    public static String durationToString2(long duration) {
        duration /= 1000L;
        int hour = (int)(duration / 3600L);
        int minute = (int)((duration - (long)(hour * 3600)) / 60L);
        int second = (int)(duration - (long)(hour * 3600) - (long)(minute * 60));
        return LocaleUtils.formatStringIgnoreLocale("%02d:%02d:%02d", new Object[]{Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second)});
    }

    public static String durationToAdapterString(long duration) {
        if(duration <= 0L) {
            return "00:00";
        } else {
            duration /= 1000L;
            int hour = (int)(duration / 3600L);
            int minute = (int)((duration - (long)(hour * 3600)) / 60L);
            int second = (int)(duration - (long)(hour * 3600) - (long)(minute * 60));
            return hour > 0?LocaleUtils.formatStringIgnoreLocale("%02d:%02d:%02d", new Object[]{Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second)}):LocaleUtils.formatStringIgnoreLocale("%02d:%02d", new Object[]{Integer.valueOf(minute), Integer.valueOf(second)});
        }
    }

    public static String durationToNumString(long duration) {
        if(duration >= 60000L) {
            float sec1 = (float)duration * 1.0F / 60000.0F;
            return sec1 == 0.0F?"1.0":LocaleUtils.formatStringIgnoreLocale("%.1f", new Object[]{Float.valueOf(sec1)});
        } else {
            long sec = duration / 1000L;
            return sec == 0L?"1":String.valueOf(sec);
        }
    }

    public static String durationToUnitString(long duration) {
        return duration >= 60000L?"Min":"Sec";
    }

    public static String getNumberVersionName(String version) {
        StringBuilder builder = new StringBuilder();
        char[] var2 = version.toCharArray();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            char value = var2[var4];
            if((value < 48 || value > 57) && value != 46) {
                break;
            }

            builder.append(value);
        }

        if(builder.length() > 0 && builder.charAt(builder.length() - 1) == 46) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    @SuppressLint({"SimpleDateFormat"})
    public static long parseDateTimeFromString(String dateStr) {
        if(dateStr != null && dateStr.length() != 0) {
            ParsePosition pos = new ParsePosition(0);

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date dateObj = format.parse(dateStr, pos);
                return dateObj != null?dateObj.getTime():-1L;
            } catch (Exception var4) {
                return -1L;
            }
        } else {
            return -1L;
        }
    }

    public static String timeToString(long time) {
        DateFormat format = DateFormat.getDateInstance(2, Locale.getDefault());
        return format.format(new Date(time));
    }

    public static String toPercentString(long numerator, long denominator) {
        return toPercent(numerator, denominator) + "%";
    }

    public static long toPercent(long numerator, long denominator) {
        return denominator == 0L?0L:numerator * 100L / denominator;
    }
}
