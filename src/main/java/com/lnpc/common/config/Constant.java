package com.lnpc.common.config;
/**
 * Class of Constant
 * @author changjq
 *
 */
public class Constant {
	public final static String KEY_DATACENTER = "d";
	public final static String OPERATION = "SUCCESS_OR_FAIL";
	public final static String RET_CODE = "RET_CODE";
	public final static String OPERATION_RESULT_SUCCESSFUL = "SUCCESS";
	public final static String OPERATION_RESULT_FAIL = "FAIL";
	public final static String CURRENT_DATE = "CURRENT_DATE";
	public final static String CURRENT_SYSTEM_TYPE="TEACHER";
	public final static String CONTEXT_PATH = "CONTEXT_PATH";
	public final static String ROWSET_PATH = "ROWSET_PATH";
	public final static String LOGIN_PATH = "LOGIN_PATH";
	public final static String MANAGE_LOGIN_PATH = "MANAGE_LOGIN_PATH";
	public final static String WEB_LOGIN_PATH = "WEB_LOGIN_PATH";
	public final static String CACHE_PATH = "CACHE_PATH";
	public final static String SERVER_LOCAL_TIME = "SERVER_LOCAL_TIME";
	public final static String LNPC_REDIRECT_URI = "LNPC_REDIRECT_URI";
	public final static String CACHE_TYPE_CONSTANT = "CACHE_TYPE_CONSTANT";
	public final static String CACHE_TEXT_CONSTANT = "CACHE_TEXT_CONSTANT";
	public final static String CACHE_VALUE_CONSTANT = "CACHE_VALUE_CONSTANT";
	public final static String CACHE_TABLE_CONSTANT = "CACHE_TABLE_CONSTANT";
	public final static String CACHE_ORDER_CONSTANT = "CACHE_ORDER_CONSTANT";

	public final static int SIZE_PER_PAGE = 20;
	public final class DataBase{
		public final static String DATABASE_TYPE_ORACLE = "ORACLE";
		public final static String DATABASE_TYPE_MYSQL = "MYSQL";
		public final static String DATABASE_TYPE_SQLSERVER = "SQLSERVER";
		public final static String DATABASE_TYPE = "DATABASE_TYPE";
		
		public final static String FILED_TYPE_INTEGER = "integer";
		public final static String FILED_TYPE_NUMERIC = "numeric";
		public final static String FILED_TYPE_VARCHAR = "varchar";
		public final static String FILED_TYPE_DATE = "date";
		public final static String FILED_TYPE_TINYINT = "tinyint";
		public final static String FILED_TYPE_CHAR = "char";
		public final static String FILED_TYPE_SMALLINT = "smallint";
		public final static String FILED_TYPE_FLOAT = "float";
		public final static String FILED_TYPE_REAL = "real";
		public final static String FILED_TYPE_DOUBLE = "double";
		public final static String FILED_TYPE_TIMESTAMP = "timestamp";
		public final static String FILED_TYPE_BLOB = "blob";
		public final static String FILED_TYPE_CLOB = "clob";
	}
	public final class Login{
		public final static String CURRENT_ADMIN = "CURRENT_ADMIN";
		public final static String CURRENT_USER = "CURRENT_USER";
		public final static String FAIL_MESSAGE = "FAIL_MESSAGE";
		public final static String USER_NOT_EXIST = "用户不存在";
		public final static String USER_OR_PASS_ERROR = "用户名或密码错误";
	}
}
