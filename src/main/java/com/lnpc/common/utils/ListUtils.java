package com.lnpc.common.utils;

import java.util.List;

/**
 * List相关工具类
 * 
 * @author changjq
 * 
 */
public class ListUtils {
	/**
	 * 判断list是否为空或空字符串
	 * 
	 * @author changjq
	 * @param list
	 * @return true 不为null并且size>0; false 为null或者是size==0
	 */
	public static boolean notNullAndEmpty(List<?> list) {
		if (list != null && list.size() != 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param list
	 * @return true 为null或者是size==0;false 不为null并且size>0
	 */
	public static boolean nullOrEmpty(List<?> list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}
}
