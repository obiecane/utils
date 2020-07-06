package com.ahzak.utils;

import cn.hutool.core.date.DateUnit;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间工具类
 *
 * @author xiaoleilu
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static final String COMMON_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String COMMON_DATE_PATTERN = "yyyy-MM-dd";
    public static final String COMMON_TIME_PATTERN = "HH:mm:ss";

    private static final Map<String, DateTimeFormatter> DATETIME_FORMATTER_MAP;

    static {
        DATETIME_FORMATTER_MAP = new ConcurrentHashMap<>();
        DATETIME_FORMATTER_MAP.put(COMMON_DATE_TIME_PATTERN, DateTimeFormatter.ofPattern(COMMON_DATE_TIME_PATTERN));
        DATETIME_FORMATTER_MAP.put(COMMON_DATE_PATTERN, DateTimeFormatter.ofPattern(COMMON_DATE_PATTERN));
        DATETIME_FORMATTER_MAP.put(COMMON_TIME_PATTERN, DateTimeFormatter.ofPattern(COMMON_TIME_PATTERN));
    }

    public static void main(String[] args) {
//        final String s1 = formatDuration(3721, TimeUnit.SECONDS, "HH:mm:ss");
//        final String s2 = formatDuration(3721, TimeUnit.SECONDS, "HHHH:mm:ss");
//        final String s3 = formatDuration(3721, TimeUnit.SECONDS, "H:mm:ss");
//        final String s34 = formatDuration(3721, TimeUnit.SECONDS, "H:m:s");
//        final String s5 = formatDuration(3721, TimeUnit.SECONDS, "H:mm:s");
//        final String s6 = formatDuration(3721, TimeUnit.SECONDS, "H:m:ss");
//        final String s7 = formatDuration(3721, TimeUnit.SECONDS, "m:ss");
//        final String s8 = formatDuration(3721, TimeUnit.SECONDS, "mm:ss");
//        final String s9 = formatDuration(3721, TimeUnit.SECONDS, "mmmm:s");
//
//        System.out.println(1);


        final LocalDateTime localDateTime211 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 1, 12, 12, 9, 21));
        final LocalDateTime localDateTime322 = beginOfMonthLocalDateTime(LocalDateTime.of(2018, 2, 11, 12, 9, 21));
        final LocalDateTime localDateTime373 = beginOfMonthLocalDateTime(LocalDateTime.of(2000, 2, 11, 12, 9, 21));
        final LocalDateTime localDateTime434 = beginOfMonthLocalDateTime(LocalDateTime.of(2020, 3, 19, 12, 9, 21));
        final LocalDateTime localDateTime535 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 4, 21, 12, 9, 21));
        final LocalDateTime localDateTime636 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 5, 30, 12, 9, 21));
        final LocalDateTime localDateTime647 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 6, 30, 12, 9, 21));
        final LocalDateTime localDateTime738 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 7, 31, 12, 9, 21));
        final LocalDateTime localDateTime719 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 8, 26, 12, 9, 21));
        final LocalDateTime localDateTime720 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 9, 23, 12, 9, 21));
        final LocalDateTime localDateTime741 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 10, 27, 12, 9, 21));
        final LocalDateTime localDateTime752 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 11, 21, 12, 9, 21));
        final LocalDateTime localDateTime763 = beginOfMonthLocalDateTime(LocalDateTime.of(2019, 12, 22, 12, 9, 21));

        final LocalDateTime localDateTime21 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 1, 12, 12, 9, 21));
        final LocalDateTime localDateTime32 = endOfMonthLocalDateTime(LocalDateTime.of(2018, 2, 11, 12, 9, 21));
        final LocalDateTime localDateTime37 = endOfMonthLocalDateTime(LocalDateTime.of(2000, 2, 11, 12, 9, 21));
        final LocalDateTime localDateTime43 = endOfMonthLocalDateTime(LocalDateTime.of(2020, 3, 19, 12, 9, 21));
        final LocalDateTime localDateTime53 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 4, 21, 12, 9, 21));
        final LocalDateTime localDateTime63 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 5, 30, 12, 9, 21));
        final LocalDateTime localDateTime64 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 6, 30, 12, 9, 21));
        final LocalDateTime localDateTime73 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 7, 31, 12, 9, 21));
        final LocalDateTime localDateTime71 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 8, 26, 12, 9, 21));
        final LocalDateTime localDateTime72 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 9, 23, 12, 9, 21));
        final LocalDateTime localDateTime74 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 10, 27, 12, 9, 21));
        final LocalDateTime localDateTime75 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 11, 21, 12, 9, 21));
        final LocalDateTime localDateTime76 = endOfMonthLocalDateTime(LocalDateTime.of(2019, 12, 22, 12, 9, 21));


        final LocalDate localDateTime8 = beginOfWeekLocalDate(LocalDate.of(2020, 4, 28));
        final LocalDate localDateTime9 = endOfWeekLocalDate(LocalDate.of(2020, 4, 28));
        final LocalDate localDateTime81 = beginOfWeekLocalDate(LocalDate.of(2020, 9, 28));
        final LocalDate localDateTime92 = endOfWeekLocalDate(LocalDate.of(2020, 9, 28));

        final LocalDateTime localDateTime10 = beginOfYearLocalDateTime(LocalDateTime.of(2020, 4, 28, 0, 0, 0));
        final LocalDateTime localDateTime11 = endOfYearLocalDateTime(LocalDateTime.of(2020, 4, 28, 0, 0, 0));

        System.out.println(1);


    }


    public static String format(LocalDate date, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        return date.format(formatter);
    }

    public static String format(LocalDate date) {
        return format(date, COMMON_DATE_PATTERN);
    }

    public static String format(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        return dateTime.format(formatter);
    }

    public static String format(LocalDateTime dateTime) {
        return format(dateTime, COMMON_DATE_TIME_PATTERN);
    }

    public static String format(LocalTime time, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        return time.format(formatter);
    }

    public static String format(LocalTime time) {
        return format(time, COMMON_TIME_PATTERN);
    }

    public static String format(Date date) {
        return format(date, COMMON_DATE_TIME_PATTERN);
    }

    public static String formatNow(String format) {
        return format(LocalDateTime.now(), format);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return format(dateTime, COMMON_DATE_TIME_PATTERN);
    }

    public static String formatDate(LocalDate date) {
        return format(date, COMMON_DATE_PATTERN);
    }

    public static String formatDate(LocalDateTime dateTime) {
        return format(dateTime, COMMON_DATE_PATTERN);
    }

    public static String formatTime(LocalTime time) {
        return format(time, COMMON_TIME_PATTERN);
    }

    public static String formatTime(LocalDateTime dateTime) {
        return format(dateTime, COMMON_TIME_PATTERN);
    }

    public static LocalDateTime parseLocalDateTime(CharSequence charSequence, String format) {
        String dateStr = charSequence.toString();
        DateTimeFormatter formatter = getFormatter(format);
        LocalDateTime ldt = LocalDateTime.parse(dateStr, formatter);
        return ldt;
    }

    public static LocalDateTime parseLocalDateTime(CharSequence charSequence) {
        return parseLocalDateTime(charSequence, COMMON_DATE_TIME_PATTERN);
    }

    public static LocalDate parseLocalDate(CharSequence charSequence, String format) {
        String dateStr = charSequence.toString();
        DateTimeFormatter formatter = getFormatter(format);
        LocalDate ld = LocalDate.parse(dateStr, formatter);
        return ld;
    }

    public static LocalDate parseLocalDate(CharSequence charSequence) {
        return parseLocalDate(charSequence, COMMON_DATE_PATTERN);
    }

    public static LocalTime parseLocalTime(CharSequence charSequence, String format) {
        String timeStr = charSequence.toString();
        DateTimeFormatter formatter = getFormatter(format);
        LocalTime lt = LocalTime.parse(timeStr, formatter);
        return lt;
    }

    public static LocalTime parseLocalTime(CharSequence charSequence) {
        return parseLocalTime(charSequence, COMMON_TIME_PATTERN);
    }


    /**
     * 以东8区时区取出指定日期距离1970-01-01T00:00:00Z.的毫秒数<br>
     *
     * @param localDateTime 日期
     * @return 毫秒数
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(zone()).toEpochMilli();
    }


    /**
     * 判断两个日期相差的时长，只保留绝对值
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param unit      相差的单位：相差 天{@link DateUnit#DAY}、小时{@link DateUnit#HOUR} 等
     * @return 日期差
     * @author Zhu Kaixiao
     * @date 2019-10-16
     */
    public static long between(LocalDateTime beginDate, LocalDateTime endDate, DateUnit unit) {
        return between(beginDate, endDate, unit, true);
    }


    /**
     * 判断两个日期相差的时长
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param unit      相差的单位：相差 天{@link DateUnit#DAY}、小时{@link DateUnit#HOUR} 等
     * @param isAbs     日期间隔是否只保留绝对值正数
     * @return 日期差
     * @author Zhu Kaixiao
     * @date 2019-10-16
     */
    public static long between(LocalDateTime beginDate, LocalDateTime endDate, DateUnit unit, boolean isAbs) {
        long beginMilli = toEpochMilli(beginDate);
        long endMilli = toEpochMilli(endDate);
        long diffMilli = endMilli - beginMilli;
        long r = diffMilli / unit.getMillis();

        return isAbs ? Math.abs(r) : r;
    }


    /**
     * 以东8区时区把毫秒时间戳转为日期<br>
     *
     * @param timestamp 毫秒时间戳
     * @return 日期
     */
    public static LocalDateTime localDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zone());
    }


    public static LocalDateTime localDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = zone();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }


    public static boolean isIn(LocalTime time, LocalTime startTime, LocalTime endTime) {
        final long start = startTime.toNanoOfDay();
        final long end = endTime.toNanoOfDay();
        final long specify = time.toNanoOfDay();
        return specify >= start && specify <= end;
    }

    public static boolean isIn(LocalDate date, LocalDate startDate, LocalDate endDate) {
        final long start = startDate.toEpochDay();
        final long end = endDate.toEpochDay();
        final long specify = date.toEpochDay();
        return specify >= start && specify <= end;
    }

    public static boolean isIn(LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        final long start = toEpochMilli(startDateTime);
        final long end = toEpochMilli(endDateTime);
        final long specify = toEpochMilli(dateTime);
        return specify >= start && specify <= end;
    }


    private static ZoneOffset zone() {
        //ZoneOffset.of("+8") 东八区
        //ZoneId.systemDefault() 系统时区
        return ZoneOffset.of("+8");
    }

    private static DateTimeFormatter getFormatter(String format) {
        DateTimeFormatter formatter = DATETIME_FORMATTER_MAP.get(format);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(format);
            DATETIME_FORMATTER_MAP.put(format, formatter);
        }
        return formatter;
    }

    private static Pattern DURATION_FORMAT_PATTERN = Pattern.compile("(H*)([^H]*?)(m+)([^m]*?)(s{1,2})");

    /**
     * 时长格式化, 将时长格式化为时分秒的格式
     * HH:mm:ss
     *
     * @param milli  毫秒
     * @param format 格式  常用: HH:mm:ss, H:m:s, mm:ss, m:s
     *               小时占位符(H)可以省略, 但是分钟占位符(m)和秒占位符(s)不能缺少
     * @return
     */
    public static String formatDuration(long milli, String format) {
        Matcher matcher = DURATION_FORMAT_PATTERN.matcher(format);
        if (matcher.find()) {
            String hFormat = matcher.group(1);
            String sp1 = matcher.group(2);
            String mFormat = matcher.group(3);
            String sp2 = matcher.group(4);
            String sFormat = matcher.group(5);
            if (StringUtils.isNotBlank(mFormat) && StringUtils.isNotBlank(sFormat)) {
                final String mPlaceholder;
                final String sPlaceholder = sFormat.length() == 1 ? "%d" : "%02d";
                // 不需要小时
                if (StringUtils.isBlank(hFormat)) {
                    long m = milli / 1000 / 60;
                    long s = (milli - m * 1000 * 60) / 1000;
                    String mStr = String.valueOf(m);
                    if (mStr.length() < mFormat.length()) {
                        char[] pend = new char[mFormat.length() - mStr.length()];
                        Arrays.fill(pend, '0');
                        mStr = String.copyValueOf(pend) + mStr;

                    }
                    return matcher.replaceAll(String.format("%s" + sp2 + sPlaceholder, mStr, s));
                } else {
                    long h = milli / 1000 / 60 / 60;
                    long m = (milli - h * 1000 * 60 * 60) / 1000 / 60;
                    long s = (milli - h * 1000 * 60 * 60 - m * 1000 * 60) / 1000;
                    String hStr = String.valueOf(h);

                    if (hStr.length() < hFormat.length()) {
                        char[] pend = new char[hFormat.length() - hStr.length()];
                        Arrays.fill(pend, '0');
                        hStr = String.copyValueOf(pend) + hStr;
                    }
                    mPlaceholder = mFormat.length() == 1 ? "%d" : "%02d";
                    return matcher.replaceAll(String.format("%s" + sp1 + mPlaceholder + sp2 + sPlaceholder, hStr, m, s));
                }
            }
        }

        throw new IllegalArgumentException("格式字符串不正确");

    }

    /**
     * 时长格式化, 将时长格式化为时分秒的格式
     *
     * @param duration 时长
     * @param timeUnit 单位
     * @param format   格式
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/12/21 16:19
     */
    public static String formatDuration(long duration, TimeUnit timeUnit, String format) {
        return formatDuration(timeUnit.toMillis(duration), format);
    }


    /**
     * 今日零点
     *
     * @return
     */
    public static LocalDateTime todayZeroTime() {
        return beginOfDayLocalDateTime(LocalDateTime.now());
    }

    /**
     * 今日23:59:59.999999999
     *
     * @return
     */
    public static LocalDateTime todayEndTime() {
        return endOfDayLocalDateTime(LocalDateTime.now());
    }


    /**
     * 获取指定日期起始时间(零时零分零秒)
     * 例如传入的是 2020/04/28 12:15:58, 将返回2020/04/28 00:00:00
     *
     * @return
     */
    public static LocalDateTime beginOfDayLocalDateTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取指定日期起始时间(零时零分零秒)
     * 例如传入的是 2020/04/28, 将返回2020/04/28 00:00:00
     *
     * @return
     */
    public static LocalDateTime beginOfDayLocalDateTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * 获取指定日期结束时间(23:59:59.999999999)
     * 例如传入的是 2020/04/28 12:15:58, 将返回2020/04/28 23:59:59.999999999
     *
     * @return
     */
    public static LocalDateTime endOfDayLocalDateTime(LocalDateTime dateTime) {
        return endOfDayLocalDateTime(dateTime.toLocalDate());
    }

    /**
     * 获取指定日期结束时间(23:59:59.999999999)
     * 例如传入的是 2020/04/28, 将返回2020/04/28 23:59:59.999999999
     *
     * @return
     */
    public static LocalDateTime endOfDayLocalDateTime(LocalDate time) {
        return LocalDateTime.of(time, LocalTime.MAX);
    }

    /**
     * 获取当前月份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfMonthLocalDateTime() {
        return beginOfMonthLocalDateTime(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在月份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfMonthLocalDateTime(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                beginOfMonthLocalDate(dateTime.toLocalDate()),
                LocalTime.MIN
        );
        return firstDay;
    }

    /**
     * 获取当前月份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/01
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfMonthLocalDate() {
        return beginOfMonthLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在月份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/01 00:00:00
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfMonthLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay;
    }


    /**
     * 获取当前日期所在月份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/30 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfMonthLocalDateTime() {
        return endOfMonthLocalDateTime(LocalDateTime.now());
    }

    /**
     * 获取当前日期所在月份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/30
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfMonthLocalDate() {
        return endOfMonthLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在月份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/30 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfMonthLocalDateTime(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                endOfMonthLocalDate(dateTime.toLocalDate()),
                LocalTime.MAX
        );
        return firstDay;
    }

    /**
     * 获取指定日期所在月份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/30
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfMonthLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return firstDay;
    }


    /**
     * 获取当前日期所在年份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/01/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfYearLocalDateTime() {
        return beginOfYearLocalDateTime(LocalDateTime.now());
    }

    /**
     * 获取当前日期所在年份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/01/01
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfYearLocalDate() {
        return beginOfYearLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在年份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/01/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfYearLocalDateTime(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                beginOfYearLocalDate(dateTime.toLocalDate()),
                LocalTime.MIN
        );
        return firstDay;
    }

    /**
     * 获取指定日期所在年份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/01/01
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfYearLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfYear());
        return firstDay;
    }

    /**
     * 获取当前日期所在年份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/12/31 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfYearLocalDateTime() {
        return endOfYearLocalDateTime(LocalDateTime.now());
    }

    /**
     * 获取当前日期所在年份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/12/31
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfYearLocalDate() {
        return endOfYearLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在年份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/12/31 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfYearLocalDateTime(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                endOfYearLocalDate(dateTime.toLocalDate()),
                LocalTime.MAX
        );
        return firstDay;
    }

    /**
     * 获取指定日期所在年份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/12/31
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfYearLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.lastDayOfYear());
        return firstDay;
    }


    /**
     * 获取当前日期所在星期的第一天(星期一)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfWeekLocalDateTime() {
        return beginOfWeekLocalDateTime(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在星期的第一天(星期一)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfWeekLocalDateTime(LocalDateTime date) {
        final LocalDateTime firstDay = LocalDateTime.of(beginOfWeekLocalDate(date.toLocalDate()), LocalTime.MIN);
        return firstDay;
    }

    /**
     * 获取当前日期所在星期的第一天(星期一)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfWeekLocalDate() {
        return beginOfWeekLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在星期的第一天(星期一)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfWeekLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(DayOfWeek.MONDAY);
        return firstDay;
    }

    /**
     * 获取当前日期所在星期最后一天(星期天)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfWeekLocalDate() {
        return beginOfWeekLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在星期最后一天(星期天)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfWeekLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(DayOfWeek.SUNDAY);
        return firstDay;
    }


    /**
     * 获取当前日期所在星期最后一天(星期天)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfWeekLocalDateTime() {
        return endOfWeekLocalDateTime(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在星期最后一天(星期天)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfWeekLocalDateTime(LocalDateTime date) {
        final LocalDateTime firstDay = LocalDateTime.of(endOfWeekLocalDate(date.toLocalDate()), LocalTime.MAX);
        return firstDay;
    }








}
