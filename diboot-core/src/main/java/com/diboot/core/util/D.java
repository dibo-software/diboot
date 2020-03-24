package com.diboot.core.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 提供常用的日期操作的工具类
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */
public class D extends DateUtils{
	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
	
	/***
	 * 日期时间格式
	 */
	public static final String FORMAT_DATE_y2M = "yyMM";
	public static final String FORMAT_DATE_y2Md = "yyMMdd";
	public static final String FORMAT_DATE_y4 = "yyyy";
	public static final String FORMAT_DATE_y4Md = "yyyyMMdd";
	public static final String FORMAT_DATE_Y4MD = "yyyy-MM-dd";
	public static final String FORMAT_TIMESTAMP = "yyMMddhhmmss";
	public static final String FORMAT_TIME_HHmm = "HH:mm";
	public static final String FORMAT_TIME_HHmmss = "HH:mm:ss";
	public static final String FORMAT_DATETIME_Y4MDHM = "yyyy-MM-dd HH:mm";
	public static final String FORMAT_DATETIME_Y4MDHMS = "yyyy-MM-dd HH:mm:ss";
	/***
	 * 星期
	 */
	public static final String[] WEEK = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

	/***
	 * 当前的日期时间
	 * @return format指定格式的日期时间
	 */
	public static String now(String format){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(cal.getTime());
	}
	
