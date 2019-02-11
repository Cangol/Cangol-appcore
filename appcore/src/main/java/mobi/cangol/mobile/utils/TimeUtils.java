/**
 * Copyright (c) 2013 Cangol
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils;

/**
 * @author Cangol
 */

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import mobi.cangol.mobile.logging.Log;

public class TimeUtils {

    private static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    private static final String YYYYMMDD = "yyyyMMdd";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM = "yyyy-MM";
    private static final String YYYY = "yyyy";
    private static final String MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    private static final String MM_DD = "MM-dd";
    private static final String HH_MM_SS = "HH:mm:ss";
    private static final String HH_MM = "HH:mm";
    private static final String MM_SS = "mm:ss";
    private static final String GMT_02D_00 = "GMT+%02d:00";
    private static final String MM = "MM";
    private static final String DD = "dd";

    private TimeUtils() {
    }

    /**
     * 得到当前的年份 返回格式:yyyy
     *
     * @return String
     */
    public static String getCurrentYear() {
        return new SimpleDateFormat(YYYY).format(new Date());
    }

    /**
     * 得到当前的月份 返回格式:MM
     *
     * @return String
     */
    public static String getCurrentMonth() {
        return new SimpleDateFormat(MM).format(new Date());
    }

    /**
     * 得到当前的日期 返回格式:dd
     *
     * @return String
     */
    public static String getCurrentDay() {
        return new SimpleDateFormat(DD).format(new Date());
    }

    /**
     * 得到当前的时间 返回格式:HH:mm:
     *
     * @return String
     */
    public static String getCurrentHoursMinutes() {
        return new SimpleDateFormat(HH_MM).format(new Date());
    }

