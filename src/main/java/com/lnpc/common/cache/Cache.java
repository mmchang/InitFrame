package com.lnpc.common.cache;

import java.util.Iterator;
/**
 * 系统缓存接口
 * @author changjq
 *
 */
public interface Cache
{
	/**
	 * 获取缓存名称
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 缓存名称
	 */
	public String getName();
	/**
	 * 放入缓存
	 * @author changjq
	 * @date 2015年6月5日
	 * @param key
	 * @param value
	 */
	public void put(Object key, Object value);
	/**
	 * 获取缓存内容
	 * @author changjq
	 * @date 2015年6月5日
	 * @param key
	 * @return 缓存内容
	 */
	public Object get(Object key);
	/**
	 * 删除缓存内容
	 * @author changjq
	 * @date 2015年6月5日
	 * @param key
	 */
	public void remove(Object key);
	/**
	 * 删除所有缓存内容
	 * @author changjq
	 * @date 2015年6月5日
	 */
	public void removeAll();
	/**
	 * 释放操作，可以做一些回收事件
	 * @author changjq
	 * @date 2015年6月5日
	 */
	public void release();
	/**
	 * 获取缓存key的集合
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 缓存key的集合
	 */
	public Iterator<?> keySet();
	/**
	 * 缓存大小
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	public int size();
	/**
	 * 加载缓存（初始化）
	 * @author changjq
	 * @date 2015年6月5日
	 */
	public void load(String... rowsetName);
}
