package com.lnpc.common.cache;

import com.lnpc.common.utils.ObjectUtils;

/**
 * 系统缓存工厂类
 * 
 * @author changjq
 * 
 */
public class CacheManagerFactory {
	private static CacheManager cacheManager;

	private static final String Class_name = CacheManager.class.getName();
	static {
		cacheManager = (CacheManager) ObjectUtils.create(Class_name);
	}

	private CacheManagerFactory() {

	}

	/**
	 * 获取缓存管理器
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 缓存管理器
	 */
	public static CacheManager getCacheManager() {
		return cacheManager;
	}
}
