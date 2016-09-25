package com.lnpc.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 对象相关操作类
 * 
 * @author changjq
 * 
 */
public final class ObjectUtils {
	private static Logger logger = LoggerFactory.getLogger(ObjectUtils.class);

	private ObjectUtils() {
	}

	/**
	 * 创建类的实例
	 * 
	 * @author changjq
	 * @param className
	 *            类路径
	 * @return 类的实例
	 */
	public final static Object create(String className) {
		try {
			Class<?> clazz = ClassUtils.loadClass(className);
			return clazz.newInstance();
		} catch (Exception e) {
			logger.error("Error occurs when create object {}", className);
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}