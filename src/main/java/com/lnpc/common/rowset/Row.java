package com.lnpc.common.rowset;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 行
 * 
 * @author changjq
 * 
 */
public class Row implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2479318618882830250L;

	/**
	 * 存储列 字段名：值
	 */
	private Map<String, Object> valuesMap = new HashMap<String, Object>();

	/**
	 * 行状态 参阅RowConstant
	 */
	private String status = "false";

	/**
	 * 是否选中
	 */
	private String check = "false";

	/**
	 * 判断是否包含字段名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param columnName
	 * @return true 包含;false 不包含
	 */
	protected boolean isContainColumn(String columnName) {
		return valuesMap.containsKey(columnName);
	}

	/**
	 * 获取字段值
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param columnName
	 * @return 字段值
	 */
	public String getColumnValue(String columnName) {
		Object value = valuesMap.get(columnName);
		if (value == null) {
			return null;
		}
		return String.valueOf(value);
	}

	/**
	 * 设置字段值
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param columName
	 * @param value
	 */
	public void setColumnValue(String columName, Object value) {
		setColumnValue(columName, value, false);
	}

	/**
	 * 设置字段值
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param columName
	 * @param value
	 * @param isUserAlias
	 *            true 设置行状态为RowConstant.MODIFY_STATUS<br>
	 */
	public void setColumnValue(String columName, Object value, boolean isUserAlias) {
		if (!getStatus().equals(RowConstant.NEW_STATUS)) {
			if (!isUserAlias) {
				setStatus(RowConstant.MODIFY_STATUS);
			}
		}
		valuesMap.put(columName, value);
	}

	/**
	 * 获取行状态
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 行状态
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置行状态
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 判断行是否选中
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return "true" 选中;"false" 未选择
	 */
	public String getCheck() {
		return check;
	}

	/**
	 * 设置行是否选择<br>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param check
	 *            "true" 选中;"false" 未选择
	 */
	public void setCheck(String check) {
		this.check = check;
	}

	/**
	 * 字段名转数组
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	public Object[] toArray() {
		return valuesMap.keySet().toArray();
	}

	/**
	 * 
	 * <p>
	 * Description:构建JSON
	 * </p>
	 * 
	 * @return
	 * @author changjq
	 * @date 2016年9月25日
	 */
	public JSONObject toJSON(int rowId) {
		JSONObject jsonCur = (JSONObject) JSON.toJSON(valuesMap);
		JSONObject tag = new JSONObject();
		tag.put("check", check);
		tag.put("status", status);
		tag.put("rowId", rowId);
		jsonCur.put("tag", tag);
		jsonCur.put("original", valuesMap);
		return jsonCur;
	}

	/**
	 * 
	 * <p>
	 * Description:返回json字符串形式
	 * </p>
	 * 
	 * @param rowId
	 * @return
	 * @author changjq
	 * @date 2016年9月25日
	 */
	public String toJSONString(int rowId) {
		return JSON.toJSONString(this.toJSON(rowId));
	}

	public Row clone() {
		Row row = new Row();
		Set<String> set = this.valuesMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = this.valuesMap.get(key);
			row.setColumnValue(key, value);
		}
		row.setCheck(this.check);
		row.setStatus(this.status);
		return row;
	}

}
