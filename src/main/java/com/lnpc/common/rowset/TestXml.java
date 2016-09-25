package com.lnpc.common.rowset;

import com.lnpc.common.cache.Cache;
import com.lnpc.common.cache.CacheManagerFactory;
import com.lnpc.common.config.Constant;
import com.lnpc.manage.ConfigManager;

public class TestXml {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConfigManager.setSysPath(Constant.ROWSET_PATH, "E:\\project\\");
		ConfigManager.setConstant(Constant.DataBase.DATABASE_TYPE, "ORACLE");
		Cache rowsetXMLCache = new RowSetXMLCacheImpl();
		rowsetXMLCache.load();
		CacheManagerFactory.getCacheManager().set(RowSetXMLCacheImpl.cacheName, rowsetXMLCache);
		RowSet rs = new RowSet("TEST");
		Row row = new Row();
		row.setColumnValue("E_ID", "1");
		row.setColumnValue("E_NAME", "namefortest");
		row.setColumnValue("E_BEGIN", "2015-02-05 10:20");
		row.setColumnValue("E_END", "2015-02-06 20:30:15");
		rs.addRow(row);
		System.out.println(rs.buildQueryConditon());
		// JSONObject jsonObj = JSON.parseObject(str);
		// System.out.println(jsonObj.get("rows"));
	}

}
