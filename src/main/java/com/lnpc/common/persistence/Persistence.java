package com.lnpc.common.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import oracle.sql.ROWID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import com.lnpc.common.config.Constant;
import com.lnpc.common.rowset.Row;
import com.lnpc.common.rowset.RowConstant;
import com.lnpc.common.rowset.RowSet;
import com.lnpc.common.rowset.RowSetUtils;
import com.lnpc.common.utils.DateUtils;
import com.lnpc.common.utils.StringUtils;
import com.lnpc.manage.ConfigManager;

/**
 * 数据库访问类 Class access to database
 * 
 * @author changjq
 * 
 */
public class Persistence extends JdbcDaoSupport {
	private static Logger logger = LoggerFactory.getLogger(Persistence.class);
	private String _condition = "", _order = "", _sql = "", _totalCountSql = "";
	/**
	 * 总数据条数
	 */
	private int totalCount = 0;
	private String value = null;
	/**
	 * 查询返回的对象
	 */
	private RowSet rowset = null;
	/**
	 * 每页显示数据条数 data count per page
	 */
	private int sizePerPage = 0;
	/**
	 * 当前页 current page number<br>
	 * default to -1
	 */
	private int currentPage = -1;
	/**
	 * 是否是以自动方式构建sql语句
	 */
	private boolean auto = true;

	/**
	 * 手动构建sql语句 constructor<br>
	 * 
	 * @param dataSource
	 */
	public Persistence(DataSource dataSource) {
		this.setDataSource(dataSource);
		this.auto = false;
		this.rowset = new RowSet();
		this.sizePerPage = Constant.SIZE_PER_PAGE;
	}

