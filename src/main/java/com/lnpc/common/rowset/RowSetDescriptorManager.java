package com.lnpc.common.rowset;

import java.io.File;

import com.lnpc.common.cache.Cache;
import com.lnpc.common.cache.CacheManagerFactory;
import com.lnpc.common.utils.ResourceUtils;

/**
 * RowSet描述管理类
 * 
 * @author changjq
 * 
 */
public class RowSetDescriptorManager {

	/**
	 * 根据名称获取RowSet描述
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @return RowSet描述
	 */
	public static RowSetDescriptor getRowSetDescriptor(String name) {
		RowSetDescriptor rowSetDescriptor = null;
		Cache cache = CacheManagerFactory.getCacheManager().get(RowSetXMLCacheImpl.cacheName);
		Object object = cache.get(name);
		if (object == null) {
			synchronized (cache) {
				object = cache.get(name);
				if (object == null) {
					rowSetDescriptor = load(name);
					if (rowSetDescriptor != null) {
						CacheManagerFactory.getCacheManager().get(RowSetXMLCacheImpl.cacheName).put(name, rowSetDescriptor);
					}
				}
			}
		} else {
			rowSetDescriptor = (RowSetDescriptor) object;
			File file = new File(ResourceUtils.getRowSetPath(name));
			if (file.lastModified() > rowSetDescriptor.getLastModified()) {
				synchronized (cache) {
					rowSetDescriptor = load(name);
					CacheManagerFactory.getCacheManager().get(RowSetXMLCacheImpl.cacheName).put(name, rowSetDescriptor);
				}
			}
		}
		return rowSetDescriptor;
	}

	/**
	 * 
	 * <p>
	 * Description: 创建一个RowSetDescriptor并返回
	 * </p>
	 * 
	 * @param rowSetName
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public static RowSetDescriptor load(String rowSetName) {
		RowSetDescriptor rowsetDescriptor = new RowSetDescriptor(rowSetName);
		return rowsetDescriptor;
	}
}
