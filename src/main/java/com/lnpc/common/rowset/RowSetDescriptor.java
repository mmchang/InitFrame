package com.lnpc.common.rowset;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.ListOrderedMap;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RowSet描述类
 * 
 * @author changjq
 * 
 */
public class RowSetDescriptor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493801740652388619L;

	private ListOrderedMap columns = new ListOrderedMap();

	private ListOrderedMap columnsTypes = new ListOrderedMap();

	private String condition;

	private String order;

	private long lastModified = 0l;

	private String rowSetName;

	private Logger logger = LoggerFactory.getLogger(RowSetDescriptor.class);

	/**
	 * 
	 */
	public RowSetDescriptor() {

	}

	/**
	 * 
	 * @param rowSetName
	 */
	public RowSetDescriptor(String rowSetName) {
		this.rowSetName = rowSetName;
		this.init();
	}

	/**
	 * 
	 * <p>
	 * Description: 初始化操作
	 * </p>
	 * 
	 * @author changjq
	 * @date 2016年9月24日
	 */
	private void init() {
		Element elem = RowSetUtils.getRowSetRoot(this);
		List<?> columnset = elem.elements();
		for (Object obj : columnset) {
			Element child = (Element) obj;
			if (RowConstant.COLUMNSET_STR.equals(child.getName())) {
				this.initColumn(child);
			} else if (RowConstant.CONDITION_STR.equals(child.getName())) {
				this.initCondition(child);
			} else if (RowConstant.ORDER_STR.equals(child.getName())) {
				this.initOrder(child);
			}
		}
		logger.info("RowSet:{} has been loaded... ", rowSetName);
	}

	/**
	 * 
	 * <p>
	 * Description: 初始化列
	 * </p>
	 * 
	 * @param element
	 * @author changjq
	 * @date 2016年9月24日
	 */
	private void initColumn(Element element) {
		if (element != null) {
			String alias = null;
			String type = null;
			List<?> columns = element.elements(RowConstant.COLUMN_STR);
			for (Object obj : columns) {
				Element child = (Element) obj;
				alias = child.attributeValue("alias");
				type = child.attributeValue("type");
				ColumnDescriptor columnDescriptor = new ColumnDescriptor(child.attributeValue("index"), alias, child.attributeValue("name"), type, child.attributeValue("precision"),
						child.attributeValue("key"), child.attributeValue("table"), child.attributeValue("sign"), child.attributeValue("descrip"), child.attributeValue("join"),
						child.attributeValue("on"));
				columnsTypes.put(alias, type);
				this.addColumn(columnDescriptor);
			}
		}
	}

	/**
	 * 
	 * <p>
	 * Description:初始化RowSet xml中的condition
	 * </p>
	 * 
	 * @param element
	 * @author changjq
	 * @date 2016年9月24日
	 */
	private void initCondition(Element element) {
		if (element != null) {
			this.setCondition(element.getTextTrim());
		}
	}

	/**
	 * 
	 * <p>
	 * Description:初始化RowSet xml中的order
	 * </p>
	 * 
	 * @param element
	 * @author changjq
	 * @date 2016年9月24日
	 */
	private void initOrder(Element element) {
		if (element != null) {
			this.setOrder(element.getTextTrim());
		}
	}

	/**
	 * 
	 * <p>
	 * Description: 返回列类型
	 * </p>
	 * 
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getColumnTypes() {
		return columnsTypes;
	}

	/**
	 * <p>
	 * Description: 获取列数量
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	public int getColumnCount() {
		return columns.size();
	}

	/**
	 * <p>
	 * Description: 根据名称获取列
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @return
	 */
	public ColumnDescriptor getColumn(String name) {
		return (ColumnDescriptor) columns.get(name);
	}

	/**
	 * <p>
	 * Description: 根据索引获取列
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param index
	 * @return
	 */
	public ColumnDescriptor getColumn(int index) {
		return (ColumnDescriptor) columns.getValue(index);
	}

	/**
	 * <p>
	 * Description: 添加一列
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param column
	 */
	public void addColumn(ColumnDescriptor column) {
		columns.put(column.getAlias(), column);
	}

	/**
	 * <p>
	 * Description: 获取RowSet xml中的condition
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	protected String getCondition() {
		return condition;
	}

	/**
	 * <p>
	 * Description: 设置RowSet xml中的condition
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param condition
	 */
	protected void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * <p>
	 * Description: 获取RowSet xml中的order
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	protected String getOrder() {
		return order;
	}

	/**
	 * <p>
	 * Description: 设置RowSet xml中的order
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param order
	 */
	protected void setOrder(String order) {
		this.order = order;
	}

	/**
	 * <p>
	 * Description: 设置RowSet xml的lastModified
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param lastModified
	 */
	protected void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * <p>
	 * Description: 获取RowSet xml的lastModified
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param lastModified
	 * @return
	 */
	public boolean isModified(long lastModified) {
		return lastModified > this.lastModified;
	}

	/**
	 * <p>
	 * Description: 获取RowSet名称
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return
	 */
	protected String getRowSetName() {
		return rowSetName;
	}

	/**
	 * <p>
	 * Description: 设置RowSet名称
	 * </p>
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param rowSetName
	 */
	protected void setRowSetName(String rowSetName) {
		this.rowSetName = rowSetName;
	}
}
