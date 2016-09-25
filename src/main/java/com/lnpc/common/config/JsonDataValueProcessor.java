package com.lnpc.common.config;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * 此类用于将对象转为JSON格式时<br>
 * 此系统暂未用到
 * 
 * @author changjq
 * 
 */
public class JsonDataValueProcessor implements JsonValueProcessor {
	private final String format = "yyyy-MM-dd HH:mm:ss";

	public Object processObjectValue(String key, Object value, JsonConfig arg2) {
		if (value == null) {
			return "";
		} else if (value instanceof Date) {
			String str = new SimpleDateFormat(format).format((Date) value);
			return str;
		} else if (value instanceof Timestamp) {
			String str = new SimpleDateFormat(format).format((Date) value);
			return str;
		} else if (value instanceof String) {
			return ((String) value).trim();
		}
		return value;
	}

	public Object processArrayValue(Object value, JsonConfig arg1) {
		return null;
	}
}