    /**
     * 得到当前的时间，精确到毫秒,共14位 返回格式:yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(new Date());
    }

    /**
     * 得到当前的时间，精确到毫秒,共14位 返回格式:yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrentTime2() {
        return new SimpleDateFormat(YYYYMMDDHHMMSS).format(new Date());
    }

    /**
     * 转换字符（yyyy-MM-dd）串日期到Date
     *
     * @param date
     * @return
     */
    public static Date convertToDate(String date) {
        try {
            return new SimpleDateFormat(YYYY_MM_DD).parse(date);
        } catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        }
    }

    /**
     * 转换日期到字符换yyyy-MM-dd
     *
     * @param date
     * @return
     */
    public static String convertToString(Date date) {
        try {
            return new SimpleDateFormat(YYYY_MM_DD).format(date);
        } catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        }
    }

    /**
     * 得到当前的时间加上输入年后的时间，精确到毫秒,共19位 返回格式:yyyy-MM-dd:HH:mm:ss
     *
     * @return String
     */
    public static String getCurrentTimeAddYear(int addYear) {
        return String.valueOf(Integer.parseInt(getCurrentYear()) + addYear) + new SimpleDateFormat("-MM-dd:HH:mm:ss").format(new Date());
    }

    /**
     * 得到当前的日期,共10位 返回格式：yyyy-MM-dd
     *
     * @return String
     */
    public static String getCurrentDate() {
        return new SimpleDateFormat(YYYY_MM_DD).format(new Date());
    }

    /**
     * 得到当前的日期,共8位 返回格式：yyyyMMdd
     *
     * @return String
     */
    public static String getDate8Bit() {
        return new SimpleDateFormat(YYYYMMDD).format(new Date());
    }

    /**
     * 得到当前日期加上某一个整数的日期，整数代表天数
     *
     * @param currentDate String 格式 yyyy-MM-dd
     * @param addDay      int
     * @return yyyy-MM-dd
     */
    public static String addDay(String currentDate, int addDay) {
        GregorianCalendar gc;
        int year;
        int month;
        int day;

        try {
            year = Integer.parseInt(currentDate.substring(0, 4));
            month = Integer.parseInt(currentDate.substring(5, 7)) - 1;
            day = Integer.parseInt(currentDate.substring(8, 10));

            gc = new GregorianCalendar(year, month, day);
            gc.add(GregorianCalendar.DATE, addDay);

            return new SimpleDateFormat(YYYY_MM_DD).format(gc.getTime());
        } catch (Exception e) {
            Log.d(e.getMessage());
            return null;
        }
    }

    /**
     * 得到当前月份的第一天日期
     *
     * @param period yyyy-MM
     */
    public static String getStartDateInPeriod(String period) {
        final DateFormat df = new SimpleDateFormat(YYYY_MM);
        try {
            if (df.parse(period) == null) {
                return null;
            }
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        final int year = Integer.parseInt(period.substring(0, 4));
        final int month = Integer.parseInt(period.substring(5, 7));
        final Calendar cl = Calendar.getInstance();
        cl.set(year, month - 1, 1);
        return df.format(cl.getTime());

    }

    /**
     * 得到当前月份的最后一天
     *
     * @param period yyyy-MM
     * @return
     */
    public static String getEndDateInPeriod(String period) {
        final DateFormat df = new SimpleDateFormat(YYYY_MM);
        try {
            if (df.parse(period) == null) {
                return null;
            }
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        final int year = Integer.parseInt(period.substring(0, 4));
        final int month = Integer.parseInt(period.substring(5, 7));
        final Calendar cl = Calendar.getInstance();
        cl.set(year, month - 1, 1);
        cl.add(Calendar.MONTH, 1);
        cl.add(Calendar.DATE, -1);
        return df.format(cl.getTime());
    }

    /**
     * 将YYYYMMDD形式改成YYYY-MM-DD
     *
     * @param str
     * @return
     */
    public static String convertStandard(String str) {
        String timeStr = null;
        if (str == null || str.equals("")) {
            timeStr = null;
        } else {
            try {
                final Date date = new SimpleDateFormat(YYYYMMDD).parse(str);
                timeStr = new SimpleDateFormat(YYYY_MM_DD).format(date);
            } catch (ParseException e) {
                Log.d(e.getMessage());
            }
        }
        return timeStr;
    }

    /**
     * 将YYYY-MM-DD形式改成YYYYMMDD
     *
     * @param str
     * @return
     */
    public static String convert8Bit(String str) {
        String timeStr = null;
        if (str == null || str.equals("")) {
            timeStr = null;
        } else {
            try {
                final Date date = new SimpleDateFormat(YYYY_MM_DD).parse(str);
                timeStr = new SimpleDateFormat(YYYYMMDD).format(date);
            } catch (ParseException e) {
                Log.d(e.getMessage());
            }
        }
        return timeStr;
    }

    /**
     * 转换时间yyyy-MM-dd HH:mm:ss到毫秒数
     *
     * @param str
     * @return
     */
    public static long convertLong(String str) {
        Date currentTime = null;
        long time = -1;
        try {
            currentTime = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(str);
            time = currentTime.getTime();
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        return time;
    }

    public static String formatDateString(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time);
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        return new SimpleDateFormat(YYYY_MM_DD).format(date);
    }

    public static String formatTimeString2(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time);
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        return new SimpleDateFormat(HH_MM_SS).format(date);
    }

    public static String formatTimeString(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time);
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        return new SimpleDateFormat(HH_MM).format(date);
    }

    /**
     * 将long形式改成yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    public static String formatYmdHms(long time, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成yyyy-MM-dd
     *
     * @param time
     * @param zone
     * @return
     */
    public static String formatYmd(long time, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成MM-dd HH:mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    public static String formatMdHms(long time, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(MM_DD_HH_MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成MM-dd HH:mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    public static String formatHms(long time, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(HH_MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成MM-dd HH:mm
     *
     * @param time
     * @param zone
     * @return
     */
    public static String formatHm(long time, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(HH_MM);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    public static String formatMs(long time, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成pattern
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String format(long time, String pattern, int zone) {
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, zone)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String formatYmdHms(long time) {
        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成yyyy-MM-dd
     *
     * @return
     */
    public static String formatYmd(long time) {
        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String formatMdHms(long time) {
        final SimpleDateFormat formatter = new SimpleDateFormat(MM_DD_HH_MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String formatHms(long time) {
        final SimpleDateFormat formatter = new SimpleDateFormat(HH_MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成HH:mm
     *
     * @param time
     * @return
     */
    public static String formatHm(long time) {
        final SimpleDateFormat formatter = new SimpleDateFormat(HH_MM);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成mm:ss
     *
     * @param time
     * @return
     */
    public static String formatMs(long time) {
        final SimpleDateFormat formatter = new SimpleDateFormat(MM_SS);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 将long形式改成pattern
     *
     * @param time
     * @param pattern
     * @return
     */
    public static String format(long time, String pattern) {
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format(GMT_02D_00, 8)));
        return formatter.format(new Date(time));
    }

    /**
     * 计算距今的时间
     *
     * @param time
     * @return
     */
    public static String formatRecentTime(String time) {
        if (null == time || "".equals(time)) {
            return "";
        }
        Date commentTime = null;
        Date currentTime = null;
        try {
            commentTime = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time);
            currentTime = Calendar.getInstance().getTime();
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        final long between = (currentTime.getTime() - commentTime.getTime()) / 1000;// 除以1000是为了转换成秒

        final long year = between / (24 * 3600 * 30 * 12);
        final long month = between / (24 * 3600 * 30);
        final long week = between / (24 * 3600 * 7);
        final long day = between / (24 * 3600);
        final long hour = between % (24 * 3600) / 3600;
        final long minute = between % 3600 / 60;
        final long second = between % 60 / 60;

        if (year != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(year).append("年前");
            return sb.toString();
        }
        if (month != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(month).append("个月前");
            return sb.toString();
        }
        if (week != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(week).append("周前");
            return sb.toString();
        }
        if (day != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(day).append("天前");
            return sb.toString();
        }
        if (hour != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(hour).append("小时前");
            return sb.toString();
        }
        if (minute != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(minute).append("分钟前");
            return sb.toString();
        }
        if (second != 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(second).append("秒前");
            return sb.toString();
        }

        return "";
    }

    /**
     * 转化为中文时间格式
     *
     * @param time HH:mm:ss
     * @return
     */
    public static String getZhTimeString(String time) {
        final String[] str = time.split(":");
        if (str.length == 3) {
            return Integer.valueOf(str[0]) + "小时" + Integer.valueOf(str[1])
                    + "分" + Integer.valueOf(str[2]) + "秒";
        } else if (str.length == 2) {
            return Integer.valueOf(str[0]) + "分" + Integer.valueOf(str[1])
                    + "秒";
        } else {
            return Integer.valueOf(str[0]) + "秒";
        }
    }

    /**
     * 获取格式化日期yyyy-MM-dd
     *
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     * @return
     */
    public static String getFormatDate(int year, int monthOfYear, int dayOfMonth) {
        final DecimalFormat nf = new DecimalFormat("00");
        return year + "-" + nf.format((monthOfYear + 1)) + "-"+ nf.format(dayOfMonth);
    }

    /**
     * 获取格式化时间HH:mm
     *
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static String getFormatTime(int hourOfDay, int minute) {
        final DecimalFormat nf = new DecimalFormat("00");
        return nf.format((hourOfDay)) + ":" + nf.format(minute);
    }

    /**
     * 格式化为应用 常见显示格式 当前天显示时间，其他显示年月日
     *
     * @param strTime yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatLatelyTime(String strTime) {
        if (null == strTime || "".equals(strTime)) {
            return "";
        }
        String str = null;
        Calendar currentCalendar;
        Calendar commentCalendar;
        try {
            currentCalendar = Calendar.getInstance();
            commentCalendar = Calendar.getInstance();
            commentCalendar.setTimeInMillis(new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(strTime).getTime());

            if (currentCalendar.get(Calendar.YEAR) == commentCalendar.get(Calendar.YEAR)
                    && currentCalendar.get(Calendar.MONTH) == commentCalendar.get(Calendar.MONTH)
                    && currentCalendar.get(Calendar.DAY_OF_YEAR) == commentCalendar.get(Calendar.DAY_OF_YEAR)) {
                str = new SimpleDateFormat(HH_MM).format(commentCalendar.getTime());
            } else if (currentCalendar.get(Calendar.YEAR) == commentCalendar.get(Calendar.YEAR)
                    && currentCalendar.get(Calendar.MONTH) == commentCalendar.get(Calendar.MONTH)
                    && currentCalendar.get(Calendar.DAY_OF_YEAR) != commentCalendar.get(Calendar.DAY_OF_YEAR)) {
                str = new SimpleDateFormat(MM_DD).format(commentCalendar.getTime());
            } else if (currentCalendar.get(Calendar.YEAR) == commentCalendar.get(Calendar.YEAR)
                    && currentCalendar.get(Calendar.MONTH) == commentCalendar.get(Calendar.MONTH)) {
                str = new SimpleDateFormat(YYYY_MM).format(commentCalendar.getTime());
            } else {
                str = new SimpleDateFormat(YYYY_MM_DD).format(commentCalendar.getTime());
            }
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        return str;
    }

    /**
     * 判断是同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameDay(long time1, long time2) {
        final Calendar calDateA = Calendar.getInstance();
        calDateA.setTimeInMillis(time1);

        final Calendar calDateB = Calendar.getInstance();
        calDateB.setTimeInMillis(time2);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
                .get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断是同一月
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameMonth(long time1, long time2) {
        final Calendar time1Ca = Calendar.getInstance();
        time1Ca.setFirstDayOfWeek(Calendar.MONDAY);
        time1Ca.setTimeInMillis(time1);
        final Calendar time2Ca = Calendar.getInstance();
        time2Ca.setFirstDayOfWeek(Calendar.MONDAY);
        time2Ca.setTimeInMillis(time2);
        return time1Ca.get(Calendar.YEAR) == time2Ca.get(Calendar.YEAR)
                && time1Ca.get(Calendar.MONTH) == time2Ca.get(Calendar.MONTH);
    }

    /**
     * 判断是同一周
     *
     * @param time1
     * @param time2
     * @return
     */
    public static boolean isSameWeek(long time1, long time2) {
        final Calendar time1Ca = Calendar.getInstance();
        time1Ca.setFirstDayOfWeek(Calendar.MONDAY);
        time1Ca.setTimeInMillis(time1);
        final Calendar time2Ca = Calendar.getInstance();
        time2Ca.setFirstDayOfWeek(Calendar.MONDAY);
        time2Ca.setTimeInMillis(time2);
        return time1Ca.get(Calendar.WEEK_OF_YEAR) == time2Ca.get(Calendar.WEEK_OF_YEAR);
    }
}