	/**
	 * 自动构建sql语句 constructor<br>
	 * using for building sql automatically
	 * 
	 * @param rowSetName
	 * @param dataSource
	 */
	public Persistence(String rowSetName, DataSource dataSource) {
		this.setDataSource(dataSource);
		this.rowset = new RowSet(rowSetName);
		this._order = this.rowset.getOrder();
		this._condition = this.rowset.getCondition();
		this._sql = RowSetUtils.getSelectSql(this.rowset.getName());
		this.auto = true;
		this.sizePerPage = Constant.SIZE_PER_PAGE;
	}
	public Persistence(String rowSetName, DataSource dataSource,boolean join) {
		this.setDataSource(dataSource);
		this.rowset = new RowSet(rowSetName);
		this._order = this.rowset.getOrder();
		this._condition = this.rowset.getCondition();
		if(join){
			this._sql = RowSetUtils.getSelectJoinSql(this.rowset.getName());
		}else{
			this._sql = RowSetUtils.getSelectSql(this.rowset.getName());
		}
		this.auto = true;
		this.sizePerPage = Constant.SIZE_PER_PAGE;
	}
	/**
	 * 用于需要返回自增主键的插入操作
	 * @author changjq
	 * @date 2015年6月19日
	 * @param rs
	 * @return 自增主键数组
	 * @throws DataAccessException
	 * @throws CannotGetJdbcConnectionException
	 */
	public String[] insertRowSet(RowSet rs)throws DataAccessException, CannotGetJdbcConnectionException{
		String ret[] = null;
		try {
			if (rs == null || rs.getRowCount()==0) {
				throw new IllegalArgumentException("The rowset is null!");
			} else {
				ret = new String[rs.getRowCount()];
				String rowSetName = rs.getName();
				int length = rs.getRowCount();
				Object [] objArr = null;
				for (int k = 0; k < length; k++) {
					String status = rs.getRowByIndex(k).getStatus();
					String sql = null;
					Object [] args = null;
					objArr = RowSetUtils.assembeSql(rowSetName, rs.getRowByIndex(k));
					sql = (String) objArr[0];
					args = (Object[]) objArr[1];
					logger.info("The sql is:" + sql + ".");
					if (sql != null && !"".equals(sql)) {
						final String innerSql = sql;
						final Object[] innerArgs = args;
						KeyHolder keyHolder = new GeneratedKeyHolder();
						int result = this.getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								// TODO Auto-generated method stub
								PreparedStatement ps  = con.prepareStatement(innerSql,Statement.RETURN_GENERATED_KEYS);
								for(int argsIndex = 0;argsIndex<innerArgs.length;argsIndex++){
									Object obj = innerArgs[argsIndex];
									if(obj instanceof String){
										ps.setString(argsIndex+1, (String) obj);
									}else if(obj instanceof SerialClob){
										ps.setClob(argsIndex+1, (Clob) obj);
									}else if(obj instanceof Integer){
										ps.setInt(argsIndex+1, (Integer) obj);
									}else if(obj instanceof java.sql.Timestamp){
										ps.setTimestamp(argsIndex+1, (Timestamp) obj);
									}else if(obj instanceof Date){
										ps.setDate(argsIndex+1, (java.sql.Date) obj);
									}else if(obj instanceof Double){
										ps.setDouble(argsIndex+1, (Double) obj);
									}else{
										ps.setString(argsIndex+1, (String) obj);
									}
								}
								return ps;
							}
						}, keyHolder);
						ROWID rid = (ROWID) keyHolder.getKeys().get("ROWID");
						ret[k] = rid.stringValue();
					}

				}
				this.releaseConnection(getConnection());
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 非批量操作RowSet 适用于增、删、改<br/>
	 * @Title: updateRowSetNotBatch
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月1日 下午2:41:19 
	 * @param rs
	 * @return
	 * @throws DataAccessException
	 * @throws CannotGetJdbcConnectionException
	 * @return: int
	 */
	public int updateRowSetNotBatch(RowSet rs) throws DataAccessException, CannotGetJdbcConnectionException {
		int ret = 0;
		try {
			if (rs == null) {
				throw new IllegalArgumentException("The rowset is null!");
			} else {
				String rowSetName = rs.getName();
				int length = rs.getRowCount();
				Object [] retArr = null;
				for (int k = 0; k < length; k++) {
					String sql = null;
					Object [] args = null;
					retArr = RowSetUtils.assembeSql(rowSetName, rs.getRowByIndex(k));
					sql = (String) retArr[0];
					args =  (Object[]) retArr[1];
					logger.info("The sql is:" + sql + ".");
					if (!StringUtils.checkNullOrEmptyString(sql)) {
						int result = this.getJdbcTemplate().update(sql,args);
						ret = ret + result;
					}
				}
				this.releaseConnection(getConnection());
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	/**
	 * 批量操作RowSet 适用于增、删、改<br/>
	 * 注：调用此方法的前提是sql语句唯一
	 * @author changjq
	 * @param rs
	 * @return
	 */
	public int updateRowSet(RowSet rs) throws DataAccessException, CannotGetJdbcConnectionException {
		int ret = 0;
		try {
			if (rs == null) {
				throw new IllegalArgumentException("The rowset is null!");
			} else {
				String rowSetName = rs.getName();
				final int length = rs.getRowCount();
				String _sql = null;
				Object [] argsArr = new Object[length];
				for (int k = 0; k < length; k++) {
					Object [] retArr = null;
					String sql = null;
					Object [] args = null;
					retArr = RowSetUtils.assembeSql(rowSetName, rs.getRowByIndex(k));
					sql = (String) retArr[0];
					args =  (Object[]) retArr[1];
					argsArr[k] = args;
					if(k==0){//暂时每次都要构建sql，后续改善
						_sql = sql;
					}
				}
				final Object [] innerArgs = argsArr;
				logger.info("The sql is:" + _sql + ".");
				this.getJdbcTemplate().batchUpdate(_sql, new BatchPreparedStatementSetter(){
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						final Object [] arr = (Object[]) innerArgs[i];
						for(int argsIndex = 0;argsIndex<arr.length;argsIndex++){
							Object obj = arr[argsIndex];
							if(obj==null){
								ps.setDate(argsIndex+1, (java.sql.Date) null);
							}
							else if(obj instanceof String){
								ps.setString(argsIndex+1, (String) obj);
							}else if(obj instanceof SerialClob){
								ps.setClob(argsIndex+1, (Clob) obj);
							}else if(obj instanceof Integer){
								ps.setInt(argsIndex+1, (Integer) obj);
							}else if(obj instanceof java.sql.Timestamp){
								ps.setTimestamp(argsIndex+1, (Timestamp) obj);
							}else if(obj instanceof Date){
								ps.setDate(argsIndex+1, (java.sql.Date) obj);
							}else if(obj instanceof Double){
								ps.setDouble(argsIndex+1, (Double) obj);
							}else{
								ps.setString(argsIndex+1, (String) obj);
							}
						}
					}

					@Override
					public int getBatchSize() {
						// TODO Auto-generated method stub
						return length;
					}
					
				});
				this.releaseConnection(getConnection());
			}
			return ret;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 构建sql语句 包括 拼接分页语句 拼接查询条件 拼接order by字段
	 * 
	 * @author changjq
	 */
	private void initializeSQL() {
		if (_sql.trim().toLowerCase().startsWith("select")) {
			if (this.currentPage != -1) {
				int pageBegin = (currentPage - 1) * sizePerPage;
				int pageEnd = currentPage * sizePerPage;
				this._totalCountSql = _sql;
				if (!"".equals(this._condition)) {
					_totalCountSql += " where " + _condition;
				}
				
				_totalCountSql = "select count(*) from (" + _totalCountSql + ") TEMPTABLE";
				// oracle
				if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_ORACLE)) {
					StringBuffer _sql2 = new StringBuffer(512);
					_sql2.append("SELECT TEMP1.*,ROWNUM RN FROM (");
					_sql2.append(_sql);
					if (!"".equals(this._condition)) {
						final String finalCondition = " where " + _condition;
						_sql2.append(finalCondition);
					}
					
					if (!"".equals(this._order)) {
						final String finalOrder = " order by " + _order;
						_sql2.append(finalOrder);
					}
					_sql2.append(") TEMP1");
					
					String finalslq2Where = "";
					finalslq2Where += (" where ROWNUM <=" + pageEnd);
					_sql2.append(finalslq2Where);

					StringBuffer newSql = new StringBuffer();
					newSql.append(" SELECT TEMPTABLE.* FROM (");
					newSql.append(_sql2);
					newSql.append(" ) TEMPTABLE ");
					newSql.append(" WHERE TEMPTABLE.RN>" + pageBegin);
					_sql = newSql.toString();
				}
				// mysql
				else if (ConfigManager.getConstant(Constant.DataBase.DATABASE_TYPE).equals(Constant.DataBase.DATABASE_TYPE_MYSQL)) {
					if (!"".equals(this._condition)) {
						_sql += " WHERE " + _condition;
					}
					String mysqlPageCondition = " limit " + pageBegin + "," + sizePerPage;
					_sql += mysqlPageCondition;
				}
			} else {
				if (!"".equals(this._condition)) {
					_sql += " where " + _condition;
				}
				this._totalCountSql = _sql;
				_totalCountSql = "select count(*) from (" + _totalCountSql + ") TEMPTABLE";
				if (!"".equals(this._order)) {
					_sql += " order by " + _order;
				}
			}
		} else {
			if (!"".equals(this._condition)) {
				_sql += " where " + _condition;
			}
		}
	}

	/**
	 * 查询
	 * 
	 * @author changjq
	 * @return RowSet对象
	 * @throws Exception
	 */
	public RowSet query() throws DataAccessException, CannotGetJdbcConnectionException, InvalidResultSetAccessException {
		Object[] args = null;
		return this.query(args);
	}

	/**
	 * 查询
	 * 
	 * @author changjq
	 * @return List对象
	 */
	public List<Map<String, Object>> queryForList() throws DataAccessException, CannotGetJdbcConnectionException {
		Object[] args = null;
		return this.queryForList(args);
	}

	/**
	 * 查询
	 * 
	 * @author changjq
	 * @param args
	 * @return List对象
	 */
	public List<Map<String, Object>> queryForList(Object... args) throws DataAccessException, CannotGetJdbcConnectionException {
		this.initializeSQL();
		List<Map<String, Object>> retList = null;
		int size = 0;
		logger.info("The sql is:" + _sql + ".");
		if (args != null && args.length != 0) {
			retList = this.getJdbcTemplate().queryForList(_sql, args);
			size = (Integer) this.getJdbcTemplate().queryForObject(_totalCountSql, args, Integer.class);
		} else {
			retList = this.getJdbcTemplate().queryForList(_sql);
			size = (Integer) this.getJdbcTemplate().queryForObject(_totalCountSql, Integer.class);
		}
		this.totalCount = size;
		this.releaseConnection(getConnection());
		return retList;
	}

	/**
	 * 查询
	 * 
	 * @author changjq
	 * @param args
	 * @return RowSet对象
	 */
	public RowSet query(Object... args) throws DataAccessException, CannotGetJdbcConnectionException, InvalidResultSetAccessException {
		this.initializeSQL();
		SqlRowSet sqlRowSet = null;
		int size = 0;
		logger.info("The sql is:" + _sql + ".");
		if (args != null && args.length != 0) {
			sqlRowSet = this.getJdbcTemplate().queryForRowSet(_sql, args);
			size = (Integer) this.getJdbcTemplate().queryForObject(_totalCountSql, args, Integer.class);
		} else {
			sqlRowSet = this.getJdbcTemplate().queryForRowSet(_sql);
			size = (Integer) this.getJdbcTemplate().queryForObject(_totalCountSql, Integer.class);
		}
		SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
		while (sqlRowSet.next()) {
			Row row = new Row();
			rowset.addRow(row);
			for (int k = 1; k <= metaData.getColumnCount(); k++) {
				String t = StringUtils.getStringFieldType(metaData.getColumnType(k));
				value = sqlRowSet.getString(k) == null ? "" : sqlRowSet.getString(k);
				if(Constant.DataBase.FILED_TYPE_CLOB.equals(t)){//临时解决clob oracle 10g+ mabey
					Object obj = sqlRowSet.getObject(k);
					SerialClob clob = (SerialClob) obj;
					try {
						if (clob != null && clob.length() > 0) {
							Reader is = clob.getCharacterStream();
							StringBuffer sb = new StringBuffer(1024);
							BufferedReader br = new BufferedReader(is);
							String s = null;
							while ((s = br.readLine()) != null) {
								sb.append(s);
							}
							value = sb.toString();

						} else {
							value = "";
						}
					} catch (SerialException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (!"".equals(value) && ("date".equals(t) || "timestamp".equals(t))) {
					try {
						Date tempDate = DateUtils.string2Date(value, "yyyy-MM-dd HH:mm:ss");
						value = DateUtils.date2String(tempDate, "yyyy-MM-dd HH:mm:ss");
					} catch (Exception e) {
						e.printStackTrace();
						value = "";
					}
				}
				row.setColumnValue(metaData.getColumnLabel(k), value);
			}
			row.setStatus(RowConstant.OLD_STATUS);
		}
		this.rowset.setTotalProperty(size);
		if (this.getCurrentPage() == -1) {
			this.rowset.setCurrentPage(1);
			this.rowset.setTotalPage(1);
			this.rowset.setSizePerPage(size);
		} else {
			this.rowset.setCurrentPage(getCurrentPage());
			this.rowset.setTotalPage((size + sizePerPage - 1) / sizePerPage);
			this.rowset.setSizePerPage(this.getSizePerPage());
		}
		this.releaseConnection(getConnection());
		return this.rowset;
	}

	/**
	 * 用于crud(create,retrieve,update,delete).<br>
	 * r:返回 RowSet对象<br>
	 * cud:返回 空的RowSet对象
	 * 
	 * @author changjq
	 * @param args
	 * @return RowSet
	 */
	public RowSet execute(Object... args) throws DataAccessException, CannotGetJdbcConnectionException, InvalidResultSetAccessException {
		SqlRowSet sqlRowSet = null;
		this.initializeSQL();
		logger.info("The sql is:" + _sql + ".");
		if (_sql.toLowerCase().startsWith("select")) {
			if (args != null && args.length != 0) {
				sqlRowSet = this.getJdbcTemplate().queryForRowSet(_sql, args);
			} else {
				sqlRowSet = this.getJdbcTemplate().queryForRowSet(_sql);
			}
			SqlRowSetMetaData metaData = sqlRowSet.getMetaData();

			rowset = new RowSet();
			while (sqlRowSet.next()) {
				Row row = new Row();
				rowset.addRow(row);
				for (int k = 1; k <= metaData.getColumnCount(); k++) {
					value = sqlRowSet.getString(k) == null ? "" : sqlRowSet.getString(k);
					row.setColumnValue(metaData.getColumnLabel(k), value);
				}
				row.setStatus(RowConstant.OLD_STATUS);
			}
			rowset.setName(metaData.getTableName(1));
		} else {
			if (args != null && args.length != 0) {
				this.getJdbcTemplate().update(_sql, args);
			} else {
				this.getJdbcTemplate().update(_sql);
			}
		}
		this.releaseConnection(getConnection());
		return this.rowset;
	}

	/**
	 * 获取sql语句的条件
	 * 
	 * @author changjq
	 * @return
	 */
	public String getCondition() {
		return this._condition;
	}

	/**
	 * 获取rowset.xml中的排序字段
	 * 
	 * @author changjq
	 * @return
	 */
	public String getOrder() {
		_order = rowset.getOrder();
		return _order;
	}

	/**
	 * 设置sql语句条件
	 * 
	 * @author changjq
	 * @param condition
	 * @return
	 */
	public Persistence setCondition(String condition) {
		this._condition = condition;
		return this;
	}

	/**
	 * 为sql添加"与"条件
	 * 
	 * @author changjq
	 * @param condition
	 * @return
	 */
	public Persistence setAndCondition(String condition) {
		return setCondition(RowSetUtils.SQL_AND, condition);
	}

	/**
	 * 为sql添加"或"条件
	 * 
	 * @author changjq
	 * @param condition
	 * @return this
	 */
	public Persistence setOrCondition(String condition) {
		return setCondition(RowSetUtils.SQL_OR, condition);
	}

	/**
	 * 为sql语句添加条件
	 * 
	 * @author changjq
	 * @param link
	 *            :[and,or]
	 * @param appendCondition
	 * @return this
	 */
	protected Persistence setCondition(String link, String appendCondition) {
		if (appendCondition != null && !appendCondition.equals("")) {
			if (this._condition == null || this._condition.equals("")) {
				setCondition(appendCondition);
			} else {
				setCondition("(" + this._condition + ") " + link + " (" + appendCondition + ")");
			}
		}
		return this;
	}

	/**
	 * 为sql语句设置排序字段（英文逗号分隔）
	 * 
	 * @author changjq
	 * @param order
	 */
	public void setOrder(String order) {
		this._order = order;
	}

	/**
	 * 设置要执行的sql语句
	 * 
	 * @author changjq
	 * @param sql
	 */
	public void setSql(String sql) {
		this._sql = sql;
	}
	
	public String getSql() {
		return this._sql;
	}

	/**
	 * 获取当前页数
	 * 
	 * @author changjq
	 * @return 当前页数
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * 设置当前页数，如不需要分页，无需设置
	 * 
	 * @author changjq
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * 获取每页显示数据数
	 * 
	 * @author changjq
	 * @return 每页显示数据数
	 */
	public int getSizePerPage() {
		return sizePerPage;
	}

	/**
	 * 设置每页显示数据数 default to 20
	 * 
	 * @author changjq
	 * @param sizePerPage
	 */
	public void setSizePerPage(int sizePerPage) {
		this.sizePerPage = sizePerPage;

	}

	/**
	 * 获取查询条件下所有数据条数 queryForList被调用后会被自动赋值 get all data count by sql condition.<br>
	 * value will be set after method queryForList is called.
	 * 
	 * @author changjq
	 * @return 查询条件下所有数据条数
	 */
	public int getTotalCount() {
		return totalCount;
	}

}
