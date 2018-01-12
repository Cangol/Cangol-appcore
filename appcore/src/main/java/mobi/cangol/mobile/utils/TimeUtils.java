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
    private TimeUtils() {
    }

    /**
     * 得到当前的年份 返回格式:yyyy
     *
     * @return String
     */
    public static String getCurrentYear() {
        Date nowDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(nowDate);
    }

    /**
     * 得到当前的月份 返回格式:MM
     *
     * @return String
     */
    public static String getCurrentMonth() {
        Date nowDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        return formatter.format(nowDate);
    }

    /**
     * 得到当前的日期 返回格式:dd
     *
     * @return String
     */
    public static String getCurrentDay() {
        Date nowDate = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        return formatter.format(nowDate);
    }

    /**
     * 得到当前的时间 返回格式:HH:mm:
     *
     * @return String
     */
    public static String getCurrentHoursMinutes() {
        Date nowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(nowDate);
    }

    /**
     * 得到当前的时间，精确到毫秒,共14位 返回格式:yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String getCurrentTime() {
        Date nowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = formatter.format(nowDate);
        return currentTime;
    }

    /**
     * 得到当前的时间，精确到毫秒,共14位 返回格式:yyyyMMddHHmmss
     *
     * @return String
     */
    public static String getCurrentTime2() {
        Date nowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentTime = formatter.format(nowDate);
        return currentTime;
    }

    /**
     * 转换字符（yyyy-MM-dd）串日期到Date
     *
     * @param date
     * @return
     */
    public static Date convertToDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.format(date);
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
    public static String getCurrentTimeAddYear(int addyear) {
        Date nowDate = new Date();

        String currentYear = TimeUtils.getCurrentYear();
        currentYear = String.valueOf(Integer.parseInt(TimeUtils
                .getCurrentYear()) + addyear);

        SimpleDateFormat formatter = new SimpleDateFormat("-MM-dd:HH:mm:ss");
        String currentTime = formatter.format(nowDate);
        return currentYear + currentTime;
    }

    /**
     * 得到当前的日期,共10位 返回格式：yyyy-MM-dd
     *
     * @return String
     */
    public static String getCurrentDate() {
        Date nowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = formatter.format(nowDate);
        return currentDate;
    }

    /**
     * 得到当前的日期,共8位 返回格式：yyyyMMdd
     *
     * @return String
     */
    public static String getDate8Bit() {
        Date nowDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String currentDate = formatter.format(nowDate);
        return currentDate;
    }

    /**
     * 得到当前日期加上某一个整数的日期，整数代表天数
     *
     * @param currentdate String 格式 yyyy-MM-dd
     * @param add_day     int
     * @return yyyy-MM-dd
     */
    public static String addDay(String currentdate, int add_day) {
        GregorianCalendar gc = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        int year, month, day;

        try {
            year = Integer.parseInt(currentdate.substring(0, 4));
            month = Integer.parseInt(currentdate.substring(5, 7)) - 1;
            day = Integer.parseInt(currentdate.substring(8, 10));

            gc = new GregorianCalendar(year, month, day);
            gc.add(GregorianCalendar.DATE, add_day);

            return formatter.format(gc.getTime());
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
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        try {
            if (df.parse(period) == null) {
                return null;
            }
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        int year = Integer.parseInt(period.substring(0, 4));
        int month = Integer.parseInt(period.substring(5, 7));
        Calendar cl = Calendar.getInstance();
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
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        try {
            if (df.parse(period) == null) {
                return null;
            }
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        int year = Integer.parseInt(period.substring(0, 4));
        int month = Integer.parseInt(period.substring(5, 7));
        Calendar cl = Calendar.getInstance();
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
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMDD");
            try {
                Date date = format.parse(str);
                format = new SimpleDateFormat("yyyy-MM-dd");
                timeStr = format.format(date);
            } catch (ParseException e) {
                Log.d(e.getMessage());
                timeStr = null;
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
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(str);
                format = new SimpleDateFormat("yyyyMMDD");
                timeStr = format.format(date);
            } catch (ParseException e) {
                Log.d(e.getMessage());
                timeStr = null;
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
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = null;
        long time = -1;
        try {
            currentTime = dfs.parse(str);
            time = currentTime.getTime();
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        return time;
    }
    public static String formatDateString(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static String formatTimeString2(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }
    public static String formatTimeString(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            Log.d(e.getMessage());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }
    /**
     * 将long形式改成yyyy-MM-dd HH:mm:ss
     * @param time
     * @param zone
     * @return
     */
    public static String formatYmdHms(long time, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", zone)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成yyyy-MM-dd
     * @param time
     * @param zone
     * @return
     */
    public static String formatYmd(long time, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", zone)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成MM-dd HH:mm:ss
     * @param time
     * @param zone
     *
     * @return
     */
    public static String formatMdHms(long time, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", zone)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成MM-dd HH:mm:ss
     * @param time
     * @param zone
     * @return
     */
    public static String formatHms(long time, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", zone)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成MM-dd HH:mm
     * @param time
     * @param zone
     * @return
     */
    public static String formatHm(long time, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", zone)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成mm:ss
     * @param time
     * @param zone
     * @return
     */
    public static String formatMs(long time, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", zone)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成pattern
     * @param time
     * @param pattern
     * @param zone
     * @return
     */
    public static String format(long time,String pattern, int zone) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成yyyy-MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static String formatYmdHms(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成yyyy-MM-dd
     * @return
     */
    public static String formatYmd(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static String formatMdHms(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成HH:mm:ss
     * @param time
     * @return
     */
    public static String formatHms(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成HH:mm
     * @param time
     * @return
     */
    public static String formatHm(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成mm:ss
     * @param time
     * @return
     */
    public static String formatMs(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
        return formatter.format(new Date(time));
    }
    /**
     * 将long形式改成pattern
     * @param time
     * @param pattern
     * @return
     */
    public static String format(long time,String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone(String.format("GMT+%02d:00", 8)));
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
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            commentTime = dfs.parse(time);
            currentTime = Calendar.getInstance().getTime();
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        long between = (currentTime.getTime() - commentTime.getTime()) / 1000;// 除以1000是为了转换成秒

        long year = between / (24 * 3600 * 30 * 12);
        long month = between / (24 * 3600 * 30);
        long week = between / (24 * 3600 * 7);
        long day = between / (24 * 3600);
        long hour = between % (24 * 3600) / 3600;
        long minute = between % 3600 / 60;
        long second = between % 60 / 60;

        if (year != 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(year).append("年前");
            return sb.toString();
        }
        if (month != 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(month).append("个月前");
            return sb.toString();
        }
        if (week != 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(week).append("周前");
            return sb.toString();
        }
        if (day != 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(day).append("天前");
            return sb.toString();
        }
        if (hour != 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(hour).append("小时前");
            return sb.toString();
        }
        if (minute != 0) {
            StringBuffer sb = new StringBuffer();
            sb.append(minute).append("分钟前");
            return sb.toString();
        }
        if (second != 0) {
            StringBuffer sb = new StringBuffer();
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
        String[] str = time.split(":");
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
        DecimalFormat nf = new DecimalFormat("00");
        return year + "-" + nf.format((monthOfYear + 1)) + "-"
                + nf.format(dayOfMonth);
    }

    /**
     * 获取格式化时间HH:mm
     *
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static String getFormatTime(int hourOfDay, int minute) {
        DecimalFormat nf = new DecimalFormat("00");
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
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = null;
        Date commentTime = null;
        String str = null;
        try {
            currentTime = Calendar.getInstance().getTime();
            commentTime = dfs.parse(strTime);
            if (currentTime.getYear() == commentTime.getYear()
                    && currentTime.getMonth() == commentTime.getMonth()
                    && currentTime.getDate() == commentTime.getDate()) {
                dfs = new SimpleDateFormat("HH:mm");
                str = dfs.format(commentTime);
            } else if (currentTime.getYear() == commentTime.getYear()
                    && currentTime.getMonth() == commentTime.getMonth()
                    && currentTime.getDate() != commentTime.getDate()) {
                dfs = new SimpleDateFormat("MM-dd");
                str = dfs.format(commentTime);
            } else if (currentTime.getYear() == commentTime.getYear()
                    && currentTime.getMonth() != commentTime.getMonth()) {
                dfs = new SimpleDateFormat("yyyy-MM");
                str = dfs.format(commentTime);
            } else {
                dfs = new SimpleDateFormat("yyyy-MM-DD");
                str = dfs.format(commentTime);
            }
        } catch (ParseException e) {
            Log.d(e.getMessage());
            return null;
        }
        return str;
    }
}
