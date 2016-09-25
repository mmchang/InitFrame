package com.lnpc.common.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间相关工具类
 * 
 * @author changjq
 * 
 */
public class DateUtils {
	/**
	 * 将日期格式化为字符串形式
	 * 
	 * @author changjq
	 * @param date
	 * @param format
	 * @return 时间格式化后的字符串形式
	 */
	public static String date2String(Date date, String format) {
		String strRet = "";
		if (date != null) {
			DateFormat dateFormat = new SimpleDateFormat(format);
			strRet = dateFormat.format(date);
		}
		return strRet;
	}

	/**
	 * 将字符串格式化为日期形式
	 * 
	 * @author changjq
	 * @param dateStr
	 * @param format
	 * @return 格式化后的日期
	 */
	public static Date string2Date(String dateStr, String format) throws Exception {
		Date date = null;
		if (dateStr != null && !"".equals(dateStr)) {
			DateFormat dateFormat = new SimpleDateFormat(format);
			date = dateFormat.parse(dateStr);
		}
		return date;
	}
	
	/**
	 * 
	 * <p>Description:硬性返回时间字符串的format形式 </p>
	 * <p>由于oracle不支持时间字段与字符串比较，且使用to_date函数需要format</p>
	 * <p>目前只支持完整的日期和24小时制的形式，且日期与时间中间有空格</p>
	 * @param dateString
	 * @return
	 * @author changjq
	 * @date 2016年9月25日
	 */
	public static String dateStr2FormatStrForOracle(String dateString){
		String format = null;
		if(dateString!=null){
			int len = dateString.trim().length();
			final String[] splitSign = {"-","/","."};
			for(final String sign:splitSign){
				if(dateString.contains(sign)){
					switch (len) {
					case 4:
						format = "yyyy";
						break;
					case 7:
						format = "yyyy"+sign+"mm";
						break;
					case 10:
						format = "yyyy"+sign+"mm"+sign+"dd";
						break;
					case 13:
						format = "yyyy"+sign+"mm"+sign+"dd hh24";
						break;
					case 16:
						format = "yyyy"+sign+"mm"+sign+"dd hh24:mi";
						break;
					case 19:
						format = "yyyy"+sign+"mm"+sign+"dd hh24:mi:ss";
						break;
					default:
						dateString = "yyyy"+sign+"mm"+sign+"dd";
						break;
					}
					break;
				}
			}
			if(format==null){
				switch (len) {
				case 4:
					format = "yyyy";
					break;
				case 6:
					format = "yyyymm";
					break;
				case 8:
					format = "yyyymmdd";
					break;
				case 11:
					format = "yyyymmdd hh24";
					break;
				case 14:
					format = "yyyymmdd hh24:mi";
					break;
				case 17:
					format = "yyyymmdd hh24:mi:ss";
					break;
				default:
					dateString = "yyyymmdd";
					break;
				}
			}
		}
		return format;
	}
}
