package com.lnpc.common.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 系统缓存管理器
 * 
 * @author changjq
 * 
 */
public class CacheManager {
	/**
	 * 存储缓存对象
	 */
	private static Map<Object, Object> caches = Collections.synchronizedMap(new HashMap<Object, Object>());

	/**
	 * 根据缓存名称获取缓存对象
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @return 缓存对象
	 */
	public Cache get(String name) {
		return (Cache) caches.get(name);
	}

	/**
	 * 将缓存对象放入二级缓存
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @param cache
	 */
	public void set(String name, Cache cache) {
		caches.put(name, cache);
	}

	/**
	 * 二级缓存内容迭代
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 二级缓存内容的迭代
	 */
	public Iterator<Object> keySet() {
		return caches.keySet().iterator();
	}

	/**
	 * 释放二级缓存的内容
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 */
	public void release() {
		for (Iterator<Object> iter = caches.values().iterator(); iter.hasNext();) {
			Cache cache = (Cache) iter.next();
			cache.release();
		}
		caches.clear();
	}

	/**
	 * 根据缓存名称，释放二级缓存内容并移除
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 */
	public void remove(String name) {
		Cache cache = (Cache) caches.get(name);
		if (cache != null) {
			cache.release();
			caches.remove(name);
		}
	}
}
