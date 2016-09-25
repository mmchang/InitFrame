package com.lnpc.init;

import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import com.lnpc.common.cache.Cache;
import com.lnpc.common.cache.CacheManagerFactory;
import com.lnpc.common.config.Constant;
import com.lnpc.common.persistence.Persistence;
import com.lnpc.common.rowset.RowSet;
import com.lnpc.common.rowset.RowSetXMLCacheImpl;
import com.lnpc.common.utils.BeanUtils;
import com.lnpc.manage.CacheManager;
import com.lnpc.manage.ConfigManager;

/**
 * 启动加载类（初始化）
 * 
 * @author changjq
 * 
 */
public class InitialServlet extends HttpServlet {

	private static final long serialVersionUID = -3648045921150437356L;

	/**
	 * 初始化必要系统参数<br>
	 * 加载spring配置文件<br>
	 * 加载RowSet xml文件,并放入缓存<br>
	 * 如果需要，加载数据字典到缓存。取决于此servlet在 web.xml中的配置
	 */
	@Override
	public void init() {
		try {
			if (!"".equals(getInitParameter(Constant.DataBase.DATABASE_TYPE))) {
				ConfigManager.setConstant(Constant.DataBase.DATABASE_TYPE, getInitParameter(Constant.DataBase.DATABASE_TYPE));
			}
			String rootPath = getServletContext().getRealPath("/");
			ConfigManager.setSysPath(Constant.CONTEXT_PATH, rootPath);
			Enumeration<?> namesEum = this.getInitParameterNames();
			while (namesEum.hasMoreElements()) {
				String name = (String) namesEum.nextElement();
				if (name.endsWith("_PATH")) {
					ConfigManager.setSysPath(name, rootPath + getInitParameter(name));
				}else if(name.endsWith("_CONSTANT")){
					ConfigManager.setConstant(name, getInitParameter(name));
				}
			}
			/**
			 * load the config xml file of spring
			 */
			BeanUtils.setServletContext(getServletContext());
			/** load RowSet XML **/
			Cache rowsetXMLCache = new RowSetXMLCacheImpl();
			rowsetXMLCache.load();

			/** set RowSet to cache **/
			CacheManagerFactory.getCacheManager().set(RowSetXMLCacheImpl.cacheName, rowsetXMLCache);

			/**
			 * load the properties to cahces
			 */
			if ("true".equals(getInitParameter("LOAD_CACHE"))) {
				CacheManager.load(ConfigManager.getSysPath(Constant.CACHE_PATH));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
