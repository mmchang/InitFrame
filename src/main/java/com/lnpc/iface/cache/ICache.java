package com.lnpc.iface.cache;

import java.util.List;

/**
 * 缓存接口，实现类用于加载数据库数据字典缓存
 * 
 * @author changjq
 * 
 */
public interface ICache {
	/**
	 * 查询缓存类型 key
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 缓存key的集合
	 */
	public List<?> queryPropertiesTypes();

	/**
	 * 查询缓存内容集合
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param type
	 * @return 缓存内容集合
	 */
	public List<?> queryPropertiesByType(String type);
}