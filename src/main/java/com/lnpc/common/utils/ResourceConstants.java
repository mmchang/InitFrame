package com.lnpc.common.utils;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * lnpc.properties属性文件相关操作类
 * 
 * @author changjq
 * 
 */
public class ResourceConstants {
	private static Logger logger = Logger.getLogger(ResourceConstants.class);
	private static String filePath = null;
	private final static String PROPERTIES_NAME = "lnpc.properties";
	private static Map<String, String> resource = null;
	static {
		final String ROOT = ResourceConstants.class.getClassLoader().getResource("/").getPath();
		filePath = ROOT + PROPERTIES_NAME;
		resource = new LightConfigLoader(filePath).getInfoMap();
	}

	/**
	 * 根据键获取相应的值
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param key
	 * @return key对应的value
	 */
	public static String getResource(String key) {
		String resourceResult = resource.get(key);
		if (resourceResult == null) {
			resourceResult = "";
		}
		return resourceResult;
	}
}
