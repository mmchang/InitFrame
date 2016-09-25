package com.lnpc.common.rowset;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lnpc.common.cache.Cache;
import com.lnpc.common.utils.ResourceUtils;
import com.lnpc.common.utils.StringUtils;

/**
 * 缓存RowSetDescriptor
 * 
 * @author changjq
 * 
 */
public class RowSetXMLCacheImpl implements Cache {
	/**
	 * 缓存名称
	 */
	public static final String cacheName = "rowsetDescriptor";

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(RowSetXMLCacheImpl.class);

	/**
	 * 存储RowSetDescriptor
	 */
	private static Map<Object, Object> caches = Collections.synchronizedMap(new HashMap<Object, Object>());

	@Override
	public Object get(Object key) {
		return caches.get(key);
	}

	@Override
	public String getName() {
		return cacheName;
	}

	@Override
	public Iterator<Object> keySet() {
		return caches.keySet().iterator();
	}

	@Override
	public void put(Object key, Object value) {
		caches.put(key, value);
	}

	@Override
	public void release() {
		// nothing to do
	}

	@Override
	public void remove(Object key) {
		caches.remove(key);
	}

	@Override
	public void removeAll() {
		caches.clear();
	}

	@Override
	public int size() {
		return caches.size();
	}

	/**
	 * load rowset xml
	 */
	public void load(String... names) {
		int size = 0;
		if (names != null && names.length != 0) {
			size = names.length;
			for (int i = 0; i < size; i++) {
				logger.info("Loading RowSet:{}...", names[i]);
				loadRowSet(names[i]);
			}
		} else {
			logger.info("Loading all RowSet...");
			String rowSetName = null;
			String rowSetFile = null;
			String rowset_root_path = ResourceUtils.getRowSetRootPath();
			File rowsets[] = new File(rowset_root_path).listFiles();
			size = rowsets.length;
			for (int i = 0; i < size; i++) {
				rowSetFile = rowsets[i].getName();
				if (rowSetFile.endsWith(".xml")) {
					rowSetName = StringUtils.trimExtendName(rowSetFile);
					loadRowSet(rowSetName);
				}
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param names
	 * @author changjq
	 * @date 2016年9月25日
	 */
	private void loadRowSet(String rowSetName) {
		this.put(rowSetName, RowSetDescriptorManager.load(rowSetName));
	}

}
