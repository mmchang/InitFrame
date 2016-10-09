package com.lnpc.common;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lnpc.common.config.Constant;
import com.lnpc.common.download.FileDownload;
import com.lnpc.common.rowset.Row;
import com.lnpc.common.rowset.RowSet;
import com.lnpc.common.upload.FileUploadForStruts2;
import com.lnpc.common.utils.DateUtils;
import com.lnpc.common.utils.FileUtils;

/**
 * 客户端与服务器之间进行通信的传输对象
 * 
 * @author changjq
 * 
 */
public class DataCenter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 存储RowSet对象（RowSet.alias:RowSet）
	 */
	private Map<String, RowSet> rowSets = new HashMap<String, RowSet>();
	/**
	 * 存储变量（key:value形式）
	 */
	private Map<String, Object> variables = new HashMap<String, Object>();

	/**
	 * 预留字段
	 */
	private String check = "false";
	
	/**
	 * 预留字段
	 */
	private String state = "false";
	
	/**
	 * 客户端提交方式标识（"true" 异步;"false" 同步）
	 */
	private String async = "false";
	
	/**
	 * HttpServletResponse 对象（暂未开放）
	 */
	private HttpServletResponse response = null;
	
	/**
	 * HttpServletRequest 对象（暂未开放）
	 */
	private HttpServletRequest request = null;
	
	/**
	 * 下载文件的文件名（客户端显示）
	 */
	private String saveFileName;
	
	/**
	 * 需要下载文件的全路径，全路径和文件流只能二选一
	 */
	private String downloadFilePath;
	
	/**
	 * 需要下载的文件流， 全路径和文件流只能二选一
	 */
	private InputStream fileInputStream;

	/**
	 * 默认构造函数
	 */
	public DataCenter() {
		this.initDataCenter();
	}

	/**
	 * 构造函数
	 * 
	 * @param request
	 * @param response
	 */
	public DataCenter(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.initDataCenter();
	}

	/**
	 * 
	 * <p>Description: 设置下载文件的文件名（客户端显示） </p>
	 * @author changjq
	 * @date 2015年6月5日
	 * @param saveFileName
	 */
	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	/**
	 * 
	 * <p>Description: 设置下载文件的全路径 </p>
	 * @author changjq
	 * @date 2015年6月5日
	 * @param downloadFilePath
	 */
	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}

	/**
	 * 
	 * <p>Description: 设置下载文件的文件流 </p>
	 * @author changjq
	 * @date 2015年6月5日
	 * @param fileInputStream
	 */
	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	/**
	 * 
	 * <p>Description: 获取上下文路径 </p>
	 * @author changjq
	 * @date 2015年6月5日
	 * @param reg
	 * @return 上下文路径
	 */
	public String getRealPath(String reg) {
		return this.request.getSession().getServletContext().getRealPath(reg);
	}

	/**
	 * 
	 * <p>Description: 获取客户端提交方式 </p>
	 * @return "true" 异步;"false" 同步
	 */
	public String getAsync() {
		return this.async;
	}

	/**
	 * 
	 * <p>Description: 设置客户端提交方式（服务端不建议使用此方法） </p>
	 * @param async
	 */
	public void setAsync(String async) {
		this.async = async;
	}

	/**
	 * 
	 * <p>Description: 获取变量（key:value）的值 </p>
	 * @author changjq
	 * @param name
	 * @return 变量（key:value）值的String类型
	 */
	public String getVariable(String name) {
		return (String) variables.get(name);
	}

	/**
	 * <p>Description: 获取变量（key:value）的值 </p>
	 * @param name
	 * @return
	 * changjq
	 * 2016年9月24日
	 */
	public Object getObjectVariable(String name){
		return variables.get(name);
	}
	
	/**
	 * 
	 * <p>Description: 获取变量值得String类型 </p>
	 * @param name
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public String getStringVariable(String name){
		return (String) this.getObjectVariable(name);
	}
	
	/**
	 * 
	 * <p>Description: 获取变量值得int类型 </p>
	 * @param name
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public int getIntVariable(String name) {
		String stringValue = this.getStringVariable(name);
		return stringValue == null ? 0 : Integer.valueOf(stringValue);
	}
	
	/**
	 * 
	 * <p>Description: 获取变量值得float类型 </p>
	 * @param name
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public float getFloatVariable(String name){
		String stringValue = this.getStringVariable(name);
		return stringValue == null ? 0l : Float.valueOf(stringValue);
	}
	
	/**
	 * 
	 * <p>Description: 获取变量值得double类型 </p>
	 * @param name
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public double getDoubleVariable(String name){
		String stringValue = this.getStringVariable(name);
		return stringValue == null ? 0d : Double.valueOf(stringValue);
	}
	
	/**
	 * 
	 * <p>Description: 获取客户端提交的参数，等同于request.getParameter(name); </p>
	 * @author changjq
	 * @param name
	 * @return (String) request.getParameter(name)
	 */
	public String getParameter(String name) {
		return request.getParameter(name);
	}

	/**
	 * 
	 * <p>Description: 获取客户端提交参数名的数组 </p>
	 * @author changjq
	 * @return 客户端提交参数名的数组
	 */
	public Object[] getParameterNames() {
		List<Object> list = new LinkedList<Object>();
		Enumeration<?> enums = request.getParameterNames();
		while (enums.hasMoreElements())
			list.add(enums.nextElement());
		return list.toArray();
	}

	/**
	 * 
	 * <p>Description: 获取客户端提交的参数，等同于request.getParameterValues(name); </p>
	 * @author changjq
	 * @param name
	 * @return request.getParameterValues(name)
	 */
	public String[] getParameters(String name) {
		return request.getParameterValues(name);
	}

	/**
	 * 
	 * <p>Description: 获取request属性值 </p>
	 * @author changjq
	 * @date 2015年6月5日
	 * @param name
	 * @return object request.getAttribute(name);
	 */
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

	/**
	 * 
	 * <p>Description: 设置变量（key:value） </p>
	 * @author changjq
	 * @param name
	 * @param value
	 */
	public void setVariable(String name, Object value) {
		variables.put(name, value);
	}

	/**
	 * <p>Description: 设置request属性，等同于request.setAttribute(name, value) </p>
	 * <p>推荐使用setAttribute </p>
	 * @deprecated
	 * @author changjq
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, String value) {
		Object object = request.getAttribute(name);
		if (object != null) {
			request.setAttribute(name, value);
		}
	}

	/**
	 * 
	 * <p>Description: 设置request属性，等同于request.setAttribute(name, value) </p>
	 * @author changjq
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, Object value) {
		this.request.setAttribute(name, value);
	}

	/**
	 * 
	 * <p>Description: 设置返回信息（客户端接收） key:SUCCESS_OR_FAIL value:value </p>
	 * @param value
	 */
	public void setReturnMessage(String value) {
		this.setVariable(Constant.OPERATION, value);
	}

	/**
	 * 
	 * <p>Description: 设置返回信息（客户端接收） </p>
	 * @author changjq
	 * @param value
	 *            true "SUCCESS";false "FAIL"
	 */
	public void setReturnMessage(boolean value) {
		if (value) {
			this.setVariable(Constant.RET_CODE, "1");
			this.setVariable(Constant.OPERATION, Constant.OPERATION_RESULT_SUCCESSFUL);
		} else {
			this.setVariable(Constant.RET_CODE, "0");
			this.setVariable(Constant.OPERATION, Constant.OPERATION_RESULT_FAIL);
		}
	}

	/**
	 * 
	 * <p>Description: 获取session属性值 </p>
	 * @author changjq
	 * @param name
	 * @return session属性值
	 */
	public Object getSessionAttr(String name) {
		return this.request.getSession().getAttribute(name);
	}

	/**
	 * 
	 * <p>Description: 设置session属性值 </p>
	 * @author changjq
	 * @param name
	 * @param value
	 */
	public void setSessionAttr(String name, Object value) {
		this.request.getSession().setAttribute(name, value);
	}

	/**
	 * 
	 * <p>Description: 删除session属性 </p>
	 * @author changjq
	 * @param name
	 */
	public void removeSessionAttr(String name) {
		this.request.getSession().removeAttribute(name);
	}

	/**
	 * 
	 * <p>Description:（预留，暂未使用） </p>
	 * @author changjq
	 * @return 
	 */
	public String getCheck() {
		return check;
	}

	/**
	 * 
	 * <p>Description:（预留，暂未使用） </p>
	 * @author changjq
	 * @param check
	 */
	public void setCheck(String check) {
		this.check = check;
	}

	/**
	 * 
	 * <p>Description: （预留，暂未使用）</p>
	 * @author changjq
	 * @return
	 */
	public String getState() {
		return state;
	}

	/**
	 * 
	 * <p>Description:（预留，暂未使用） </p>
	 * @author changjq
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 
	 * <p>Description: 构建 DataCenter对象为JSON字符串形式 </p>
	 * @return DataCenter对象的JSON字符串形式
	 */
	public String toJSON() {
		JSONObject map = new JSONObject();
		List<JSONObject> lst = new ArrayList<JSONObject>();
		Object variablesObject[] = variables.keySet().toArray();
		for (int i = 0; i < variablesObject.length; i++) {
			JSONObject json = new JSONObject();
			String name = (String) variablesObject[i];
			Object value = variables.get(name);
			json.put("name", name);
			json.put("value", value);
			lst.add(json);
		}
		map.put("variables", lst);
		List<JSONObject> listRowSet = new ArrayList<JSONObject>();
		Object rowset[] = rowSets.keySet().toArray();
		for (int k = 0; k < rowset.length; k++) {
			RowSet rs = (RowSet) rowSets.get(rowset[k]);
			listRowSet.add(rs.toJSON());
		}
		map.put("rowsets", listRowSet);
		return JSON.toJSONString(map);
	}

	/**
	 * 
	 * <p>Description: 获取一个RowSet对象，如果不存在则返回null </p>
	 * @author changjq
	 * @param rowSetName
	 * @return RowSet对象
	 */
	public RowSet getRowSet(String rowSetName) {
		RowSet retRs = null;
		Set<String> set = rowSets.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (key.equalsIgnoreCase(rowSetName)) {
				retRs = rowSets.get(key);
				break;
			}
		}
		return retRs;
	}

	/**
	 * 
	 * <p>Description: 添加一个RowSet对象 </p>
	 * @author changjq
	 * @param rs
	 */
	public void add(RowSet rs) {
		rowSets.put(rs.getAlias(), rs);
	}

	/**
	 * 
	 * <p>Description: 删除一个RowSet对象 </p>
	 * @author changjq
	 * @param rowSetName
	 */
	public void remove(String rowSetName) {
		rowSets.remove(rowSetName);
	}

	/**
	 * 
	 * <p>Description: 设置struts2上传文件</p>
	 * @author changjq
	 * @param files
	 * @param fileNames
	 */
	public void setStruts2UploadFiles(File[] files, String[] fileNames) {
		uploadProcessForStruts2(files, fileNames);
	}

	/**
	 * 
	 * <p>Description: 设置struts2上传文件 </p>
	 * @author changjq
	 * @param files
	 * @param fileNames
	 */
	public void setStruts2UploadFiles(List<File> files, String[] fileNames) {
		File[] uploadFiles = (File[]) files.toArray();
		setStruts2UploadFiles(uploadFiles, fileNames);
	}

	/**
	 * 
	 * <p>Description: 拷贝上传的文件到指定目录，并设置相应数据库字段的值 </p>
	 * @author changjq
	 * @param uploadFiles
	 * @param fileNames
	 */
	private void uploadProcessForStruts2(File[] uploadFiles, String[] fileNames) {
		if (uploadFiles != null) {
			for (int i = 0; i < uploadFiles.length; i++) {
				FileUploadForStruts2 upload = new FileUploadForStruts2(this.request);
				String filePath = upload.upload(uploadFiles[i], fileNames[i]);
				Set<String> set = rowSets.keySet();
				Iterator<String> it = set.iterator();
				while (it.hasNext()) {
					boolean find = false;
					String rowsetKey = it.next();
					RowSet rs = rowSets.get(rowsetKey);
					int rowSize = rs.getRowCount();
					for (int j = 0; j < rowSize; j++) {
						Row row = rs.getRowByIndex(j);
						Object[] columnKeyArr = row.toArray();
						for (int k = 0; k < columnKeyArr.length; k++) {
							String columnKey = String.valueOf(columnKeyArr[k]);
							String columnValue = row.getColumnValue(columnKey);
							String reg = columnKey + ".LNPCATTACHMENT." + fileNames[i];
							if (columnValue.equals(reg)) {
								row.setColumnValue(columnKey, FileUtils.getFileNameByPath(filePath));
								find = true;
								break;
							}
						}
						if (find) {
							break;
						}
					}
					if (find) {
						break;
					}
				}
			}
		}
	}

	/**
	 * 
	 * <p>Description: 返回客户端前调用此方法，目前主要是下载操作 </p>
	 * @author changjq
	 * @date 2015年6月5日
	 * @return 0 已下载;1未下载
	 */
	public int finish() {
		int ret = 0;
		if (this.downloadFilePath != null) {
			FileDownload download = new FileDownload(this.request, this.response);
			download.setDownloadFilePath(this.downloadFilePath);
			download.setSaveFileName(this.saveFileName);
			download.downloadByPath();
			ret = 1;
		} else if (this.fileInputStream != null) {
			FileDownload download = new FileDownload(this.request, this.response);
			download.setSaveFileName(this.saveFileName);
			download.setFileInputStream(this.fileInputStream);
			download.downloadByStream();
			ret = 1;
		}
		this.setAttribute(Constant.KEY_DATACENTER, this.toJSON());
		return ret;
	}

	/**
	 * 
	 * <p>Description: 初始化必要参数 </p>
	 * @author changjq
	 * @date 2015年6月5日
	 */
	private void initDataCenter() {
		
		/**
		 * 设置服务器当前时间
		 */
		this.variables.put(Constant.SERVER_LOCAL_TIME, DateUtils.date2String(new Date(), "yyyy-MM-dd"));
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
}
