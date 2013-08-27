package com.cangol.mobile.utils;

/**
 * @Description: 日期帮助类
 * @version $Revision: 1.0 $
 * @author xuewu.wei
 * @date: 2010-12-6
 * @time: 下午04:47:39
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtils {
	private static String CurrentTime;
	private static String CurrentDate;
	/**
	 * 得到当前的年份
	 * 返回格式:yyyy
	 * @return String
	 */
	public static String getCurrentYear() {
		java.util.Date NowDate = new java.util.Date();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		return formatter.format(NowDate);
	}
	/**
	 * 得到当前的月份
	 * 返回格式:MM
	 * @return String
	 */
	public static String getCurrentMonth() {
		java.util.Date NowDate = new java.util.Date();

		SimpleDateFormat formatter = new SimpleDateFormat("MM");
		return formatter.format(NowDate);
	}
	/**
	 * 得到当前的日期
	 * 返回格式:dd
	 * @return String
	 */
	public static String getCurrentDay() {
		java.util.Date NowDate = new java.util.Date();

		SimpleDateFormat formatter = new SimpleDateFormat("dd");
		return formatter.format(NowDate);
	}
	/**
	 * 得到当前的时间，精确到毫秒,共14位
	 * 返回格式:yyyy-MM-dd HH:mm:ss
	 * @return String
	 */
	public static String getCurrentTime() {
		Date NowDate = new Date();
		SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CurrentTime = formatter.format(NowDate);
		return CurrentTime;
	}
	public static Date convertToDate(String date) {
		SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
		try{
			return formatter.parse(date);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static String convertToString(Date date) {
		SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
		try{
			return formatter.format(date);
		}catch(Exception e){
			//e.printStackTrace();
			return null;
		}
	}
	/**
	 * 得到当前的时间加上输入年后的时间，精确到毫秒,共19位
	 * 返回格式:yyyy-MM-dd:HH:mm:ss
	 * @return String
	 */
	public static String getCurrentTimeAddYear(int addyear) {
		String currentYear="";
		Date NowDate = new Date();
		
		currentYear=TimeUtils.getCurrentYear();
		currentYear=String.valueOf(Integer.parseInt(TimeUtils.getCurrentYear())+addyear);
		
		
		SimpleDateFormat formatter =new SimpleDateFormat("-MM-dd:HH:mm:ss");
		CurrentTime = formatter.format(NowDate);
		return currentYear+CurrentTime;
	}
	/**
	 * 得到当前的日期,共10位
	 * 返回格式：yyyy-MM-dd
	 * @return String
	 */
	public static String getCurrentDate() {
		Date NowDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		CurrentDate = formatter.format(NowDate);
		return CurrentDate;
	}
	/**
	 * 得到当前的日期,共8位
	 * 返回格式：yyyyMMdd
	 * @return String
	 */
	public static String getDate8Bit() {
		Date NowDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		CurrentDate = formatter.format(NowDate);
		return CurrentDate;
	}
	/**
	 * 得到当前日期加上某一个整数的日期，整数代表天数
	 * 输入参数：currentdate : String 格式 yyyy-MM-dd
	 * 			add_day		:  int
	 * 返回格式：yyyy-MM-dd
	 */
	public static String addDay(String currentdate,int add_day){
		GregorianCalendar gc=null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		int year,month,day;
		
		try {
			year=Integer.parseInt(currentdate.substring(0,4));
			month=Integer.parseInt(currentdate.substring(5,7))-1;
			day=Integer.parseInt(currentdate.substring(8,10));
			
			gc=new GregorianCalendar(year,month,day);
			gc.add(GregorianCalendar.DATE,add_day);
		
			return formatter.format(gc.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 得到当前月份的第一天日期
	 */
	public static String getStartDateInPeriod(String period) {
		StringBuffer str = new StringBuffer(period);
		return str.append("01").toString();

	}
    /**
     * 得到当前月份的最后一天
     * @param period
     * @return
     */
	public static String getEndDateInPeriod(String period) {
		String date = "";
		java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd");
		int year = Integer.parseInt(period.substring(0, 4));
		int month = Integer.parseInt(period.substring(5, 7));
		System.err.println(month);
		Calendar cl = Calendar.getInstance();
		cl.set(year, month - 1, 1);
		cl.add(Calendar.MONTH, 1);
		cl.add(Calendar.DATE, -1);
		date = df.format(cl.getTime());
		return date;
	}
    /**
     * 将YYYYMMDD形式改成YYYY-MM-DD
     * 
     */
	public static String convertStr(String str1){
		if(str1==null||str1.equals("")){
			return "";
		}else{
			String result ="";
			result += str1.substring(0,4)+"-";
			result += str1.substring(4,6)+"-";
			result += str1.substring(6,8);
			return result;
		}
	}
	 /**
     * 将YYYY-MM-DD形式改成YYYYMMDD
     * 
     */
	public static String convert(String str1){
		if(str1==null||str1.equals("")){
			return "";
		}else{
			String temp[] = str1.split("-");
			String result = "";
			for(int i=0;i<temp.length;i++){
				result = result + temp[i];
			}
			return result;
		}
	}
	public static String countTime(String cTime) {
		if(null==cTime||"".equals(cTime))return "";
		Date commentTime = null;
		Date currentTime = null;
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				commentTime = dfs.parse(cTime);
				currentTime = Calendar.getInstance().getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		long between = (currentTime.getTime() - commentTime.getTime()) / 1000;// 除以1000是为了转换成秒

		long year = between / (24 * 3600 * 30 * 12);
		long month = between / (24 * 3600 * 30);
		long week = between/(24*3600*7);
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long minute = between % 3600 / 60;
		long second = between % 60 / 60;

		StringBuffer sb = new StringBuffer();
		if (year != 0) {
			sb.append(year + "年");
			return sb.toString() + "前";
		}
		if (month != 0) {
			sb.append(month + "个月");
			return sb.toString() + "前";
		}
		if (week != 0) {
			sb.append(week + "周");
			return sb.toString() + "前";
		}
		if (day != 0) {
			sb.append(day + "天");
			return sb.toString() + "前";
		}
		if (hour != 0) {
			sb.append(hour + "小时");
			return sb.toString() + "前";
		}
		if (minute != 0) {
			sb.append(minute + "分钟");
			return sb.toString() + "前";
		}
		if (second != 0) {
			sb.append(second + "秒");
			return sb.toString() + "前";
		}

		return "";
	}
	/**
	 * 
	 * @param time HH:mm:ss
	 * @return
	 */
	public static String getZhTimeString(String time){
		String[] str=time.split(":");
		if(str.length==3){
			return Integer.valueOf(str[0])+"小时"+Integer.valueOf(str[1])+"分"+Integer.valueOf(str[2])+"秒";
		}else if(str.length==2){
			return Integer.valueOf(str[0])+"分"+Integer.valueOf(str[1])+"秒";
		}else {
			return Integer.valueOf(str[0])+"秒";
		}
	}
}


