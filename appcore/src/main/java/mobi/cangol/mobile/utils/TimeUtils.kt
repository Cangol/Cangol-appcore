/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.utils

/**
 * @author Cangol
 */

import mobi.cangol.mobile.logging.Log
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    private const val YYYYMMDDHHMMSS = "yyyyMMddHHmmss"
    private const val YYYYMMDD = "yyyyMMdd"
    private const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    private const val YYYY_MM_DD = "yyyy-MM-dd"
    private const val YYYY_MM = "yyyy-MM"
    private const val YYYY = "yyyy"
    private const val MM_DD_HH_MM_SS = "MM-dd HH:mm:ss"
    private const val MM_DD = "MM-dd"
    private const val HH_MM_SS = "HH:mm:ss"
    private const val HH_MM = "HH:mm"
    private const val MM_SS = "mm:ss"
    private const val GMT_02D_00 = "GMT+%02d:00"
    private const val MM = "MM"
    private const val DD = "dd"

    /**
     * 得到当前的年份 返回格式:yyyy
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentYear(): String {
        return SimpleDateFormat(YYYY).format(Date())
    }

    /**
     * 得到当前的月份 返回格式:MM
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentMonth(): String {
        return SimpleDateFormat(MM).format(Date())
    }

    /**
     * 得到当前的日期 返回格式:dd
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentDay(): String {
        return SimpleDateFormat(DD).format(Date())
    }

    /**
     * 得到当前的时间 返回格式:HH:mm:
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentHoursMinutes(): String {
        return SimpleDateFormat(HH_MM).format(Date())
    }

    /**
     * 得到当前的时间，精确到毫秒,共14位 返回格式:yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentTime(): String {
        return SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(Date())
    }

    /**
     * 得到当前的时间，精确到毫秒,共14位 返回格式:yyyyMMddHHmmss
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentTime2(): String {
        return SimpleDateFormat(YYYYMMDDHHMMSS).format(Date())
    }

    /**
     * 得到当前的日期,共10位 返回格式：yyyy-MM-dd
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentDate(): String {
        return SimpleDateFormat(YYYY_MM_DD).format(Date())
    }

    /**
     * 得到当前的日期,共8位 返回格式：yyyyMMdd
     *
     * @return String
     */
    @JvmStatic
    fun getDate8Bit(): String {
        return SimpleDateFormat(YYYYMMDD).format(Date())
    }

    /**
     * 转换字符（yyyy-MM-dd）串日期到Date
     *
     * @param date
     * @return
     */
    @JvmStatic
    fun convertToDate(date: String): Date? {
        return try {
            SimpleDateFormat(YYYY_MM_DD).parse(date)
        } catch (e: Exception) {
            Log.d(e.message)
            null
        }

    }

    /**
     * 转换日期到字符换yyyy-MM-dd
     *
     * @param date
     * @return
     */
    @JvmStatic
    fun convertToString(date: Date): String? {
        return try {
            SimpleDateFormat(YYYY_MM_DD).format(date)
        } catch (e: Exception) {
            Log.d(e.message)
            null
        }

    }

    /**
     * 得到当前的时间加上输入年后的时间，精确到毫秒,共19位 返回格式:yyyy-MM-dd:HH:mm:ss
     *
     * @return String
     */
    @JvmStatic
    fun getCurrentTimeAddYear(addYear: Int): String {
        return (Integer.parseInt(getCurrentYear()) + addYear).toString() + SimpleDateFormat("-MM-dd:HH:mm:ss").format(Date())
    }

    /**
     * 得到当前日期加上某一个整数的日期，整数代表天数
     *
     * @param currentDate String 格式 yyyy-MM-dd
     * @param addDay      int
     * @return yyyy-MM-dd
     */
    @JvmStatic
    fun addDay(currentDate: String, addDay: Int): String? {
        val gc: GregorianCalendar
        val year: Int
        val month: Int
        val day: Int

        return try {
            year = Integer.parseInt(currentDate.substring(0, 4))
            month = Integer.parseInt(currentDate.substring(5, 7)) - 1
            day = Integer.parseInt(currentDate.substring(8, 10))

            gc = GregorianCalendar(year, month, day)
            gc.add(Calendar.DATE, addDay)

            SimpleDateFormat(YYYY_MM_DD).format(gc.time)
        } catch (e: Exception) {
            Log.d(e.message)
            null
        }

    }

    /**
     * 得到当前月份的第一天日期
     *
     * @param period yyyy-MM
     */
    @JvmStatic
    fun getStartDateInPeriod(period: String): String? {
        val df = SimpleDateFormat(YYYY_MM)
        try {
            if (df.parse(period) == null) {
                return null
            }
        } catch (e: ParseException) {
            Log.d(e.message)
            return null
        }

        val year = Integer.parseInt(period.substring(0, 4))
        val month = Integer.parseInt(period.substring(5, 7))
        val cl = Calendar.getInstance()
        cl.set(year, month - 1, 1)
        return df.format(cl.time)

    }

    /**
     * 得到当前月份的最后一天
     *
     * @param period yyyy-MM
     * @return
     */
    @JvmStatic
    fun getEndDateInPeriod(period: String): String? {
        val df = SimpleDateFormat(YYYY_MM)
        try {
            if (df.parse(period) == null) {
                return null
            }
        } catch (e: ParseException) {
            Log.d(e.message)
            return null
        }

        val year = Integer.parseInt(period.substring(0, 4))
        val month = Integer.parseInt(period.substring(5, 7))
        val cl = Calendar.getInstance()
        cl.set(year, month - 1, 1)
        cl.add(Calendar.MONTH, 1)
        cl.add(Calendar.DATE, -1)
        return df.format(cl.time)
    }

    /**
     * 将YYYYMMDD形式改成YYYY-MM-DD
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun convertStandard(str: String?): String? {
        var timeStr: String? = null
        if (str != null && str != "") {
            try {
                val date = SimpleDateFormat(YYYYMMDD).parse(str)
                timeStr = SimpleDateFormat(YYYY_MM_DD).format(date)
            } catch (e: ParseException) {
                Log.d(e.message)
            }

        }
        return timeStr
    }

    /**
     * 将YYYY-MM-DD形式改成YYYYMMDD
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun convert8Bit(str: String?): String? {
        var timeStr: String? = null
        if (str != null && str != "") {
            try {
                val date = SimpleDateFormat(YYYY_MM_DD).parse(str)
                timeStr = SimpleDateFormat(YYYYMMDD).format(date)
            } catch (e: ParseException) {
                Log.d(e.message)
            }

        }
        return timeStr
    }

    /**
     * 转换时间yyyy-MM-dd HH:mm:ss到毫秒数
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun convertLong(str: String): Long {
        var currentTime: Date?
        var time: Long = -1
        try {
            currentTime = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(str)
            time = currentTime!!.time
        } catch (e: ParseException) {
            Log.d(e.message)
        }

        return time
    }

    @JvmStatic
    fun formatDateString(time: String): String {
        var date: Date? = null
        try {
            date = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time)
        } catch (e: ParseException) {
            Log.d(e.message)
        }

        return SimpleDateFormat(YYYY_MM_DD).format(date)
    }

    @JvmStatic
    fun formatTimeString2(time: String): String {
        var date: Date? = null
        try {
            date = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time)
        } catch (e: ParseException) {
            Log.d(e.message)
        }

        return SimpleDateFormat(HH_MM_SS).format(date)
    }

    @JvmStatic
    fun formatTimeString(time: String): String {
        var date: Date? = null
        try {
            date = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time)
        } catch (e: ParseException) {
            Log.d(e.message)
        }

        return SimpleDateFormat(HH_MM).format(date)
    }

    /**
     * 将long形式改成yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    @JvmStatic
    fun formatYmdHms(time: Long, zone: Int): String {
        val formatter = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成yyyy-MM-dd
     *
     * @param time
     * @param zone
     * @return
     */
    @JvmStatic
    fun formatYmd(time: Long, zone: Int): String {
        val formatter = SimpleDateFormat(YYYY_MM_DD)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成MM-dd HH:mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    @JvmStatic
    fun formatMdHms(time: Long, zone: Int): String {
        val formatter = SimpleDateFormat(MM_DD_HH_MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成MM-dd HH:mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    @JvmStatic
    fun formatHms(time: Long, zone: Int): String {
        val formatter = SimpleDateFormat(HH_MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成MM-dd HH:mm
     *
     * @param time
     * @param zone
     * @return
     */
    @JvmStatic
    fun formatHm(time: Long, zone: Int): String {
        val formatter = SimpleDateFormat(HH_MM)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成mm:ss
     *
     * @param time
     * @param zone
     * @return
     */
    @JvmStatic
    fun formatMs(time: Long, zone: Int): String {
        val formatter = SimpleDateFormat(MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成pattern
     *
     * @param time
     * @param pattern
     * @return
     */
    @JvmStatic
    fun format(time: Long, pattern: String, zone: Int): String {
        val formatter = SimpleDateFormat(pattern)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, zone))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    @JvmStatic
    fun formatYmdHms(time: Long): String {
        val formatter = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成yyyy-MM-dd
     *
     * @return
     */
    @JvmStatic
    fun formatYmd(time: Long): String {
        val formatter = SimpleDateFormat(YYYY_MM_DD)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    @JvmStatic
    fun formatMdHms(time: Long): String {
        val formatter = SimpleDateFormat(MM_DD_HH_MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成HH:mm:ss
     *
     * @param time
     * @return
     */
    @JvmStatic
    fun formatHms(time: Long): String {
        val formatter = SimpleDateFormat(HH_MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成HH:mm
     *
     * @param time
     * @return
     */
    @JvmStatic
    fun formatHm(time: Long): String {
        val formatter = SimpleDateFormat(HH_MM)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成mm:ss
     *
     * @param time
     * @return
     */
    @JvmStatic
    fun formatMs(time: Long): String {
        val formatter = SimpleDateFormat(MM_SS)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 将long形式改成pattern
     *
     * @param time
     * @param pattern
     * @return
     */
    @JvmStatic
    fun format(time: Long, pattern: String): String {
        val formatter = SimpleDateFormat(pattern)
        formatter.timeZone = TimeZone.getTimeZone(String.format(GMT_02D_00, 8))
        return formatter.format(Date(time))
    }

    /**
     * 计算距今的时间
     *
     * @param time
     * @return
     */
    @JvmStatic
    fun formatRecentTime(time: String?): String? {
        if (null == time || "" == time) {
            return ""
        }
        var commentTime: Date?
        var currentTime: Date?
        try {
            commentTime = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(time)
            currentTime = Calendar.getInstance().time
        } catch (e: ParseException) {
            Log.d(e.message)
            return null
        }

        val between = (currentTime!!.time - commentTime!!.time) / 1000// 除以1000是为了转换成秒

        val year = between / (24 * 3600 * 30 * 12)
        val month = between / (24 * 3600 * 30)
        val week = between / (24 * 3600 * 7)
        val day = between / (24 * 3600)
        val hour = between % (24 * 3600) / 3600
        val minute = between % 3600 / 60
        val second = between % 60 / 60

        if (year != 0L) {
            val sb = StringBuilder()
            sb.append(year).append("年前")
            return sb.toString()
        }
        if (month != 0L) {
            val sb = StringBuilder()
            sb.append(month).append("个月前")
            return sb.toString()
        }
        if (week != 0L) {
            val sb = StringBuilder()
            sb.append(week).append("周前")
            return sb.toString()
        }
        if (day != 0L) {
            val sb = StringBuilder()
            sb.append(day).append("天前")
            return sb.toString()
        }
        if (hour != 0L) {
            val sb = StringBuilder()
            sb.append(hour).append("小时前")
            return sb.toString()
        }
        if (minute != 0L) {
            val sb = StringBuilder()
            sb.append(minute).append("分钟前")
            return sb.toString()
        }
        if (second != 0L) {
            val sb = StringBuilder()
            sb.append(second).append("秒前")
            return sb.toString()
        }

        return ""
    }

    /**
     * 转化为中文时间格式
     *
     * @param time HH:mm:ss
     * @return
     */
    @JvmStatic
    fun getZhTimeString(time: String): String {
        val str = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (str.size == 3) {
            (Integer.valueOf(str[0]).toString() + "小时" + Integer.valueOf(str[1])
                    + "分" + Integer.valueOf(str[2]) + "秒")
        } else if (str.size == 2) {
            (Integer.valueOf(str[0]).toString() + "分" + Integer.valueOf(str[1])
                    + "秒")
        } else {
            Integer.valueOf(str[0]).toString() + "秒"
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
    @JvmStatic
    fun getFormatDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val nf = DecimalFormat("00")
        return year.toString() + "-" + nf.format((monthOfYear + 1).toLong()) + "-" + nf.format(dayOfMonth.toLong())
    }

    /**
     * 获取格式化时间HH:mm
     *
     * @param hourOfDay
     * @param minute
     * @return
     */
    @JvmStatic
    fun getFormatTime(hourOfDay: Int, minute: Int): String {
        val nf = DecimalFormat("00")
        return nf.format(hourOfDay.toLong()) + ":" + nf.format(minute.toLong())
    }

    /**
     * 格式化为应用 常见显示格式 当前天显示时间，其他显示年月日
     *
     * @param strTime yyyy-MM-dd HH:mm:ss
     * @return
     */
    @JvmStatic
    fun formatLatelyTime(strTime: String?): String? {
        if (null == strTime || "" == strTime) {
            return ""
        }
        var str: String?
        val currentCalendar: Calendar
        val commentCalendar: Calendar
        try {
            currentCalendar = Calendar.getInstance()
            commentCalendar = Calendar.getInstance()
            commentCalendar.timeInMillis = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(strTime).time

            if (currentCalendar.get(Calendar.YEAR) == commentCalendar.get(Calendar.YEAR)
                    && currentCalendar.get(Calendar.MONTH) == commentCalendar.get(Calendar.MONTH)
                    && currentCalendar.get(Calendar.DAY_OF_YEAR) == commentCalendar.get(Calendar.DAY_OF_YEAR)) {
                str = SimpleDateFormat(HH_MM).format(commentCalendar.time)
            } else if (currentCalendar.get(Calendar.YEAR) == commentCalendar.get(Calendar.YEAR)
                    && currentCalendar.get(Calendar.MONTH) == commentCalendar.get(Calendar.MONTH)
                    && currentCalendar.get(Calendar.DAY_OF_YEAR) != commentCalendar.get(Calendar.DAY_OF_YEAR)) {
                str = SimpleDateFormat(MM_DD).format(commentCalendar.time)
            } else if (currentCalendar.get(Calendar.YEAR) == commentCalendar.get(Calendar.YEAR) && currentCalendar.get(Calendar.MONTH) == commentCalendar.get(Calendar.MONTH)) {
                str = SimpleDateFormat(YYYY_MM).format(commentCalendar.time)
            } else {
                str = SimpleDateFormat(YYYY_MM_DD).format(commentCalendar.time)
            }
        } catch (e: ParseException) {
            Log.d(e.message)
            return null
        }

        return str
    }

    /**
     * 判断是同一天
     *
     * @param time1
     * @param time2
     * @return
     */
    @JvmStatic
    fun isSameDay(time1: Long, time2: Long): Boolean {
        val calDateA = Calendar.getInstance()
        calDateA.timeInMillis = time1

        val calDateB = Calendar.getInstance()
        calDateB.timeInMillis = time2

        return (calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
                .get(Calendar.DAY_OF_MONTH))
    }

    /**
     * 判断是同一月
     *
     * @param time1
     * @param time2
     * @return
     */
    @JvmStatic
    fun isSameMonth(time1: Long, time2: Long): Boolean {
        val time1Ca = Calendar.getInstance()
        time1Ca.firstDayOfWeek = Calendar.MONDAY
        time1Ca.timeInMillis = time1
        val time2Ca = Calendar.getInstance()
        time2Ca.firstDayOfWeek = Calendar.MONDAY
        time2Ca.timeInMillis = time2
        return time1Ca.get(Calendar.YEAR) == time2Ca.get(Calendar.YEAR) && time1Ca.get(Calendar.MONTH) == time2Ca.get(Calendar.MONTH)
    }

    /**
     * 判断是同一周
     *
     * @param time1
     * @param time2
     * @return
     */
    @JvmStatic
    fun isSameWeek(time1: Long, time2: Long): Boolean {
        val time1Ca = Calendar.getInstance()
        time1Ca.firstDayOfWeek = Calendar.MONDAY
        time1Ca.timeInMillis = time1
        val time2Ca = Calendar.getInstance()
        time2Ca.firstDayOfWeek = Calendar.MONDAY
        time2Ca.timeInMillis = time2
        return time1Ca.get(Calendar.WEEK_OF_YEAR) == time2Ca.get(Calendar.WEEK_OF_YEAR)
    }
}
