package com.lnpc.common.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * spring bean 工具类
 * 
 * @author changjq
 * 
 */
public class BeanUtils {
	/**
	 * 对spring bean 进行一次缓存
	 */
	private static Map<String, Object> object_map = Collections.synchronizedMap(new HashMap<String, Object>());
	private static WebApplicationContext webAppCxt = null;
	private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);

	private BeanUtils() {

	}

	/**
	 * 初始化spring
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param sc
	 */
	public static void setServletContext(ServletContext sc) {
		webAppCxt = WebApplicationContextUtils.getWebApplicationContext(sc);
		logger.info("Initial the WebApplicationContext finished...");
	}

	/**
	 * 从spring池中获取bean的实例
	 * 
	 * @author changjq
	 * @param name
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Object getBean(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (object_map.containsKey(name)) {
			return object_map.get(name);
		} else {
			if (webAppCxt.containsBean(name)) {
				return webAppCxt.getBean(name);
			} else {
				Object object = Class.forName(name).newInstance();
				object_map.put(name, object);
				return object;
			}
		}
	}
}
