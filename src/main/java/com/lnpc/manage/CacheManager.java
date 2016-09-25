package com.lnpc.manage;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.jdom.Document;
import org.jdom.Element;

import com.lnpc.common.config.Constant;
import com.lnpc.common.persistence.Persistence;
import com.lnpc.common.rowset.RowSet;
import com.lnpc.common.utils.BeanUtils;
import com.lnpc.common.utils.ListUtils;
import com.lnpc.common.utils.XmlUtils;

/**
 * 缓存（数据字典）管理类
 * 
 * @author changjq
 * 
 */
public class CacheManager {
	private static Map<String, RowSet> cachesMap = Collections.synchronizedMap(new HashMap<String, RowSet>());
	
	private static String cacheSqlPath = null;

	private CacheManager() {

	}

	/**
	 * 获取所有缓存对象的拷贝
	 * @Title: getCachesMap
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月9日 上午11:02:22 
	 * @return
	 * @return: Map<String,RowSet>
	 */
	public static Map<String, RowSet> getCachesMap() {
		//暂时返回真实CACHE
		return cachesMap;
		
		/*Map<String, RowSet> cachesClone = new HashMap<String, RowSet>();
		Set<String> set = cachesMap.keySet();
		Iterator<String> it = set.iterator();
		while(it.hasNext()){
			String type = it.next();
			RowSet rs = cachesMap.get(type);
			cachesClone.put(type, rs.clone());
		}
		return cachesClone;*/
	}

	/**
	 * 重新加载缓存
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @throws Exception
	 */
	public static void reload(String... types) throws Exception {
		final String filePath = cacheSqlPath;
		if(types!=null && types.length>0){
			for(String type:types){
				cachesMap.remove(type);
				getCache(type);
			}
		}else{
			cachesMap.clear();
			load(filePath);
		}
	}

	/**
	 * 将数据字典载入缓存
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param filePath
	 * @throws Exception
	 */
	public static void load(String filePath) throws Exception {
		cacheSqlPath = filePath;
		RowSet rs = getDictListType();
		for (int i = 0; i < rs.getRowCount(); i++) {
			String type = rs.getString(i, "TYPE");
			getDictListCache(type);
		}
		if (cacheSqlPath != null) {
			loadCacheSql();
		}
	}

