package com.lnpc.manage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置管理器
 * 
 * @author changjq
 * 
 */
public class ConfigManager {

	private static Map<String, String> sysMap = Collections.synchronizedMap(new HashMap<String, String>());

	/**
	 * 设置系统根路径
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @param value
	 */
	public static void setSysPath(String name, String value) {
		sysMap.put(name, getAppRealPath(value));
	}

	/**
	 * 设置系统常量(key:value)
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @param value
	 */
	public static void setConstant(String name, String value) {
		sysMap.put(name, value);
	}

	/**
	 * 根据key获取系统常量值
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @return 系统常量值
	 */
	public static String getConstant(String name) {
		return getSysPath(name);
	}

	/**
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @return
	 */
	public static String getSysPath(String name) {
		return (String) sysMap.get(name);
	}

	/**
	 * 转换根路径
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param sysPath
	 * @return 转换后的根路径
	 */
	private static String getAppRealPath(String sysPath) {
		if (sysPath != null) {
			String fs = System.getProperty("file.separator");
			if (fs.length() == 1) {
				char sep = fs.charAt(0);
				if (sep != '/')
					sysPath = sysPath.replace(sep, '/');
			}
		}
		return sysPath;
	}
}
