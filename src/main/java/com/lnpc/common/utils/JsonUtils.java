package com.lnpc.common.utils;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
			
			JSONObject jsonData = JSON.parseObject(debugInfo);
			Set<?> keySets = jsonData.keySet();
			Iterator<?> iterKeys = keySets.iterator();
			JSONArray dataArray = null;
			JSONArray rowArrays = null;
			JSONObject dataObject = null;
			while (iterKeys.hasNext()) {
				String key = (String) iterKeys.next();
				dataArray = jsonData.getJSONArray(key);
				
				for (int i = 0; i < dataArray.size(); i++) {
					dataObject = dataArray.getJSONObject(i);
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
						rs.setCurrentPage(dataObject.getIntValue("currentPage"));
						rs.setSizePerPage(dataObject.getIntValue("sizePerPage"));
						rowArrays = dataObject.getJSONArray("rows");
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
			rowObject = rowArrays.getJSONObject(j);
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
		Set<?> keySets = rowObject.keySet();
		Iterator<?> itemKeys = keySets.iterator();
		while (itemKeys.hasNext()) {
			String itemKey = (String) itemKeys.next();
			if ("tag".equalsIgnoreCase(itemKey)) {
				
				JSONObject tagObject = rowObject.getJSONObject(itemKey);
				row.setCheck(tagObject.getString("check"));
				row.setStatus(tagObject.getString("status"));
				int rowId = Integer.valueOf(tagObject.getString("rowId"));
				rs.addRowId(rowId);
			} else if ("ori".equalsIgnoreCase(itemKey)) {
				//JSONObject oriObject = rowObject.getJSONObject(itemKey);
				//Set<?> oriKeySets = oriObject.keySet();
				//Iterator<?> oriKeys = oriKeySets.iterator();
				//while (oriKeys.hasNext()) {
				//}
			} else {
				row.setColumnValue(itemKey, rowObject.getString(itemKey));
			}

		}
		rs.insertRow(indexRow, row);
	}
}
