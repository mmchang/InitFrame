package com.lnpc.common.utils;

import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lnpc.common.DataCenter;
import com.lnpc.common.config.Constant;
import com.lnpc.common.rowset.Row;
import com.lnpc.common.rowset.RowSet;

/**
 * JSON相关工具类，目前主要解析DataCenter对象
 * 
 * @author changjq
 * 
 */
public class JsonUtils {
	/**
	 * 根据客户端提交的请求封装DataCenter对象
	 * 
	 * @author changjq
	 * @param req
	 * @param resp
	 * @return DataCenter[]<br>
	 *         array[0] for request;array[1] for response
	 * @throws Exception
	 */
	public static DataCenter[] getDataCenter(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String debugInfo = req.getParameter(Constant.KEY_DATACENTER);
		String async = req.getParameter("async");
		DataCenter request = new DataCenter(req, resp);
		DataCenter response = new DataCenter(req, resp);
		response.setVariable("CN_YYYY_MM_DD_EEEE", DateUtils.date2String(new Date(), "yyyy年MM月dd日 EEEE"));
		response.setVariable("EN_YYYY_MM_DD_EEEE", DateUtils.date2String(new Date(), "yyyy-MM-dd EEEE"));
		DataCenter ret[] = { request, response };
		request.setAsync(async);
		if (!StringUtils.checkNullOrEmptyString(debugInfo)) {
			JSONObject jsonData = JSONObject.fromObject(debugInfo);
			Iterator<?> iterKeys = jsonData.keys();
			JSONArray dataArray = null;
			JSONArray rowArrays = null;
			JSONObject dataObject = null;
			while (iterKeys.hasNext()) {
				String key = (String) iterKeys.next();
				dataArray = jsonData.getJSONArray(key);
				for (int i = 0; i < dataArray.size(); i++) {
					dataObject = JSONObject.fromObject(dataArray.get(i));
					if ("variables".equalsIgnoreCase(key)) {
						request.setVariable(dataObject.getString("name"), dataObject.getString("value"));
					}
					if ("rowsets".equalsIgnoreCase(key)) {
						RowSet rs = new RowSet(dataObject.getString("name"));
						if(dataObject.getString("alias")!=null && !"".equals(dataObject.getString("name"))){
							rs.setAlias(dataObject.getString("alias"));
						}
						rs.setCondition(dataObject.getString("condition"));
						rs.setOrder(dataObject.getString("order"));
						rs.setCurrentPage(dataObject.getInt("currentPage"));
						rs.setSizePerPage(dataObject.getInt("sizePerPage"));
						rowArrays = JSONArray.fromObject(dataObject.getString("rows"));
						setRow(rowArrays, rs);
						request.add(rs);
					}
				}
			}

			// return {request,new DataCenter()};
		}
		// return new DataCenter();
		return ret;
	}

	/**
	 * 解析行
	 * 
	 * @author changjq
	 * @param rowArrays
	 * @param rs
	 */
	private static void setRow(JSONArray rowArrays, RowSet rs) {
		JSONObject rowObject = null;
		for (int j = 0; j < rowArrays.size(); j++) {
			rowObject = JSONObject.fromObject(rowArrays.get(j));
			setColumn(rowObject, rs, j);
		}
	}

	/**
	 * 解析列
	 * 
	 * @author changjq
	 * @param rowObject
	 * @param rs
	 * @param indexRow
	 */
	private static void setColumn(JSONObject rowObject, RowSet rs, int indexRow) {
		Row row = new Row();
		Iterator<?> itemKeys = rowObject.keys();
		while (itemKeys.hasNext()) {
			String itemKey = (String) itemKeys.next();
			if ("tag".equalsIgnoreCase(itemKey)) {
				JSONObject tagObject = JSONObject.fromObject(rowObject.getString(itemKey));
				row.setCheck(tagObject.getString("check"));
				row.setStatus(tagObject.getString("status"));
				int rowId = Integer.valueOf(tagObject.getString("rowId"));
				rs.addRowId(rowId);
			} else if ("ori".equalsIgnoreCase(itemKey)) {
				JSONObject oriObject = JSONObject.fromObject(rowObject.getString(itemKey));
				Iterator<?> oriKeys = oriObject.keys();
				while (oriKeys.hasNext()) {
					// String oriKey = (String) oriKeys.next();
				}
			} else {
				row.setColumnValue(itemKey, rowObject.getString(itemKey));
			}

		}
		rs.insertRow(indexRow, row);
	}
}