	/**
	 * 执行cache-config.xml中配置的sql，并将结果放入缓存
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 */
	private static void loadCacheSql() {
		try {
			Document doc = XmlUtils.createDocument(new File(cacheSqlPath));
			List<?> loginList = XmlUtils.selectNodes(doc, "cacheSql");
			for (int i = 0; i < loginList.size(); i++) {
				Element elem = (Element) loginList.get(i);
				if (elem.getAttributeValue("load").equals("true")) {
					getSqlCache(elem.getAttributeValue("cacheKey"), elem.getTextTrim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据cacheId获取cache-config.xml中配置的一条sql
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param cacheId
	 * @return cache-config.xml中配置的一条sql
	 */
	private static String getCacheSql(String cacheId) {
		try {
			Document doc = XmlUtils.createDocument(new File(cacheSqlPath));
			List<?> loginList = XmlUtils.selectNodes(doc, "cacheSql");
			for (int i = 0; i < loginList.size(); i++) {
				Element elem = (Element) loginList.get(i);
				if (elem.getAttributeValue("load").equals("true")) {
					if (elem.getAttributeValue("cacheKey").equals(cacheId)) {
						return elem.getTextTrim();
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * 获取缓存对象的值
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param type
	 * @param cacheValue
	 * @return 缓存对象的值
	 */
	public static String getCacheText(String type, String cacheValue) {
		String text = null;
		RowSet rs = cachesMap.get(type);
		if (rs != null) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String value = rs.getString(i, "VALUE");
				if (cacheValue.equals(value)) {
					text = rs.getString(i, "TEXT");
					break;
				}
			}
		}
		return text;
	}
	
	/**
	 * 根据type获取缓存对象<br>
	 * 如果不存在，则到TBL_DICT_LIST表中查找DICT_LIST_TYPE=type的数据，并执行加载缓存<br>
	 * 如果仍然不存在，则根据type到cache-config.xml中获取一条sql语句，并执行加载缓存<br>
	 * 如果以上条件都不存在，则返回空的RowSet
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param type
	 * @return 缓存对象RowSet
	 * @throws Exception
	 */
	public static RowSet getCache(String type) throws Exception {
		RowSet rs = cachesMap.get(type);
		if (rs == null) {
			rs = getDictListCache(type);
			if (rs.getRowCount() == 0) {
				rs = getSqlCache(type);
			}
		}
		return rs;
	}
	
	/**
	 * 到TBL_DICT_LIST表中查找DICT_LIST_TYPE=type的数据，并执行加载缓存
	 * @Title: getDictListCache
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月9日 上午10:46:47 
	 * @param persistence
	 * @param type
	 * @return
	 * @throws Exception
	 * @return: RowSet
	 */
	private static RowSet getDictListCache(String type) throws Exception{
		RowSet rs = null;
		DataSource dataSource = (DataSource) BeanUtils.getBean("dataSource");
		Persistence persistence = new Persistence(dataSource);
		StringBuffer buffer = new StringBuffer(128);
		buffer.append("SELECT ");
		buffer.append(ConfigManager.getConstant(Constant.CACHE_VALUE_CONSTANT));
		buffer.append(" AS VALUE, ");
		buffer.append(ConfigManager.getConstant(Constant.CACHE_TEXT_CONSTANT));
		buffer.append(" AS TEXT ");
		buffer.append(" FROM ");
		buffer.append(ConfigManager.getConstant(Constant.CACHE_TABLE_CONSTANT));
		buffer.append(" WHERE ");
		buffer.append(ConfigManager.getConstant(Constant.CACHE_TYPE_CONSTANT));
		buffer.append("=? ");
		final String order = ConfigManager.getConstant(Constant.CACHE_ORDER_CONSTANT);
		if(order !=null && order.length()>0){
			buffer.append(ConfigManager.getConstant(Constant.CACHE_ORDER_CONSTANT));
		}
		persistence.setSql(buffer.toString());
		Object[] args = { type };
		rs = persistence.query(args);
		if (rs.getRowCount() > 0) {
			cachesMap.put(type, rs);
		}
		return rs;
	}
	
	/**
	 * 执行cache-config.xml中的sql语句，并执行加载缓存
	 * @Title: getSqlCache
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月9日 上午10:47:08 
	 * @param persistence
	 * @param type
	 * @param sql
	 * @return
	 * @throws Exception
	 * @return: RowSet
	 */
	private static RowSet getSqlCache(String type,String sql) throws Exception{
		RowSet rs = null;
		DataSource dataSource = (DataSource) BeanUtils.getBean("dataSource");
		Persistence persistence = new Persistence(dataSource);
		persistence.setSql(sql);
		rs = persistence.query();
		if (rs.getRowCount() > 0) {
			cachesMap.put(type, rs);
		}
		return rs;
	}
	
	/**
	 * 根据type到cache-config.xml中获取一条sql语句，并执行加载缓存
	 * @Title: getSqlCache
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月9日 上午10:48:41 
	 * @param persistence
	 * @param type
	 * @return
	 * @throws Exception
	 * @return: RowSet
	 */
	private static RowSet getSqlCache(String type) throws Exception{
		String sql = getCacheSql(type);
		return getSqlCache(type,sql);
	}
	
	/**
	 * 获取TBL_DICT_LIST的DICT_LIST_TYPE字段集合
	 * @Title: getDictListType
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月9日 上午10:49:17 
	 * @param persistence
	 * @return
	 * @throws Exception
	 * @return: RowSet
	 */
	private static RowSet getDictListType() throws Exception{
		DataSource dataSource = (DataSource) BeanUtils.getBean("dataSource");
		Persistence persistence = new Persistence(dataSource);
		persistence.setSql("SELECT DISTINCT("+ConfigManager.getConstant(Constant.CACHE_TYPE_CONSTANT)+") AS TYPE FROM "+ConfigManager.getConstant(Constant.CACHE_TABLE_CONSTANT));
		return persistence.query();
	}
}
