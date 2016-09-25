package com.lnpc.common.utils;

import com.lnpc.common.config.Constant;
import com.lnpc.manage.ConfigManager;

/**
 * 资源相关工具类
 * 
 * @author changjq
 * 
 */
public class ResourceUtils {
	/**
	 * 获取rowset文件路径
	 * 
	 * @author changjq
	 * @param rowSetName
	 * @return rowset文件路径
	 */
	public static String getRowSetPath(String rowSetName) {
		return getRowSetRootPath() + rowSetName + ".xml";
	}

	/**
	 * 获取rowset根目录
	 * 
	 * @author changjq
	 * @return rowset根目录
	 */
	public static String getRowSetRootPath() {
		return ConfigManager.getSysPath(Constant.ROWSET_PATH);
	}
}