	/**
	 * 当前日期时间串
	 * @return yyMMddhhmmss
	 */
	public static String toTimestamp(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIMESTAMP);
		return sdf.format(date.getTime());
	}
	
	/**
	 * 获取月份
	 * @return
	 */
	public static String getMonth(){
		return now(FORMAT_DATE_y2M);
	}
	
	/***
	 * 获取今天的日期
	 * @return yyyyMMdd
	 */
	public static String today(){
		return now(FORMAT_DATE_y4Md);
	}

	/***
	 * 转换字符串为日期date
	 * @param datetime
	 * @param fmt
	 * @return
	 */
	public static Date convert2FormatDate(String datetime, String fmt){
		if (StringUtils.isBlank(datetime)){
			return null;
		}
		SimpleDateFormat format =  new SimpleDateFormat(fmt);
		try {
			Date date = format.parse(datetime);
			return date;
		}
		catch (ParseException e) {
			log.warn("日期格式转换异常");
		}
		return null;
	}

	/***
	 * 转换date为格式化字符串
	 * @param date
	 * @param fmt
	 * @return
	 */
	public static String convert2FormatString(Date date, String fmt) {
		if (date == null) {
			return null;
		} else {
			SimpleDateFormat format = new SimpleDateFormat(fmt);
			return format.format(date);
		}
	}

	/**
	 * 获取格式化的日期
	 * @param date 基准日期
	 * @param daysOffset 偏移量
	 * @return yyyy-MM-dd
	 */
	public static String getDate(Date date, int... daysOffset){
		if(date == null){
			date = new Date();
		}
		if(daysOffset != null && daysOffset.length > 0){
			date = addDays(date, daysOffset[0]);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_Y4MD);
		return sdf.format(date);
	}

	/***
	 * 获取格式化的日期时间
	 * @param date
	 * @return yyyy-MM-dd HH:mm
	 */
	public static String getDateTime(Date date, int... daysOffset){
		if(date == null){
			date = new Date();
		}
		if(daysOffset != null && daysOffset.length > 0){
			date = addDays(date, daysOffset[0]);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME_Y4MDHM);
		return sdf.format(date);
	}
	
	/**
	 * 是否是工作时间段，用于后台程序等
	 * @return 
	 */
	public static boolean isWorkingTime(){
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		return (hour >= 8 && hour < 20);
	}

	/***
	 * 获取上午/下午
	 * @return
	 */
	public static String getAmPm() {
		Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		if (hours <= 9){
			return "早上";
		}
		else if (9 < hours && hours <= 12){
			return "上午";
		}
		else if (12 < hours && hours <= 13){
			return "中午";
		}
		else if (13 < hours && hours <= 18){
			return "下午";
		}
		else{
			return "晚上";
		}
	}

	/**
	 * 得到当前的年月YYMM，用于生成文件夹名称
	 * @return
	 */
	public static String getYearMonth(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_y2M);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 得到当前的年月YYMM，用于生成文件夹
	 * @return
	 */
	public static String getYearMonthDay(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_y2Md);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 得到当前的年月YYMM，用于生成文件夹
	 * @return
	 */
	public static int getDay(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/***
	 * 获取日期对应的星期
	 * @param date
	 * @return
	 */
	public static String getWeek(Date date){
		return WEEK[Calendar.getInstance().get(Calendar.DAY_OF_WEEK)];
	}
	
	/**
	 * 毫秒数转date
	 * @param timeMillis
	 * @return
	 */
	public static Date timeMillis2Date(Long timeMillis){
		return new Date(timeMillis);
	}
	
	/**
	 * 字符串时间戳转日期
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static Date datetimeString2Date(String value){
		return convert2DateTime(value, FORMAT_DATETIME_Y4MDHMS);
	}

	/**
	 * 字符串时间戳转日期
	 * @return
	 * @throws ParseException
	 */
	public static Date convert2Date(String date){
		return convert2FormatDate(date, FORMAT_DATE_Y4MD);
	}

	/**
	 * 字符串时间戳转日期
	 * @param dateTime
	 * @return
	 * @throws ParseException
	 */
	public static Date convert2DateTime(String dateTime, String... dateFormat){
		String f = FORMAT_DATETIME_Y4MDHM;
		if(dateFormat != null && dateFormat.length > 0){
			f = dateFormat[0];
		}
		return convert2FormatDate(dateTime, f);
	}

	/***
	 * 模糊转换日期
	 * @param dateString
	 * @return
	 */
	public static Date fuzzyConvert(String dateString){
		if(V.isEmpty(dateString)){
			return null;
		}
		// 清洗
		if(dateString.contains("-")){
		}
		else if(dateString.contains("月")){
			dateString = dateString.replaceAll("年", "-").replaceAll("月", "-").replaceAll("日", "").replaceAll("号", "");
		}
		else{
			dateString = dateString.replaceAll("\\/", "-").replaceAll("\\.", "-");
		}
		String[] parts = dateString.split(" ");
		String[] ymd = parts[0].split("-");
		if(ymd.length >= 3){
			if(ymd[0].length() == 2){
				ymd[0] = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(0, 2) + ymd[0];
			}
			if(ymd[1].length() == 1){
				ymd[1] = "0" + ymd[1];
			}
			if(ymd[2].length() == 1){
				ymd[2] = "0" + ymd[2];
			}
		}
		parts[0] = S.join(ymd, "-");
		if(parts.length == 1){
			return D.convert2FormatDate(parts[0], D.FORMAT_DATE_Y4MD);
		}
		// 18:20:30:103
		String[] hmsArray = new String[3];
		String[] hms = parts[1].split(":");
		if(hms[0].length() == 1){
			hms[0] = "0" + hms[0];
		}
		hmsArray[0] = hms[0];
		if(hms.length >= 2){
			if(hms[1].length() == 1){
				hms[1] = "0" + hms[1];
			}
			hmsArray[1] = hms[1];
		}
		else{
			hmsArray[1] = "00";
		}
		if(hms.length >= 3){
			if(hms[2].length() == 1){
				hms[2] = "0" + hms[2];
			}
			hmsArray[2] = hms[2];
		}
		else{
			hmsArray[2] = "00";
		}
		parts[1] = S.join(hmsArray, ":");
		return D.convert2FormatDate(S.join(parts, " "), D.FORMAT_DATETIME_Y4MDHMS);
	}

}
