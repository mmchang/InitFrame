package com.lnpc.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 类操作相关工具类
 * 
 * @author changjq
 * 
 */
public final class ClassUtils {
	/**
	 * 对已加载的类进行缓存
	 */
	private static Map<Object, Object> classMapping = new HashMap<Object, Object>();

	private ClassUtils() {
	}

	/**
	 * 获取当前线程的类加载器
	 * 
	 * @author changjq
	 * @return
	 */
	public final static java.lang.ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * 加载类
	 * 
	 * @author changjq
	 * @param className
	 * @return
	 * @throws Exception
	 */
	public final static Class<?> loadClass(String className) throws Exception {
		if (classMapping.containsKey(className)) {
			return (Class<?>) classMapping.get(className);
		} else {
			return getClassLoader().loadClass(className);
		}
	}
}