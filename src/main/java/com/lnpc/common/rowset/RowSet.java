package com.lnpc.common.rowset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * RowSet
 * 
 * @author changjq
 * 
 */
public class RowSet implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4175955300811546335L;
	/**
	 * 存储行数据
	 */
	private List<Row> rowBuffer = new ArrayList<Row>();

	/**
	 * 存储行ID
	 */
	private List<Integer> rowIdBuffer = new ArrayList<Integer>();

	/**
	 * 名称
	 */
	private String rowSetName = null;

	/**
	 * 别名
	 */
	private String alias = null;

	/**
	 * 描述
	 */
	private RowSetDescriptor rowsetDescript = null;

	/**
	 * 条件(RowSet.xml condition标签内容)
	 */
	private String strCondition = null;

	/**
	 * 总数据条数
	 */
	private int totalProperty;

	/**
	 * 当前页
	 */
	private int currentPage = 1;

	/**
	 * 总页数
	 */
	private int totalPage;

	/**
	 * 每页显示数据条数
	 */
	private int sizePerPage = 0;

	public RowSet() {

	}

	public RowSet(String rowSetName) {
		this.rowsetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
		this.rowSetName = rowSetName;
		this.alias = rowSetName;
	}

	/**
	 * 添加空行
	 * 
	 * @author changjq
	 * @return 添加行的索引
	 */
	public int addRow() {
		return insertRow(getRowCount());
	}

	/**
	 * 添加行row
	 * 
	 * @author changjq
	 * @param row
	 * @return 添加行的索引
	 */
	public int addRow(Row row) {
		int index = getRowCount();
		insertRow(index, row);
		return index;
	}

	/**
	 * 添加行id
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param id
	 */
	public void addRowId(int id) {
		this.rowIdBuffer.add(id);
	}

	/**
	 * 指定位置添加空行
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @return 添加行的索引
	 */
	public int insertRow(int rowIndex) {
		Row row = new Row();
		// row.setRowId(getRowCount());
		initialRow(row);
		insertRow(rowIndex, row);
		return rowIndex;
	}

	/**
	 * 指定位置添加指定行
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param row
	 */
	public void insertRow(int rowIndex, Row row) {
		// row.setRowId(getRowCount());
		rowBuffer.add(rowIndex, row);
	}

	/**
	 * 初始化空行数据
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param row
	 */
	private void initialRow(Row row) {
		if (rowsetDescript != null) {
			for (int i = 0; i < this.rowsetDescript.getColumnCount(); i++) {
				ColumnDescriptor column = this.rowsetDescript.getColumn(i);
				// row.setColumnName(i, column.getAlias());
				row.setColumnValue(column.getAlias(), "");
				row.setStatus(RowConstant.NEW_STATUS);
			}
		}
	}

	/**
	 * 根据索引获取行
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @return 行
	 */
	public Row getRowByIndex(int rowIndex) {
		try {
			Row row = rowBuffer.get(rowIndex);
			if (row == null) {
				throw new Exception("The row is not exist!");
			}
			return row;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据行id获取行
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param id
	 * @return 行
	 */
	public Row getRowById(int id) {
		int size = rowIdBuffer.size();
		Row row = null;
		for (int i = 0; i < size; i++) {
			int rowId = rowIdBuffer.get(i);
			if (rowId == id) {
				row = this.rowBuffer.get(i);
				break;
			}
		}
		return row;
	}

	/**
	 * 根据索引删除行
	 * 
	 * @author changjq
	 * @param rowIndex
	 */
	public void deleteRowByIndex(int rowIndex) {
		Object delObj = rowBuffer.remove(rowIndex);
		if (delObj != null) {
			this.totalProperty--;
			this.totalPage = (this.totalProperty + this.sizePerPage - 1) / this.sizePerPage;
		}
	}

	/**
	 * 获取指定行的字段值
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param columName
	 * @return 指定行的字段值
	 */
	public String getString(int rowIndex, String columName) {
		Row row = getRowByIndex(rowIndex);
		return row.getColumnValue(columName);
	}

	/**
	 * 获取指定行的字段值，如果为null，则返回默认值strNullValue
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param columnName
	 * @param strNullValue
	 * @return 指定行的字段值，如果为null，则返回默认值strNullValue
	 */
	public String getString(int rowIndex, String columnName, String strNullValue) {
		String value = getString(rowIndex, columnName);
		return value == null ? strNullValue : value;
	}

	/**
	 * 获取指定行的整形字段值
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param columnName
	 * @return 指定行的整形字段值
	 */
	public int getInt(int rowIndex, String columnName) {
		String value = getString(rowIndex, columnName);
		if (value == null || "".equals(value)) {
			return 0;
		} else {
			return Integer.parseInt(value);
		}
	}

	/**
	 * 获取指定行的浮点字段值
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param columName
	 * @return 指定行的浮点字段值
	 */
	public float getFloat(int rowIndex, String columName) {
		String value = getString(rowIndex, columName);
		if (value == null || "".equals(value)) {
			return 0f;
		} else {
			return Float.parseFloat(value);
		}
	}

	/**
	 * 获取指定行的双精度字段值
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param columName
	 * @return 指定行的双精度字段值
	 */
	public double getDouble(int rowIndex, String columName) {
		String value = getString(rowIndex, columName);
		if (value == null || "".equals(value)) {
			return 0d;
		} else {
			return Double.parseDouble(value);
		}
	}

	/**
	 * 设置指定行的字段值
	 * 
	 * @author changjq
	 * @param rowIndex
	 * @param columName
	 * @param value
	 */
	public void setString(int rowIndex, String columName, String value) {
		Row row = getRowByIndex(rowIndex);
		boolean isUserAlias = false;
		if (rowsetDescript != null) {
			ColumnDescriptor columDescripter = rowsetDescript.getColumn(columName);
			if (columDescripter != null) {
				String table = columDescripter.getTable();
				if ("".equals(table)) {
					isUserAlias = true;
				}
			}
		}
		row.setColumnValue(columName, value, isUserAlias);
	}

	/**
	 * 获取行数
	 * 
	 * @author changjq
	 * @return 行数
	 */
	public int getRowCount() {
		return rowBuffer.size();
	}

	/**
	 * 获取字段总数
	 * 
	 * @author changjq
	 * @return 字段总数
	 */
	public int getColumCount() {
		return (rowsetDescript != null) ? rowsetDescript.getColumnCount() : 0;
	}

	/**
	 * 获取RowSet对象名称
	 * 
	 * @author changjq
	 * @return RowSet对象名称
	 */
	public String getName() {
		return rowSetName;
	}

	/**
	 * 设置RowSet对象名称，并加载RowSet描述
	 * 
	 * @author changjq
	 * @param rowSetName
	 */
	public void setName(String rowSetName) {
		this.rowSetName = rowSetName;
		this.alias = rowSetName;
		if (this.rowsetDescript == null) {
			this.rowsetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
		}
		// int length = rowsetDescript.getColumnCount();
		// for (int i = 0; i < length; i++) {
		// ColumnDescriptor columnDescript = rowsetDescript.getColumn(i);
		// String alais = columnDescript.getAlias();
		// for (int j = 0; j < getRowCount(); i++) {
		// Row row = (Row) getRowByIndex(j);
		// if(!row.isContainColumn(alais)){
		// this.setString(j, alais, null);
		// }
		// }
		// }
	}

	/**
	 * 获取别名
	 * 
	 * @author changjq
	 * @date 2015年6月26日
	 * @return 别名
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * 设置别名
	 * 
	 * @author changjq
	 * @date 2015年6月26日
	 * @param alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 获取RowSet查询条件
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return RowSet查询条件
	 */
	public String getCondition() {
		if (strCondition != null) {
			return strCondition;
		} else {
			return (rowsetDescript != null) ? rowsetDescript.getCondition() : "";
		}
	}

	/**
	 * 设置RowSet查询条件
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param condition
	 */
	public void setCondition(String condition) {
		this.strCondition = condition;
	}

	/**
	 * 获取RowSet排序字段
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return RowSet排序字段
	 */
	public String getOrder() {
		return (rowsetDescript != null) ? rowsetDescript.getOrder() : "";
	}

	/**
	 * 设置RowSet排序字段
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param order
	 */
	public void setOrder(String order) {
		if (rowsetDescript != null) {
			rowsetDescript.setOrder(order);
		}
	}

	/**
	 * 构建查询条件
	 * 
	 * @author changjq
	 * @return 查询条件
	 */
	public String buildQueryConditon() {
		StringBuffer bufferCondition = new StringBuffer(256);
		int rowLength = this.getRowCount();
		if (rowLength > 0) {
			Row row = this.getRowByIndex(0);
			Object arry[] = row.toArray();
			for (int j = 0; j < arry.length; j++) {
				String column = (String) arry[j];
				String value = row.getColumnValue(column);
				if (!"".equals(value) && value != null) {
					ColumnDescriptor columDescript = rowsetDescript.getColumn(column);
					bufferCondition.append(columDescript.buildQueryCondtion(value));
					bufferCondition.append(RowSetUtils.SQL_AND);
				}
			}
			if (bufferCondition.length() > 0) {
				return bufferCondition.substring(0, bufferCondition.length() - RowSetUtils.SQL_AND.length());
			}
		}
		return bufferCondition.toString();
	}

	/**
	 * 获取当前查询下总数据条数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 当前查询下总数据条数
	 */
	public int getTotalProperty() {
		return totalProperty;
	}

	/**
	 * 设置当前查询下总数据条数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param totalProperty
	 */
	public void setTotalProperty(int totalProperty) {
		this.totalProperty = totalProperty;
	}

	/**
	 * 获取当前页数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 当前页数
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * 设置当前页数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * 获取总页数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 总页数
	 */
	public int getTotalPage() {
		return totalPage;
	}

	/**
	 * 设置总页数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param totalPage
	 */
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	/**
	 * 获取每页数据条数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 每页数据条数
	 */
	public int getSizePerPage() {
		return sizePerPage;
	}

	/**
	 * 设置每页数据条数
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param sizePerPage
	 */
	public void setSizePerPage(int sizePerPage) {
		this.sizePerPage = sizePerPage;
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
	public JSONObject toJSON() {
		JSONObject map = new JSONObject();
		map.put("name", this.getName());
		map.put("alias", this.getAlias());
		map.put("order", this.getOrder());
		map.put("condition", this.getCondition());
		map.put("totalcount", this.getRowCount());
		map.put("sizePerPage", this.getSizePerPage());
		map.put("totalProperty", this.getTotalProperty());
		map.put("totalPage", this.getTotalPage());
		map.put("currentPage", this.getCurrentPage());
		if (this.rowsetDescript != null) {
			map.put("columntype", this.rowsetDescript.getColumnTypes());
		}
		JSONObject rows[] = new JSONObject[getRowCount()];
		for (int i = 0; i < getRowCount(); i++) {
			Row row = (Row) getRowByIndex(i);
			rows[i] = row.toJSON(i);
		}
		map.put("rows", rows);
		return map;
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
	public String toJSONString() {
		return JSON.toJSONString(this.toJSON());
	}

	/**
	 * 向当前RowSet添加一个RowSet对象
	 * 
	 * @author changjq
	 * @date 2015年6月15日
	 * @param newRs
	 * @return RowSet
	 */
	public RowSet addAll(RowSet newRs) {
		return this.addAll(newRs, false);
	}

	/**
	 * 向当前RowSet添加一个RowSet对象
	 * 
	 * @author changjq
	 * @date 2015年6月15日
	 * @param newRs
	 * @param clean
	 *            是否需要清除原始数据
	 * @return RowSet
	 */
	public RowSet addAll(RowSet newRs, boolean clean) {
		if (clean) {
			this.rowBuffer.clear();
		}
		if (newRs == null) {
			return this;
		}
		if (!this.getName().equals(newRs.getName())) {
			return this;
		}
		for (int i = 0; i < newRs.getRowCount(); i++) {
			this.addRow(newRs.getRowByIndex(i));
		}
		return this;
	}

	public RowSet clone() {
		RowSet rs = null;
		if (this.rowSetName == null) {
			rs = new RowSet();
		} else {
			rs = new RowSet(this.rowSetName);
		}
		rs.strCondition = this.strCondition;
		rs.totalProperty = this.totalProperty;
		rs.currentPage = this.currentPage;
		rs.totalPage = this.totalPage;
		rs.sizePerPage = this.sizePerPage;
		for (int i = 0; i < rowBuffer.size(); i++) {
			Row r = rowBuffer.get(i);
			if (rowIdBuffer.size() > 0) {
				int cloneRowId = rowIdBuffer.get(i);
				rs.addRowId(cloneRowId);
			}
			rs.insertRow(i, r.clone());
		}
		return rs;
	}

	public ColumnDescriptor getColumnDescriptor(String columnName) {
		return this.rowsetDescript.getColumn(columnName);
	}
}
