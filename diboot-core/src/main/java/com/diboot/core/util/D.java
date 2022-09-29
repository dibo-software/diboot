/*
 * Copyright (c) 2015-2020, www.dibo.ltd (service@dibo.ltd).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.diboot.core.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 提供常用的日期操作的工具类
 * @author mazc@dibo.ltd
 * @version 2.0
 * @date 2019/01/01
 */

@SuppressWarnings({"unused", "SpellCheckingInspection", "AlibabaConstantFieldShouldBeUpperCase", "AlibabaUndefineMagicConstant", "AlibabaClassNamingShouldBeCamel"})
public class D extends DateUtils{
	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
	/**
	 * 日期时间格式
	 */
	public static final String FORMAT_DATE_y2M = "yyMM";
	public static final String FORMAT_DATE_y2Md = "yyMMdd";
	public static final String FORMAT_DATE_y4 = "yyyy";
	public static final String FORMAT_DATE_y4Md = "yyyyMMdd";
	public static final String FORMAT_TIMESTAMP = "yyMMddHHmmss";
	public static final String FORMAT_TIME_HHmm = "HH:mm";
	public static final String FORMAT_TIME_HHmmss = "HH:mm:ss";
	public static final String FORMAT_DATE_Y4MD = "yyyy-MM-dd";
	public static final String FORMAT_DATETIME_Y4MDHM = "yyyy-MM-dd HH:mm";
	public static final String FORMAT_DATETIME_Y4MDHMS = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_DATE_SLASH_Y4MD = "yyyy/MM/dd";
	public static final String FORMAT_DATETIME_SLASH_Y4MDHM = "yyyy/MM/dd HH:mm";
	public static final String FORMAT_DATETIME_SLASH_Y4MDHMS = "yyyy/MM/dd HH:mm:ss";
	public static final String FORMAT_DATETIME_Y4MD_T_HMS = "yyyy-MM-ddTHH:mm:ss";
	/**
	 * 星期（中文）
	 */
	public static final String[] WEEK = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	/**
	 * 星期（英文）
	 */
	public static final String[] WEEK_EN = new String[]{"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
	/**
	 * 月份-中文
	 */
	public static final String[] MONTH_CN = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};
	/**
	 * 月份-英文
	 */
	public static final String[] MONTH_EN = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	/**
	 * 时间类型-毫秒数定义
	 */
	public static final long MS_1SECOND = 1000;
	public static final long MS_1MINUTE = 60 * MS_1SECOND;
	public static final long MS_1HOUR = 60 * MS_1MINUTE;
	public static final long MS_1DAY = 24 * MS_1HOUR;

	/**
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
	 */
	@Deprecated
	public static String getMonth(){
		return now(FORMAT_DATE_y2M);
	}

	/**
	 * 获取今天的日期
	 * @return yyyyMMdd
	 */
	public static String today(){
		return now(FORMAT_DATE_y4Md);
	}

	/**
	 * 转换字符串为日期date
	 */
	public static Date convert2FormatDate(String datetime, String fmt){
		if (StringUtils.isBlank(datetime)){
			return null;
		}
		SimpleDateFormat format =  new SimpleDateFormat(fmt);
		try {
			return format.parse(datetime);
		}
		catch (ParseException e) {
			log.warn("日期格式转换异常");
		}
		return null;
	}

	/**
	 * 转换date为日期Y4MD格式化字符串
	 */
	public static String convert2DateString(Date date) {
		return convert2FormatString(date, FORMAT_DATE_Y4MD);
	}

	/**
	 * 转换date为日期时间Y4MDHMS格式化字符串
	 */
	public static String convert2DateTimeString(Date date) {
		return convert2FormatString(date, FORMAT_DATETIME_SLASH_Y4MDHMS);
	}

	/**
	 * 转换date为格式化字符串
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
		return getDate(date, FORMAT_DATE_Y4MD, daysOffset);
	}

	/**
	 * 获取日期的下一天
	 * @param date 基准日期
	 * @return yyyy-MM-dd
	 */
	public static Date nextDay(Date date){
		if(date == null){
			return null;
		}
		return addDays(date, 1);
	}

	/**
	 * 获取格式化的日期时间
	 */
	public static String getDateTime(Date date, int... daysOffset) {
		return getDate(date, FORMAT_DATETIME_Y4MDHM, daysOffset);
	}

	/**
	 * 获取格式化的日期时间
	 *
	 * @param date       日期
	 * @param format     日期格式
	 * @param daysOffset 偏移天数，在日期上add，要么不传，要么只有一个值
	 */
	public static String getDate(Date date, String format, int... daysOffset) {
		if (date == null) {
			date = new Date();
		}
		if (daysOffset != null && daysOffset.length > 0) {
			date = addDays(date, daysOffset[0]);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 是否是工作时间段，用于后台程序等
	 *
	 * @return 是否是工作时间段
	 */
	public static boolean isWorkingTime() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		return (hour >= 8 && hour < 20);
	}

	/**
	 * 获取上午/下午
	 * @return 上午/下午
	 */
	public static String getAmPm() {
		Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		if (hours <= 9){
			return "早上";
		}
		else if (hours <= 12){
			return "上午";
		}
		else if (hours == 13){
			return "中午";
		}
		else if (hours <= 18){
			return "下午";
		}
		else{
			return "晚上";
		}
	}

	/**
	 * 得到当前的年月yyMM，用于生成文件夹名称
	 * @return 年月
	 */
	public static String getYearMonth(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_y2M);
		return sdf.format(cal.getTime());
	}

