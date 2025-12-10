package com.zcpbysj.campusidletrade_server.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 * 提供日期转换、格式化、计算等功能
 */
public class DateUtil {

    // 常用日期格式
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String HH_MM_SS = "HH:mm:ss";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String MM_DD = "MM-dd";

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static long getCurrentTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前日期时间字符串
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * 获取当前日期字符串
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
    }

    /**
     * 获取当前时间字符串
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern(HH_MM_SS));
    }

    /**
     * 时间戳转日期字符串
     */
    public static String timestampToString(long timestamp, String pattern) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), 
            ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 时间戳转日期字符串（默认格式）
     */
    public static String timestampToString(long timestamp) {
        return timestampToString(timestamp, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 日期字符串转时间戳
     */
    public static long stringToTimestamp(String dateStr, String pattern) {
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 日期字符串转时间戳（默认格式）
     */
    public static long stringToTimestamp(String dateStr) {
        return stringToTimestamp(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化日期字符串
     */
    public static String formatDate(String dateStr, String fromPattern, String toPattern) {
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(fromPattern));
        return dateTime.format(DateTimeFormatter.ofPattern(toPattern));
    }

    /**
     * Date转字符串
     */
    public static String dateToString(Date date, String pattern) {
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Date转字符串（默认格式）
     */
    public static String dateToString(Date date) {
        return dateToString(date, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 字符串转Date
     */
    public static Date stringToDate(String dateStr, String pattern) {
        LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 字符串转Date（默认格式）
     */
    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(String startDate, String endDate, String pattern) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(pattern));
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(pattern));
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 计算两个日期之间的天数差（默认格式）
     */
    public static long daysBetween(String startDate, String endDate) {
        return daysBetween(startDate, endDate, YYYY_MM_DD);
    }

    /**
     * 计算两个时间戳之间的天数差
     */
    public static long daysBetween(long startTimestamp, long endTimestamp) {
        LocalDate start = Instant.ofEpochMilli(startTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = Instant.ofEpochMilli(endTimestamp).atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 日期加减天数
     */
    public static String addDays(String dateStr, int days, String pattern) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return date.plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期加减天数（默认格式）
     */
    public static String addDays(String dateStr, int days) {
        return addDays(dateStr, days, YYYY_MM_DD);
    }

    /**
     * 日期加减月数
     */
    public static String addMonths(String dateStr, int months, String pattern) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return date.plusMonths(months).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期加减月数（默认格式）
     */
    public static String addMonths(String dateStr, int months) {
        return addMonths(dateStr, months, YYYY_MM_DD);
    }

    /**
     * 日期加减年数
     */
    public static String addYears(String dateStr, int years, String pattern) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return date.plusYears(years).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期加减年数（默认格式）
     */
    public static String addYears(String dateStr, int years) {
        return addYears(dateStr, years, YYYY_MM_DD);
    }

    /**
     * 获取月份的第一天
     */
    public static String getFirstDayOfMonth(String dateStr, String pattern) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return date.withDayOfMonth(1).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取月份的最后一天
     */
    public static String getLastDayOfMonth(String dateStr, String pattern) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return date.withDayOfMonth(date.lengthOfMonth()).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 判断是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * 获取星期几（1-7，周一到周日）
     */
    public static int getDayOfWeek(String dateStr, String pattern) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
        return date.getDayOfWeek().getValue();
    }

    /**
     * 获取年龄
     */
    public static int getAge(String birthDate, String pattern) {
        LocalDate birth = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern(pattern));
        return Period.between(birth, LocalDate.now()).getYears();
    }

    /**
     * 获取年龄（默认格式）
     */
    public static int getAge(String birthDate) {
        return getAge(birthDate, YYYY_MM_DD);
    }

    /**
     * 验证日期字符串格式是否正确
     */
    public static boolean isValidDate(String dateStr, String pattern) {
        try {
            LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取友好的时间描述（如：刚刚、1分钟前、1小时前等）
     */
    public static String getFriendlyTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        
        if (diff < 60 * 1000) {
            return "刚刚";
        } else if (diff < 60 * 60 * 1000) {
            return (diff / (60 * 1000)) + "分钟前";
        } else if (diff < 24 * 60 * 60 * 1000) {
            return (diff / (60 * 60 * 1000)) + "小时前";
        } else if (diff < 30 * 24 * 60 * 60 * 1000L) {
            return (diff / (24 * 60 * 60 * 1000)) + "天前";
        } else {
            return timestampToString(timestamp, YYYY_MM_DD);
        }
    }
}