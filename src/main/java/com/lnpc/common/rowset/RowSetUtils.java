package com.lnpc.common.rowset;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
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

	protected static final String SQL_SELECT = "SELECT * FROM ";

	protected static final String SQL_UPDATE = "UPDATE ";

	protected static final String SQL_INSERT = "INSERT INTO ";

	protected static final String SQL_DELETE = "DELETE FROM ";

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
	 * <p>
	 * Description: 根据RowSet获取select语句（多表外关联查询）
	 * </p>
	 * 
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
	 * 
	 * <p>
	 * Description: 根据RowSet获取select语句
	 * </p>
	 * 
	 * @author changjq
	 * @param rowSetName
	 * @return
	 */
	public static String getSelectSql(String rowSetName) {

		/** 返回的sql */
		String _sql = "";

		/** 表名 */
		String _table = null;

		/** 字段名 */
		String _column = null;

		/** 存储字段名 */
		List<String> hsAttr = new ArrayList<String>();

		/** 存储数据库名 */
		Set<String> hsTable = new HashSet<String>();//
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);

			/** 字段总数 */
			int columnCount = rowSetDescript.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_table = columnDescript.getTable();
				_column = columnDescript.getName();
				if ((!StringUtils.checkNullOrEmptyString(_table)) && (!StringUtils.checkNullOrEmptyString(_column))) {
					hsAttr.add(_table + SQL_POINT + _column);
					hsTable.add(_table);
				}
			}
			_sql = "select " + hsAttr + " from " + hsTable;
			_sql = StringUtils.replace(_sql, sign);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return _sql;
	}

	/**
	 * 获取修改语句（批量）
	 * 
	 * @Title: getUpdateSql
	 * @Description: 根据RowSet的名称和行获取修改语句
	 * @author: cjq
	 * @date:2015年9月25日 上午9:38:16
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[0]:sql;Object[1]:prepared args
	 */
	protected static Object[] getUpdateSql(String rowSetName, Row row) {

		/** 字段名 */
		String _column = null;

		/** 主鍵标识 */
		String _key = null;

		/** 字段值 */
		String _value = null;

		/** 字段类型 */
		String _type = null;

		/** 表名 */
		String _table = null;

		/** 返回的sql */
		String _sql = "";

		/** set的字段：字段=？ */
		StringBuffer _column_values_asks = new StringBuffer(256);

		/** 更新的where条件：字段=？ */
		StringBuffer _conditions_asks = new StringBuffer(256);

		/** 返回值：[0] sql语句(带问号) ;[1] 匹配问号的具体参数(数组) */
		Object[] retObj = new Object[2];
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();

			/** 存储匹配问号的具体参数 */
			List<Object> argsArr = new LinkedList<Object>();

			/** 存储匹配问号的where条件参数 */
			List<Object> whereArr = new LinkedList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_column = columnDescript.getName();
				_table = columnDescript.getTable();
				if ((!StringUtils.checkNullOrEmptyString(_table)) && (!StringUtils.checkNullOrEmptyString(_column))) {
					_key = columnDescript.getKey();
					_type = columnDescript.getType();
					_value = row.getColumnValue(_column);
					if (_value == null || "null".equals(_value)) {
						continue;
					}

					/** 如果当前字段是主键, 则将此字段匹配到where条件中（暂不支持更新主键） */
					if (_key.equals("true")) {
						_conditions_asks.append(_column);
						_conditions_asks.append("= ? and ");

						/** 如果当前字段是数值类型 */
						if (Constant.DataBase.FILED_TYPE_INTEGER.equals(_type) || Constant.DataBase.FILED_TYPE_NUMERIC.equals(_type)) {

							/** 添加Integer类型参数 */
							whereArr.add(new Integer(_value));
						} else if (Constant.DataBase.FILED_TYPE_VARCHAR.equals(_type)) {
							/** 如果当前字段是字符串类型 */

							/** 添加String类型参数 */
							whereArr.add(_value);
						} else if (Constant.DataBase.FILED_TYPE_DATE.equals(_type)) {
							/** 如果当前字段是时间类型 */
							final String format = DateUtils.dateStr2FormatStrForOracle(_value);

							/** 添加Timestamp类型参数 */
							whereArr.add(new java.sql.Timestamp(DateUtils.string2Date(_value, format).getTime()));
						} else {
							/** 如果是其他类型 */

							/** 添加String类型参数 */
							whereArr.add(_value);
						}
					} else {
						/** 如果当前字段非主键, 则将此字段匹配到set filed=? 中 */
						_column_values_asks.append(_column);
						_column_values_asks.append("=?,");
						if (Constant.DataBase.FILED_TYPE_INTEGER.equals(_type) || Constant.DataBase.FILED_TYPE_NUMERIC.equals(_type)) {
							if ("".equals(_value)) {
								_value = "0";// 数字类型的字段不允许添加空字符串或NULL
							}

							/** 如果包含小数点 */
							if (_value.contains(".")) {

								/** 添加Double类型参数 */
								argsArr.add(new Double(_value));
							} else {

								/** 添加Integer类型参数 */
								argsArr.add(new Integer(_value));
							}

						} else if (Constant.DataBase.FILED_TYPE_VARCHAR.equals(_type)) {

							/** 添加String类型参数 */
							argsArr.add(_value);
						} else if (Constant.DataBase.FILED_TYPE_DATE.equals(_type)) {
							if ("".equals(_value)) {
								argsArr.add(null);
							} else {
								final String format = DateUtils.dateStr2FormatStrForOracle(_value);

								/** 添加Timestamp类型参数 */
								argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(_value, format).getTime()));
							}
						} else {

							/** 添加String类型参数 */
							argsArr.add(_value);
						}

					}
				}
			}

			/** 刪除最后一次拼接的字符：, */
			final String columnsForSet = _column_values_asks.substring(0, _column_values_asks.length() - 1);
			if (_conditions_asks.length() > 0) {

				/** 刪除最后一次拼接的字符：and */
				final String columnsForWhere = _conditions_asks.substring(0, _conditions_asks.length() - 4);
				_sql += SQL_UPDATE + " " + rowSetName + " set " + columnsForSet + " where " + columnsForWhere;
			} else {
				_sql += SQL_UPDATE + " " + rowSetName + " set " + columnsForSet;
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
	 * 获取insert语句（批量）
	 * 
	 * @Title: getInsertSql
	 * @Description: 根据RowSet的名称和行获取insert语句
	 * @author: cjq
	 * @date:2015年9月24日 下午4:10:14
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[0]:sql;Object[1]:prepared args
	 */
	protected static Object[] getInsertSql(String rowSetName, Row row) {

		/** 字段名 */
		String _column = null;

		/** 主鍵标识 */
		String _key = null;

		/** 字段值 */
		String _value = null;

		/** 字段类型 */
		String _type = null;

		/** 返回的sql */
		String _sql = "";

		/** 表名 */
		String _table = null;

		/** 需要插入的字段 */
		StringBuffer _columns = new StringBuffer(256);

		/** 需要插入的值，由于是批量操作，因此此处用?代替 */
		StringBuffer _asks = new StringBuffer(256);

		/** 返回值：[0] sql语句(带问号) ;[1] 匹配问号的具体参数(数组) */
		Object[] retObj = new Object[2];
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();

			/** 匹配?的具体参数（插入的值） */
			List<Object> argsArr = new LinkedList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_column = columnDescript.getName();
				_type = columnDescript.getType();
				_table = columnDescript.getTable();
				_value = row.getColumnValue(_column);
				if (!"".equals(_table) && !"".equals(_value) && _value != null && !"null".equals(_value) && !"".equals(_column)) {
					_columns.append(_column);
					_columns.append(",");
					_asks.append("?,");

					/** 如果当前字段是数值类型 */
					if (Constant.DataBase.FILED_TYPE_INTEGER.equals(_type) || Constant.DataBase.FILED_TYPE_NUMERIC.equals(_type)) {
						if (_value.contains(".")) {
							argsArr.add(new Double(_value));
						} else {
							argsArr.add(new Integer(_value));
						}
					} else if (Constant.DataBase.FILED_TYPE_VARCHAR.equals(_type)) {
						/** 如果当前字段是字符串类型 */
						argsArr.add(_value);
					} else if (Constant.DataBase.FILED_TYPE_DATE.equals(_type)) {
						/** 如果当前字段是时间类型 */
						final String format = DateUtils.dateStr2FormatStrForOracle(_value);
						argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(_value, format).getTime()));
					} else if (Constant.DataBase.FILED_TYPE_CLOB.equals(_type)) {
						/** 如果当前字段是clob类型 */
						argsArr.add(_value);
					} else {
						argsArr.add(_value);
					}
				}
			}
			final String insertColumns = _columns.substring(0, _columns.length() - 1);
			final String insertAsks = _asks.substring(0, _asks.length() - 1);
			_sql += SQL_INSERT + " " + rowSetName + "(" + insertColumns + ") " + "values(" + insertAsks + ")";
			retObj[0] = _sql;
			retObj[1] = argsArr.toArray();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return retObj;
	}

	/**
	 * 获取delete语句（批量）
	 * 
	 * @Title: getDeleteSql
	 * @Description: 根据RowSet的名称和行获取insert语句
	 * @author: cjq
	 * @date:2015年9月25日 上午8:46:42
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: Object[0]:sql;Object[1]:prepared args
	 */
	protected static Object[] getDeleteSql(String rowSetName, Row row) {

		/** 字段名 */
		String _column = null;

		/** 主鍵标识 */
		String _key = null;

		/** 字段值 */
		String _value = null;

		/** 字段类型 */
		String _type = null;

		/** 返回的sql */
		String _sql = "";

		/** 表名 */
		String _table = null;

		/** 删除语句的的where条件：字段=？ */
		StringBuffer _delCondition = new StringBuffer(128);

		/** 返回值：[0] sql语句(带问号) ;[1] 匹配问号的具体参数(数组) */
		Object[] retObj = new Object[2];
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();

			/** 匹配?的具体参数（where条件） */
			List<Object> argsArr = new LinkedList<Object>();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_type = columnDescript.getType();
				_column = columnDescript.getName();
				_table = columnDescript.getTable();
				if ((!StringUtils.checkNullOrEmptyString(_table)) && (!StringUtils.checkNullOrEmptyString(_column))) {
					_key = columnDescript.getKey();
					_value = row.getColumnValue(_column);
					if (_value == null || "".equals(_value) || "null".equals(_value)) {
						continue;
					}

					/** 只有key为true的情况下 才能作为删除语句的where条件 */
					if (_key.equals("true")) {
						_delCondition.append(_column);
						_delCondition.append("= ? and ");
						if (Constant.DataBase.FILED_TYPE_INTEGER.equals(_type) || Constant.DataBase.FILED_TYPE_NUMERIC.equals(_type)) {
							if (_value.contains(".")) {
								argsArr.add(new Double(_value));
							} else {
								argsArr.add(new Integer(_value));
							}
						} else if (Constant.DataBase.FILED_TYPE_VARCHAR.equals(_type)) {
							/** 如果当前字段是字符串类型 */
							argsArr.add(_value);
						} else if (Constant.DataBase.FILED_TYPE_DATE.equals(_type)) {
							/** 如果当前字段是时间类型 */
							final String format = DateUtils.dateStr2FormatStrForOracle(_value);

							/** 添加Timestamp类型参数 */
							argsArr.add(new java.sql.Timestamp(DateUtils.string2Date(_value, format).getTime()));
						} else {
							argsArr.add(_value);
						}

					}
				}
			}
			if (_delCondition.length() > 0) {
				/** 刪除最后一次拼接的字符：and */
				final String conditionForWhere = _delCondition.substring(0, _delCondition.length() - 4);
				_sql += SQL_DELETE + " " + rowSetName + " where " + conditionForWhere;
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

	/**
	 * 根据RowSet的名称和行获取SQL语句
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param rowSetName
	 * @param row
	 * @return
	 * @author changjq
	 * @date 2016年10月9日
	 */
	public static Object[] assembeSql(String rowSetName, Row row) {
		Object[] retObj = null;
		final String status = row.getStatus();
		if (status.equals(RowConstant.MODIFY_STATUS)) {
			retObj = getUpdateSql(rowSetName, row);
		} else if (status.equals(RowConstant.NEW_STATUS)) {
			retObj = getInsertSql(rowSetName, row);
		} else if (status.equals(RowConstant.DELETE_STATUS)) {
			retObj = getDeleteSql(rowSetName, row);
		}
		return retObj;
	}

	/**
	 * 获取update sql语句（非批量形式）
	 * 
	 * @Title: getUpdateSqlNotBatch
	 * @Description: 根据RowSet的名称和行获取update语句
	 * @author: cjq
	 * @date:2015年12月1日 下午2:38:49
	 * @param rowSetName
	 * @param row
	 * @return
	 * @return: String
	 */
	protected static String getUpdateSqlNotBatch(String rowSetName, Row row) {

		/** 字段名 */
		String _column = null;

		/** 主鍵标识 */
		String _key = null;

		/** 字段值 */
		String _value = null;

		/** 字段类型 */
		String _type = null;

		/** 表名 */
		String _table = null;

		/** 返回的sql */
		String _sql = "";

		/** where条件 */
		StringBuffer _conditions = new StringBuffer(128);

		/** set的字段和值 */
		StringBuffer _column_values = new StringBuffer(256);
		try {
			RowSetDescriptor rowSetDescript = RowSetDescriptorManager.getRowSetDescriptor(rowSetName);
			int columnCount = rowSetDescript.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				ColumnDescriptor columnDescript = rowSetDescript.getColumn(i);
				_column = columnDescript.getName();
				_table = columnDescript.getTable();
				_type = columnDescript.getType();
				if ((!StringUtils.checkNullOrEmptyString(_table)) && (!StringUtils.checkNullOrEmptyString(_column))) {
					_key = columnDescript.getKey();
					_value = row.getColumnValue(_column);
					if (_value == null || "null".equals(_value)) {
						continue;
					}

					/** 如果是主键，则作为where条件，暂不支持修改主键 */
					if (_key.equals("true")) {
						if (Constant.DataBase.FILED_TYPE_INTEGER.equals(_type) || Constant.DataBase.FILED_TYPE_NUMERIC.equals(_type)) {
							_conditions.append(_column);
							_conditions.append("=");
							_conditions.append(_value);
							_conditions.append(" and ");
						} else if (Constant.DataBase.FILED_TYPE_VARCHAR.equals(_type)) {
							/** 如果当前字段是字符串类型 */
							_conditions.append(_column);
							_conditions.append("='");
							_conditions.append(_value);
							_conditions.append("' and ");
						} else if (Constant.DataBase.FILED_TYPE_DATE.equals(_type)) {
							/** 如果当前字段是时间类型 */
							if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
								final String format = DateUtils.dateStr2FormatStrForOracle(_value);
								if ("".equals(_value)) {
									_conditions.append(_column);
									_conditions.append(" is null and ");
								} else {
									_conditions.append("to_char(");
									_conditions.append(_column);
									_conditions.append(",'");
									_conditions.append(format);
									_conditions.append("')='");
									_conditions.append(_value);
									_conditions.append("' and ");
								}
							} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
								if ("".equals(_value)) {
									_conditions.append(_column);
									_conditions.append(" is null and ");
								} else {
									_conditions.append(_column);
									_conditions.append("='");
									_conditions.append(_value);
									_conditions.append("' and ");
								}
							} else {
								_conditions.append(_column);
								_conditions.append("='");
								_conditions.append(_value);
								_conditions.append("' and ");
							}

						} else {
							_conditions.append(_column);
							_conditions.append("='");
							_conditions.append(_value);
							_conditions.append("' and ");
						}
					} else {
						if (Constant.DataBase.FILED_TYPE_INTEGER.equals(_type) || Constant.DataBase.FILED_TYPE_NUMERIC.equals(_type)) {
							if (_value != null) {
								if ("".equals(_value)) {
									_value = "0";
								}
								_column_values.append(_column);
								_column_values.append("=");
								_column_values.append(_value);
								_column_values.append(",");
							}
						} else if (Constant.DataBase.FILED_TYPE_VARCHAR.equals(_type)) {
							/** 如果当前字段是字符串类型 */
							_column_values.append(_column);
							_column_values.append("='");
							_column_values.append(_value);
							_column_values.append("',");
						} else if (Constant.DataBase.FILED_TYPE_DATE.equals(_type)) {
							/** 如果当前字段是时间类型 */
							if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
								// final String format =
								// DateUtils.dateStr2FormatStrForOracle(_value);
								if ("".equals(_value)) {
									_value = null;
									_column_values.append(_column);
									_column_values.append("= null ,");
								} else {
									_column_values.append(_column);
									_column_values.append("=to_date('");
									_column_values.append(_value);
									_column_values.append("','yyyy-mm-dd hh24:mi:ss'),");
								}
							} else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
								if ("".equals(_value)) {
									_value = null;
									_column_values.append(_column);
									_column_values.append("= null ,");
								} else {
									_column_values.append(_column);
									_column_values.append("='");
									_column_values.append(_value);
									_column_values.append("',");
								}
							}

						} else {
							_column_values.append(_column);
							_column_values.append("='");
							_column_values.append(_value);
							_column_values.append("',");
						}

					}
				}
			}
			final String columnValues = _column_values.substring(0, _column_values.length() - 1);
			if (_conditions.length() > 0) {
				final String whereConditions = _conditions.substring(0, _conditions.length() - 4);
				_sql += SQL_UPDATE + " " + rowSetName + " set " + columnValues + " where " + whereConditions;
			} else {
				_sql += SQL_UPDATE + " " + rowSetName + " set " + columnValues;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return _sql;
	}
}