	/**
	 * 得到当前的年月日yyMMdd，用于生成文件夹
	 * @return 年月日
	 */
	public static String getYearMonthDay(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_y2Md);
		return sdf.format(cal.getTime());
	}

	/**
	 * 得到当前的月，用于生成文件夹
	 * @return 月
	 */
	public static int getDay(){
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期对应的星期
	 */
	@Deprecated
	public static String getWeek(Date date){
		return getCnWeek(date);
	}

	/**
	 * 获取当前日期对应的星期（中文）
	 */
	public static String getCnWeek(){
		int index = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		return WEEK[index];
	}

	/**
	 * 获取日期对应的星期（中文）
	 */
	public static String getCnWeek(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/**
	 * 获取当前日期对应的星期（英文）
	 */
	public static String getEnWeek(){
		int index = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		return WEEK_EN[index];
	}

	/**
	 * 获取日期对应的星期（英文）
	 */
	public static String getEnWeek(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return WEEK_EN[cal.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/**
	 * 获取当前日期对应的月份（中文）
	 */
	public static String getCnMonth(){
		return MONTH_CN[Calendar.getInstance().get(Calendar.MONTH)];
	}

	/**
	 * 获取指定日期对应的月份（中文）
	 * @param date 日期
	 */
	public static String getCnMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return MONTH_CN[cal.get(Calendar.MONTH)];
	}

	/**
	 * 获取当前日期对应的月份（英文）
	 */
	public static String getEnMonth(){
		return MONTH_EN[Calendar.getInstance().get(Calendar.MONTH)];
	}

	/**
	 * 获取指定日期对应的月份（中文）
	 * @param date 日期
	 */
	public static String getEnMonth(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return MONTH_EN[cal.get(Calendar.MONTH)];
	}

	/**
	 * 毫秒数转date
	 */
	public static Date timeMillis2Date(Long timeMillis){
		return new Date(timeMillis);
	}

	/**
	 * 字符串时间戳转日期
	 */
	public static Date datetimeString2Date(String value){
		return convert2DateTime(value, FORMAT_DATETIME_Y4MDHMS);
	}

	/**
	 * 转换耗时毫秒数为 d,h,m,s显示文本
	 * @param duration 时长毫秒数
	 */
	public static String formatDurationLabel(Long duration) {
		if (duration == null) {
			return "-";
		}
		long days = duration / MS_1DAY;
		if (days > 0) {
			return days + "d";
		}
		// 必然小于MS_1DAY，不需要取模
		long hours = duration / MS_1HOUR;
		if (hours > 0) {
			return hours + "h";
		}
		long minutes = duration / MS_1MINUTE;
		if (minutes > 0) {
			return minutes + "m";
		}
		long seconds = duration / MS_1SECOND;
		if (seconds > 0) {
			return seconds + "s";
		}
		return "<1s";
	}

	/**
	 * 字符串时间戳转日期
	 */
	public static Date convert2Date(String date){
		if(date.contains("/")){
			return convert2FormatDate(date, FORMAT_DATE_SLASH_Y4MD);
		}
		else{
			return convert2FormatDate(date, FORMAT_DATE_Y4MD);
		}
	}

	/**
	 * 获取两个时间范围内的所有日期
	 *
	 * @param startTime 包含
	 * @param endTime 不包含
	 */
	public static List<String> rangeDateList(Date startTime, Date endTime) throws ParseException {
		if (startTime == null || endTime == null || startTime.after(endTime)) {
			return Collections.emptyList();
		}
		//日期工具类准备
		DateFormat format = new SimpleDateFormat(D.FORMAT_DATE_Y4MD);
		String dBegin = format.format(startTime);

		//设置开始时间
		Calendar calBegin = Calendar.getInstance();
		calBegin.setTime(format.parse(dBegin));

		// 两个时间相差的天数
		int days = (int) ((endTime.getTime() - startTime.getTime()) / MS_1DAY);

		//装返回的日期集合容器
		List<String> rangeDateList = new ArrayList<>(days);
		// 每次循环给calBegin日期加一天
		for (int i = 0; i < days; i++) {
			rangeDateList.add(format.format(calBegin.getTime()));
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
		}
		return rangeDateList;
	}

	/**
	 * 字符串时间戳转日期
	 */
	public static Date convert2DateTime(String dateTime, String... dateFormat){
		String f = FORMAT_DATETIME_Y4MDHM;
		if(dateFormat != null && dateFormat.length > 0){
			f = dateFormat[0];
		}
		return convert2FormatDate(dateTime, f);
	}

	/**
	 * 模糊转换日期
	 */
	public static Date fuzzyConvert(String dateString){
		if(V.isEmpty(dateString)){
			return null;
		}
		dateString = formatDateString(dateString);
		if(!dateString.contains(" ")) {
			return convert2FormatDate(dateString, FORMAT_DATE_Y4MD);
		}
		return convert2FormatDate(dateString, FORMAT_DATETIME_Y4MDHMS);
	}

	/**
	 * 格式化日期字符串
	 */
	public static String formatDateString(String dateString){
		if(V.isEmpty(dateString)){
			return null;
		}
		// 清洗
		if(dateString.contains("月")){
			dateString = dateString.replaceAll("年", "-").replaceAll("月", "-").replaceAll("日", "").replaceAll("号", "");
		}
		else{
			dateString = dateString.replaceAll("/", "-").replaceAll("\\.", "-");
		}
		String[] parts = (dateString.contains("T") && !dateString.contains(" "))? dateString.split("T") : dateString.split(" ");
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
			return parts[0];
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
		return S.join(parts, " ");
	}

}
