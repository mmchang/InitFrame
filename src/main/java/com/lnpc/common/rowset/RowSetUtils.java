package com.lnpc.common.rowset;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lnpc.common.config.Constant;
import com.lnpc.common.utils.DateUtils;
import com.lnpc.common.utils.FileUtils;
import com.lnpc.common.utils.ResourceUtils;
import com.lnpc.common.utils.StringUtils;
import com.lnpc.common.utils.XmlUtils;
import com.lnpc.manage.ConfigManager;

/**
 * Class of RowSet Utils
 * 
 * @author changjq
 * 
 */
public class RowSetUtils {
	public static final String SQL_POINT = ".";

	public static final String SQL_AND = " AND ";

	public static final String SQL_OR = "OR";

	protected static final String SQL_SELECT = "SELECT * FROM";

	protected static final String SQL_UPDATE = "UPDATE";

	protected static final String SQL_INSERT = "INSERT INTO";

	protected static final String SQL_DELETE = "DELETE FROM";

	private static final char sign[] = { '[', ']' };

	public static final Logger logger = LoggerFactory.getLogger(RowSetUtils.class);

	/**
	 * 
	 * <p>
	 * Description:获取RowSet文件的根节点
	 * </p>
	 * 
	 * @param rowSetName
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public static Element getRowSetRoot(RowSetDescriptor rowSetDescript) {
		final String rowSetName = rowSetDescript.getRowSetName();
		Element root = null;
		String rowSetPath = ResourceUtils.getRowSetPath(rowSetName);
		if (!FileUtils.isExistFile(rowSetPath)) {
			logger.error("The RowSet: {} is not exits...", rowSetName);
		} else {
			Document doc = XmlUtils.createDocument(new File(rowSetPath));
			if (doc != null) {
				root = doc.getRootElement();
			}
			rowSetDescript.setLastModified(FileUtils.getLastModified(rowSetPath));
		}
		return root;
	}

	/**
	 * 
	 * <p>Description: 多表关联查询</p>
	 * @param rowSetName
	 * @deprecated
	 * @return
	 * @author changjq
	 * @date 2016年9月25日
	 */
	public static String getSelectJoinSql(String rowSetName) {
		String _sql = "", _table, _column, _join, _joinTable, _joinField, _joinTableAlias, _on;
		String joinArr1[] = null;
		String joinArr2[] = null;
		StringBuffer _joinTableCondition = new StringBuffer(512);
		;
		List<String> hsAttr = new ArrayList<String>();
		HashSet<String> hsTable = new HashSet<String>();
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_table = columnDescript.getTable();
				_column = columnDescript.getName();
				_join = columnDescript.getJoin();
				_on = columnDescript.getOn();
				if (!"".equals(_table) && !"".equals(_column)) {
					hsAttr.add(_table + SQL_POINT + _column);
					hsTable.add(_table);
					if (_join != null && !_join.equals("")) {
						joinArr1 = _join.split(",");
						joinArr2 = joinArr1[1].split("\\.");
						_joinTable = joinArr2[0];
						_joinField = joinArr2[1];
						_joinField += " ";
						_joinField += joinArr1[2];
						_joinTableAlias = _joinTable + "_" + i;
						hsAttr.add(_joinTableAlias + SQL_POINT + _joinField);
						_joinTableCondition.append(" LEFT OUTER JOIN ");
						_joinTableCondition.append(_joinTable);
						_joinTableCondition.append(" ");
						_joinTableCondition.append(_joinTableAlias);
						_joinTableCondition.append(" ON ");
						_joinTableCondition.append(_joinTableAlias);
						_joinTableCondition.append(SQL_POINT);
						_joinTableCondition.append(joinArr1[0].split("\\.")[1]);
						_joinTableCondition.append("=");
						_joinTableCondition.append(_table);
						_joinTableCondition.append(SQL_POINT);
						_joinTableCondition.append(_column);
						if (_on != null && !_on.equals("")) {
							_joinTableCondition.append(" AND ");
							_joinTableCondition.append(_on.replaceAll(_joinTable, _joinTableAlias));
						}
					}
				}
			}
			_sql += "select " + hsAttr + " from " + hsTable;
			_sql = StringUtils.replace(_sql, sign);
			if (_joinTableCondition != null && _joinTableCondition.length() > 0) {
				_sql += _joinTableCondition.toString();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return _sql;
	}

