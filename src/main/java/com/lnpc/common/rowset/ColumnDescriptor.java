package com.lnpc.common.rowset;

import java.io.Serializable;

import com.lnpc.common.config.Constant;
import com.lnpc.common.utils.DateUtils;
import com.lnpc.manage.ConfigManager;

/**
 * 列描述对象
 * 
 * @author changjq
 * 
 */
public class ColumnDescriptor implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3112949332802687019L;
	/**
	 * 索引
	 */
	private String index;
	/**
	 * 别名 可以自定义 前台JSP页面绑定此字段
	 */
	private String alias;
	/**
	 * 字段名
	 */
	private String name;
	/**
	 * 字段类型
	 */
	private String type;
	/**
	 * 类型长度
	 */
	private String precision;
	/**
	 * 是否为主键
	 */
	private String key;
	/**
	 * 表名
	 */
	private String table;
	/**
	 * 运算符号 用于自动拼接查询条件
	 */
	private String sign;
	/**
	 * 描述 暂未用到
	 */
	private String descrip;

	/**
	 * 连接内容
	 */
	private String join;

	/**
	 * 连接条件
	 */
	private String on;

	public ColumnDescriptor() {

	}

	public ColumnDescriptor(String index, String alias, String name, String type, String precision, String key, String table, String sign, String descrip, String join, String on) {
		this.index = index;
		this.alias = alias;
		this.name = name;
		this.type = type;
		this.precision = precision;
		this.key = key;
		this.table = table;
		this.sign = sign;
		this.descrip = descrip;
		this.join = join;
		this.on = on;
	}

	/**
	 * 获取别名（与JSP绑定字段名称对应）
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 别名
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * 获设置别名（与JSP绑定字段名称对应）
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 获取数据库字段名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 数据库字段名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置数据库字段名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取字段类型长度
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 字段类型长度
	 */
	public String getPrecision() {
		return precision;
	}

	/**
	 * 设置字段类型长度
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param precision
	 */
	public void setPrecision(String precision) {
		this.precision = precision;
	}

	/**
	 * 获取字段类型
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 字段类型
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置字段类型
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取索引
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 索引
	 */
	public String getIndex() {
		return index;
	}

	/**
	 * 设置索引
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param index
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * 获取是否主键
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return "true" 是;"false" 不是
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置是否主键，"true" 是;"false" 不是
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取表名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 表名
	 */
	public String getTable() {
		return table;
	}

	/**
	 * 设置表名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * 获取描述
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 描述
	 */
	public String getDescrip() {
		return descrip;
	}

	/**
	 * 设置描述
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param descrip
	 */
	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	/**
	 * 获取运算符号
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 运算符号
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * 设置运算符号
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param sign
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}

	public String getOn() {
		return on;
	}

	public void setOn(String on) {
		this.on = on;
	}

	/**
	 * 克隆
	 */
	public Object clone() {
		ColumnDescriptor columnDescriptor = new ColumnDescriptor();
		columnDescriptor.index = this.index;
		columnDescriptor.alias = this.alias;
		columnDescriptor.name = this.name;
		columnDescriptor.type = this.type;
		columnDescriptor.type = this.type;
		columnDescriptor.precision = this.precision;
		columnDescriptor.key = this.key;
		columnDescriptor.precision = this.precision;
		columnDescriptor.table = this.table;
		columnDescriptor.sign = this.sign;
		columnDescriptor.descrip = this.descrip;
		columnDescriptor.join = this.join;
		columnDescriptor.on = this.on;
		return columnDescriptor;
	}

	protected String buildQueryCondtion(String clientValue) {
		StringBuffer bufferCondition = new StringBuffer(256);
		if (!"".equals(this.sign)) {
			final String value = clientValue.trim();
			bufferCondition.append(this.table);
			bufferCondition.append(RowSetUtils.SQL_POINT);
			bufferCondition.append(this.name);
			bufferCondition.append(" ");
			if ("date".equals(this.type)) {// 处理时间类型
				if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
					String format = DateUtils.dateStr2FormatStrForOracle(value);
					if (">=".equals(this.sign) || "<=".equals(this.sign)) {
						bufferCondition.append(this.sign);
						bufferCondition.append(" to_date('");
						bufferCondition.append(value);
						bufferCondition.append("',");
						bufferCondition.append(format);
						bufferCondition.append(") ");

					} else if ("=".equals(this.sign)) {
						bufferCondition = new StringBuffer(128);
						bufferCondition.append("to_char(");
						bufferCondition.append(this.table);
						bufferCondition.append(RowSetUtils.SQL_POINT);
						bufferCondition.append(this.name);
						bufferCondition.append(",'");
						bufferCondition.append(format);
						bufferCondition.append("')");
						bufferCondition.append(this.sign);
						bufferCondition.append(" '");
						bufferCondition.append(value);
						bufferCondition.append("' ");
					}

				} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
					bufferCondition.append(this.sign);
					bufferCondition.append(" '");
					bufferCondition.append(value);
					bufferCondition.append("' ");
				}
			} else {
				if ("like".equals(this.sign)) {// 处理模糊查询
					bufferCondition.append("like '%");
					bufferCondition.append(value);
					bufferCondition.append("%' ");
				} else if ("rlike".equals(this.sign)) {// 处理右模糊查询
					bufferCondition.append("like '");
					bufferCondition.append(value);
					bufferCondition.append("%' ");
				} else if ("llike".equals(this.sign)) {// 处理左模糊查询
					bufferCondition.append("like '%");
					bufferCondition.append(value);
					bufferCondition.append("' ");
				} else {
					if ("integer".equals(this.type) || "numeric".equals(this.type)) {
						bufferCondition.append(this.sign);
						bufferCondition.append(" ");
						bufferCondition.append(value);
						bufferCondition.append(" ");
					} else if ("varchar".equals(this.type)) {
						bufferCondition.append(this.sign);
						bufferCondition.append(" '");
						bufferCondition.append(value);
						bufferCondition.append("' ");
					} else {
						bufferCondition.append(this.sign);
						bufferCondition.append(" '");
						bufferCondition.append(value);
						bufferCondition.append("' ");
					}
				}
			}
		}
		return bufferCondition.toString();
	}
}