	/**
	 * build select sql from rowset
	 * 
	 * @author changjq
	 * @param rowSetName
	 * @return
	 */
	public static String getSelectSql(String rowSetName) {
		String _sql = "", _table, _column;
		List<String> hsAttr = new ArrayList<String>();
		Set<String> hsTable = new HashSet<String>();
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_table = columnDescript.getTable();
				_column = columnDescript.getName();
				if (!"".equals(_table) && !"".equals(_column)) {
					hsAttr.add(_table + SQL_POINT + _column);
					hsTable.add(_table);
				}
			}
			_sql += "select " + hsAttr + " from " + hsTable;
			_sql = StringUtils.replace(_sql, sign);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return _sql;
	}

	/**
	 * build update sql from rowset
	 * 
	 * @Title: getUpdateSql
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq
	 * @date:2015年9月25日 上午9:38:16
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[0]:sql;Object[1]:prepared args
	 */
	public static Object[] getUpdateSql(String rowSetName, Row row) {
		String column, key, value, _table, _conditions = "", _column_values = "", _sql = "", _column_values_asks = "", _conditions_asks = "";
		Object[] retObj = new Object[2];
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			List argsArr = new ArrayList();
			List whereArr = new ArrayList();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				column = columnDescript.getName();
				_table = columnDescript.getTable();
				if (!"".equals(_table) && !"".equals(column)) {
					key = columnDescript.getKey();
					String type = columnDescript.getType();
					value = row.getColumnValue(column);
					if (value == null || "null".equals(value)) {
						continue;
					}
					if (key.equals("true")) {
						_conditions_asks += column + "= ? and ";
						if ("integer".equals(type) || "numeric".equals(type)) {
							whereArr.add(new Integer(value));
							// _conditions += column + "=" + value + " and ";
						} else if ("varchar".equals(type)) {
							whereArr.add(value);
							// _conditions += column + "='" + value + "' and ";
						} else if ("date".equals(type)) {
							String format = "yyyy-MM-dd hh:mm:ss";
							if (value.trim().length() == 10) {
								format = "yyyy-MM-dd";
							} else if (value.trim().length() == 4) {
								format = "yyyy";
							} else if (value.trim().length() == 7) {
								format = "yyyy-MM";
							} else if (value.trim().length() == 13) {
								format = "yyyy-MM-dd HH";
							} else if (value.trim().length() == 16) {
								format = "yyyy-MM-dd HH:mm";
							}
							whereArr.add(new java.sql.Timestamp(DateUtils.string2Date(value, format).getTime()));
							/*
							 * if(ConfigManager.getConstant(Constant.DataBase.
							 * DATABASE_TYPE
							 * ).equals(Constant.DataBase.DATABASE_TYPE_ORACLE
							 * )){ if("".equals(value)){ _conditions += column +
							 * " is null and "; } else{ _conditions +=
							 * "to_char("+column + ",'yyyy-mm-dd')='" + value +
							 * "' and "; } } else
							 * if(ConfigManager.getConstant(Constant
							 * .DataBase.DATABASE_TYPE
							 * ).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)){
							 * if("".equals(value)){ _conditions += column +
							 * " is null and "; } else{ _conditions += column +
							 * "='" + value + "' and "; } }
							 */
						} else {
							// _conditions += column + "='" + value + "' and ";
						}
					} else {
						_column_values_asks += column + "=?,";
						if ("integer".equals(type) || "numeric".equals(type)) {
							if (value != null) {
								if ("".equals(value)) {
									value = "0";
								}
								if (value.contains(".")) {
									argsArr.add(new Double(value));
								} else {
									argsArr.add(new Integer(value));
								}
								// _column_values += column + "=" + value + ",";
							}
						} else if ("varchar".equals(type)) {
							argsArr.add(value);
							// _column_values += column + "='" + value + "',";
						} else if ("date".equals(type)) {
							if ("".equals(value)) {
								argsArr.add(null);
							} else {
								String format = "yyyy-MM-dd HH:mm:ss";
								if (value.trim().length() == 10) {
									format = "yyyy-MM-dd";
								} else if (value.trim().length() == 4) {
									format = "yyyy";
								} else if (value.trim().length() == 7) {
									format = "yyyy-MM";
								} else if (value.trim().length() == 13) {
									format = "yyyy-MM-dd HH";
								} else if (value.trim().length() == 16) {
									format = "yyyy-MM-dd HH:mm";
								}
								argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(value, format).getTime()));
							}
							/*
							 * if(ConfigManager.getConstant(Constant.DataBase.
							 * DATABASE_TYPE
							 * ).equals(Constant.DataBase.DATABASE_TYPE_ORACLE
							 * )){
							 * 
							 * if("".equals(value)){ value = null;
							 * _column_values += column + "=" + value + ","; }
							 * else{ _column_values += column + "=to_date('" +
							 * value + "','yyyy-mm-dd hh24:mi:ss'),"; } } else
							 * if(ConfigManager.getConstant(Constant.DataBase.
							 * DATABASE_TYPE
							 * ).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)){
							 * if("".equals(value)){ value = null;
							 * _column_values += column + "=" + value + ","; }
							 * else{ _column_values += column + "='" + value +
							 * "',"; } }
							 */

						} else {
							argsArr.add(value);
							// _column_values += column + "='" + value + "',";
						}

					}
				}
			}
			// _column_values = _column_values.substring(0,
			// _column_values.length() - 1);
			_column_values_asks = _column_values_asks.substring(0, _column_values_asks.length() - 1);
			if (!"".equals(_conditions_asks)) {
				// _conditions = _conditions.substring(0, _conditions.length() -
				// 4);
				_conditions_asks = _conditions_asks.substring(0, _conditions_asks.length() - 4);
				// _sql += SQL_UPDATE + " " + rowSetName + " set " +
				// _column_values + " where " + _conditions;
				_sql += SQL_UPDATE + " " + rowSetName + " set " + _column_values_asks + " where " + _conditions_asks;
			} else {
				// _sql += SQL_UPDATE + " " + rowSetName + " set " +
				// _column_values;
				_sql += SQL_UPDATE + " " + rowSetName + " set " + _column_values_asks;
			}
			retObj[0] = _sql;
			argsArr.addAll(whereArr);
			retObj[1] = argsArr.toArray();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return retObj;
	}

	/**
	 * build insert sql from rowset
	 * 
	 * @Title: getInsertSql
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq
	 * @date:2015年9月24日 下午4:10:14
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[0]:sql;Object[1]:prepared args
	 */
	public static Object[] getInsertSql(String rowSetName, Row row) {
		String column, value, _table, _columns = "", _values = "", _sql = "", _asks = "";
		Object[] retObj = new Object[2];
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			List argsArr = new ArrayList();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				column = columnDescript.getName();
				String type = columnDescript.getType();
				_table = columnDescript.getTable();
				value = row.getColumnValue(column);
				if (!"".equals(_table) && !"".equals(value) && value != null && !"null".equals(value) && !"".equals(column)) {
					_columns += column + ",";
					_asks += "?,";
					if ("integer".equals(type) || "numeric".equals(type)) {
						if (value.contains(".")) {
							argsArr.add(new Double(value));
						} else {
							argsArr.add(new Integer(value));
						}
						// _values += "" + value + ",";
					} else if ("varchar".equals(type)) {
						argsArr.add(value);
						// _values += "'" + value + "',";
					} else if ("date".equals(type)) {
						String format = "yyyy-MM-dd HH:mm:ss";
						if (value.trim().length() == 10) {
							format = "yyyy-MM-dd";
						} else if (value.trim().length() == 4) {
							format = "yyyy";
						} else if (value.trim().length() == 7) {
							format = "yyyy-MM";
						} else if (value.trim().length() == 13) {
							format = "yyyy-MM-dd HH";
						} else if (value.trim().length() == 16) {
							format = "yyyy-MM-dd HH:mm";
						}
						argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(value, format).getTime()));
						/*
						 * if(ConfigManager.getConstant(Constant.DataBase.
						 * DATABASE_TYPE
						 * ).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)){
						 * if("".equals(value)){ value = null; _values +=
						 * value+","; } else{ _values += "to_date('" + value +
						 * "','yyyy-mm-dd hh24:mi:ss'),"; } } else
						 * if(ConfigManager
						 * .getConstant(Constant.DataBase.DATABASE_TYPE
						 * ).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)){
						 * _values += "'" + value + "',"; }
						 */

					} else if ("clob".equals(type)) {
						argsArr.add(value);
						// argsArr.add(new
						// javax.sql.rowset.serial.SerialClob(value.toCharArray()));
					} else {
						argsArr.add(value);
						// _values += "'" + value + "',";
					}

				}
			}
			// _values = _values.substring(0, _values.length() - 1);
			_columns = _columns.substring(0, _columns.length() - 1);
			_asks = _asks.substring(0, _asks.length() - 1);
			// _sql += SQL_INSERT + " " + rowSetName + "(" + _columns + ") " +
			// "values(" + _values + ")";
			_sql += SQL_INSERT + " " + rowSetName + "(" + _columns + ") " + "values(" + _asks + ")";
			retObj[0] = _sql;
			retObj[1] = argsArr.toArray();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return retObj;
		// return _sql;
	}

	/**
	 * build delete sql from rowset
	 * 
	 * @Title: getDeleteSql
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq
	 * @date:2015年9月25日 上午8:46:42
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[0]:sql;Object[1]:prepared args
	 */
	public static Object[] getDeleteSql(String rowSetName, Row row) {
		String column, key, value, _table, _conditions = "", _sql = "", _preparedConditions = "";
		Object[] retObj = new Object[2];
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			List argsArr = new ArrayList();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				String type = columnDescript.getType();
				column = columnDescript.getName();
				_table = columnDescript.getTable();
				if (!"".equals(_table) && !"".equals(column)) {
					key = columnDescript.getKey();
					value = row.getColumnValue(column);
					if (value == null || "".equals(value) || "null".equals(value)) {
						continue;
					}
					// if (key.equals("true")||key.equals("false"))
					if (key.equals("true")) // 暂时屏蔽无主键的table删除功能
					{
						_preparedConditions += column + "= ? and ";
						if ("integer".equals(type) || "numeric".equals(type)) {
							if (value.contains(".")) {
								argsArr.add(new Double(value));
							} else {
								argsArr.add(new Integer(value));
							}
							// _conditions += column + "=" + value + " and ";
						} else if ("varchar".equals(type)) {
							argsArr.add(value);
							// _conditions += column + "='" + value + "' and ";
						} else if ("date".equals(type)) {
							String format = "yyyy-MM-dd HH:mm:ss";
							if (value.trim().length() == 10) {
								format = "yyyy-MM-dd";
							} else if (value.trim().length() == 4) {
								format = "yyyy";
							} else if (value.trim().length() == 7) {
								format = "yyyy-MM";
							} else if (value.trim().length() == 13) {
								format = "yyyy-MM-dd HH";
							} else if (value.trim().length() == 16) {
								format = "yyyy-MM-dd HH:mm";
							}
							argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(value, format).getTime()));
							// 临时解决delete删除问题
							/*
							 * value = value.substring(0, 10);
							 * if(ConfigManager.getConstant
							 * (Constant.DataBase.DATABASE_TYPE
							 * ).equals(Constant.
							 * DataBase.DATABASE_TYPE_ORACLE)){ _conditions +=
							 * "to_char("+column + ",'yyyy-mm-dd')='" + value +
							 * "' and "; } else
							 * if(ConfigManager.getConstant(Constant
							 * .DataBase.DATABASE_TYPE
							 * ).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)){
							 * _conditions += column + "='" + value + "' and ";
							 * }
							 */

						} else {
							argsArr.add(value);
							// _conditions += column + "='" + value + "' and ";
						}

					}
				}
			}
			if (!"".equals(_preparedConditions)) {
				// _conditions = _conditions.substring(0, _conditions.length() -
				// 4);
				_preparedConditions = _preparedConditions.substring(0, _preparedConditions.length() - 4);
				// _sql += SQL_DELETE + " " + rowSetName + " where " +
				// _conditions;
				_sql += SQL_DELETE + " " + rowSetName + " where " + _preparedConditions;
			} else {
				_sql += SQL_DELETE + " " + rowSetName;
			}
			retObj[0] = _sql;
			retObj[1] = argsArr.toArray();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return retObj;
	}

	/*
	 * public static String getSelectSql(String rowSetName, Row row) { String
	 * column, key, value, _conditions = "", _sql = ""; try { RowSetDescriptor
	 * rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
	 * int columnCount = rowSetDescript.getColumnCount(); for (int i = 0; i <
	 * columnCount; i++) { ColumnDescriptor columnDescript =
	 * rowSetDescript.getColumn(i); column = columnDescript.getName();
	 * if(!"".equals(column)) { key = columnDescript.getKey(); value =
	 * row.getColumnValue(column); if (key.equals("true")) { _conditions +=
	 * column + "='" + value + "' and "; } } } if (!"".equals(_conditions)) {
	 * _conditions = _conditions.substring(0, _conditions.length() - 4); _sql +=
	 * SQL_SELECT + " " + rowSetName + " where " + _conditions; } else { _sql +=
	 * SQL_SELECT + " " + rowSetName; } } catch (Exception e) {
	 * logger.error(e.getMessage()); throw new RuntimeException(e); } return
	 * _sql; }
	 * 
	 * //暂时该方法没有被调用 public static String assembeSQL(String rowSetName, Row row)
	 * { String _sql = null;
	 * 
	 * String status = row.getStatus();
	 * 
	 * if (status.equals(RowConstant.MODIFY_STATUS)) { _sql =
	 * RowSetUtils.getUpdateSql(rowSetName, row); }
	 * 
	 * if (status.equals(RowConstant.NEW_STATUS)) { _sql =
	 * RowSetUtils.getInsertSql(rowSetName, row); }
	 * 
	 * if (status.equals(RowConstant.DELETE_STATUS)) { _sql =
	 * RowSetUtils.getDeleteSql(rowSetName, row); }
	 * 
	 * return _sql; }
	 */

	/**
	 * 获取非批量修改sql
	 * 
	 * @Title: getUpdateSqlNotBatch
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq
	 * @date:2015年12月1日 下午2:38:49
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: String
	 */
	public static String getUpdateSqlNotBatch(String rowSetName, Row row) {
		String column, key, value, _table, _conditions = "", _column_values = "", _sql = "";
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				column = columnDescript.getName();
				_table = columnDescript.getTable();
				if (!"".equals(_table) && !"".equals(column)) {
					key = columnDescript.getKey();
					String type = columnDescript.getType();
					value = row.getColumnValue(column);
					if (value == null || "null".equals(value)) {
						continue;
					}
					if (key.equals("true")) {
						if ("integer".equals(type) || "numeric".equals(type)) {
							_conditions += column + "=" + value + " and ";
						} else if ("varchar".equals(type)) {
							_conditions += column + "='" + value + "' and ";
						} else if ("date".equals(type)) {
							if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
								if ("".equals(value)) {
									_conditions += column + " is null and ";
								} else {
									_conditions += "to_char(" + column + ",'yyyy-mm-dd')='" + value + "' and ";
								}
							} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
								if ("".equals(value)) {
									_conditions += column + " is null and ";
								} else {
									_conditions += column + "='" + value + "' and ";
								}
							}
						} else {
							_conditions += column + "='" + value + "' and ";
						}
					} else {
						if ("integer".equals(type) || "numeric".equals(type)) {
							if (value != null) {
								if ("".equals(value)) {
									value = "0";
								}
								_column_values += column + "=" + value + ",";
							}
						} else if ("varchar".equals(type)) {
							_column_values += column + "='" + value + "',";
						} else if ("date".equals(type)) {
							if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {

								if ("".equals(value)) {
									value = null;
									_column_values += column + "=" + value + ",";
								} else {
									_column_values += column + "=to_date('" + value + "','yyyy-mm-dd hh24:mi:ss'),";
								}
							} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
								if ("".equals(value)) {
									value = null;
									_column_values += column + "=" + value + ",";
								} else {
									_column_values += column + "='" + value + "',";
								}
							}

						} else {
							_column_values += column + "='" + value + "',";
						}

					}
				}
			}
			_column_values = _column_values.substring(0, _column_values.length() - 1);
			if (!"".equals(_conditions)) {
				_conditions = _conditions.substring(0, _conditions.length() - 4);
				_sql += SQL_UPDATE + " " + rowSetName + " set " + _column_values + " where " + _conditions;
			} else {
				_sql += SQL_UPDATE + " " + rowSetName + " set " + _column_values;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return _sql;
	}

	/**
	 * 获取非批量插入sql
	 * 
	 * @Title: getInsertSqlNotBatch
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq
	 * @date:2015年12月1日 下午2:37:49
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[]
	 */
	public static Object[] getInsertSqlNotBatch(String rowSetName, Row row) {
		String column, value, _table, _columns = "", _values = "", _sql = "", _asks = "";
		Object[] retObj = new Object[2];
		;
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			ArrayList argsArr = new ArrayList();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				column = columnDescript.getName();
				String type = columnDescript.getType();
				_table = columnDescript.getTable();
				value = row.getColumnValue(column);
				if (!"".equals(_table) && !"".equals(value) && value != null && !"null".equals(value) && !"".equals(column)) {
					_columns += column + ",";
					_asks += "?,";
					if ("integer".equals(type) || "numeric".equals(type)) {
						if (value.contains(".")) {
							argsArr.add(new Double(value));
						} else {
							argsArr.add(new Integer(value));
						}
						_values += "" + value + ",";
					} else if ("varchar".equals(type)) {
						argsArr.add(value);
						_values += "'" + value + "',";
					} else if ("date".equals(type)) {
						String format = "yyyy-MM-dd hh:mm:ss";
						if (value.trim().length() == 10) {
							format = "yyyy-MM-dd";
						} else if (value.trim().length() == 4) {
							format = "yyyy";
						} else if (value.trim().length() == 7) {
							format = "yyyy-MM";
						} else if (value.trim().length() == 13) {
							format = "yyyy-MM-dd HH";
						} else if (value.trim().length() == 16) {
							format = "yyyy-MM-dd HH:mm";
						}
						argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(value, format).getTime()));
						if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
							if ("".equals(value)) {
								value = null;
								_values += value + ",";
							} else {
								_values += "to_date('" + value + "','yyyy-mm-dd hh24:mi:ss'),";
							}
						} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
							_values += "'" + value + "',";
						}

					} else if ("clob".equals(type)) {
						argsArr.add(new javax.sql.rowset.serial.SerialClob(value.toCharArray()));
					} else {
						argsArr.add(value);
						_values += "'" + value + "',";
					}

				}
			}
			_values = _values.substring(0, _values.length() - 1);
			_columns = _columns.substring(0, _columns.length() - 1);
			_asks = _asks.substring(0, _asks.length() - 1);
			// _sql += SQL_INSERT + " " + rowSetName + "(" + _columns + ") " +
			// "values(" + _values + ")";
			_sql += SQL_INSERT + " " + rowSetName + "(" + _columns + ") " + "values(" + _asks + ")";
			retObj[0] = _sql;
			retObj[1] = argsArr.toArray();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return retObj;
		// return _sql;
	}

	/**
	 * 获取非批量删除sql
	 * 
	 * @Title: getDeleteSqlNotBatch
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq
	 * @date:2015年12月1日 下午2:39:51
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: String
	 */
	public static String getDeleteSqlNotBatch(String rowSetName, Row row) {
		String column, key, value, _table, _conditions = "", _sql = "";
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				String type = columnDescript.getType();
				column = columnDescript.getName();
				_table = columnDescript.getTable();
				if (!"".equals(_table) && !"".equals(column)) {
					key = columnDescript.getKey();
					value = row.getColumnValue(column);
					if (value == null || "".equals(value) || "null".equals(value)) {
						continue;
					}
					// if (key.equals("true")||key.equals("false"))
					if (key.equals("true")) // 暂时屏蔽无主键的table删除功能
					{
						if ("integer".equals(type) || "numeric".equals(type)) {
							_conditions += column + "=" + value + " and ";
						} else if ("varchar".equals(type)) {
							_conditions += column + "='" + value + "' and ";
						} else if ("date".equals(type)) {
							// 临时解决delete删除问题
							value = value.substring(0, 10);
							if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
								_conditions += "to_char(" + column + ",'yyyy-mm-dd')='" + value + "' and ";
							} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
								_conditions += column + "='" + value + "' and ";
							}

						} else {
							_conditions += column + "='" + value + "' and ";
						}

					}
				}
			}
			if (!"".equals(_conditions)) {
				_conditions = _conditions.substring(0, _conditions.length() - 4);
				_sql += SQL_DELETE + " " + rowSetName + " where " + _conditions;
			} else {
				_sql += SQL_DELETE + " " + rowSetName;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return _sql;
	}
}
