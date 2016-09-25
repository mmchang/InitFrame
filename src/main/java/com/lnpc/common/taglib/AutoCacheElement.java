package com.lnpc.common.taglib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lnpc.common.rowset.RowSet;
import com.lnpc.common.utils.HttpUtils;
import com.lnpc.manage.CacheManager;

/**
 * 临时解决JS加载缓存ELEMENT
 * @author cjq
 *
 */
public class AutoCacheElement extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3129473626374858824L;
	
	private final static String ELEM_CHECKBOX = "checkbox";
	private final static String ELEM_RADIO="radio";
	private final static String ELEM_SELECT="select";
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		StringBuffer buffer = new StringBuffer(512);
		String retStr = "";
		try {
			String name = request.getParameter("name");
			String nameStr ="";
			if(name!=null){
				nameStr = " name=\""+name+"\"";
			}
			String id = request.getParameter("id");
			String idStr="";
			if(id!=null){
				idStr = " id=\""+id+"\"";
			}
			String css = request.getParameter("css");
			String cssStr="";
			if(css!=null){
				cssStr = " class=\""+css+"\"";
			}
			String cacheId = request.getParameter("cacheId");
			String cacheIdStr = "";
			if(cacheId!=null){
				cacheIdStr = " cacheId=\""+cacheId+"\"";
			}
			String elemType = request.getParameter("elemType");
			String defaultValue = request.getParameter("defaultValue");
			String defaultText = request.getParameter("defaultText");
			String filter = request.getParameter("filter");
			String beforeText="";
			String disabledStr="";
			String [] argsCodes = null;
			if (filter != null)
			{
				argsCodes = filter.split("\\,");
			}
			Map<String, RowSet> cacheMap = CacheManager.getCachesMap();
			RowSet rs  = cacheMap.get(cacheId);
			if (rs == null || rs.getRowCount() == 0)
			{
				rs = CacheManager.getCache(cacheId);
			}
			int len = rs.getRowCount();
			String selectStr = null;
			if(ELEM_SELECT.equals(elemType)){
				selectStr = " selected = 'selected'";
				buffer.append("<select ");
				if(!"".equals(nameStr)){
					buffer.append(nameStr);
				}
				if(!"".equals(idStr)){
					buffer.append(idStr);
				}
				if(!"".equals(cssStr)){
					buffer.append(cssStr);
				}
				if(!"".equals(cacheIdStr)){
					buffer.append(cacheIdStr);
				}
				buffer.append(">");
				if(defaultText!=null && !"".equals(defaultText)){
					if(defaultValue!=null && !"".equals(defaultValue)){
						selectStr="";
					}
					buffer.append("<option "+ selectStr+" value=\"\">" + defaultText + "</option>");
				}
				String value,text;
				for (int i = 0; i < len; i++)
				{
					value = rs.getString(i, "VALUE");
					text = rs.getString(i, "TEXT");
					if(value.equals(defaultValue)){
						selectStr = " selected = 'selected'";
					}
					else{
						selectStr = "";
					}
					if (filter != null)
					{
						if (this.isExist(argsCodes, value))
						{
							buffer.append("<option "+ selectStr+" value=\"" + value + "\">" + text + "</option>");
						}
					}
					else
					{
						buffer.append("<option "+ selectStr+" value=\"" + value + "\">" + text + "</option>");
					}

				}
				buffer.append("</select>");
				retStr = buffer.toString();
			}else if(ELEM_RADIO.equals(elemType)){
				String value,text;
				selectStr = "";
				for (int i = 0; i < len; i++) {
					value = rs.getString(i, "VALUE");
					text = rs.getString(i, "TEXT");
					if (value.equals(defaultValue)) {
						selectStr = " checked=\"checked\"";
					}
					buffer.append("<input type=\"radio\" ");
					buffer.append(selectStr);
					buffer.append(disabledStr);
					if(!"".equals(nameStr)){
						buffer.append(nameStr);
					}
					if(!"".equals(idStr)){
						buffer.append(idStr);
					}
					if(!"".equals(cssStr)){
						buffer.append(cssStr);
					}
					if(!"".equals(cacheIdStr)){
						buffer.append(cacheIdStr);
					}
					
					buffer.append("/>");
					if (filter != null) {
						if (isExist(argsCodes,text)) {
							if ("true".equals(beforeText)) {
								buffer.append(text);
								retStr = buffer.toString();
							} else {
								retStr = text + buffer.toString();
							}
						}
					} else {
						if ("true".equals(beforeText)) {
							buffer.append(text);
							retStr = buffer.toString();
						} else {
							retStr = text + buffer.toString();
						}
					}

				}
			}else if(ELEM_CHECKBOX.equals(elemType)){
				String value,text;
				selectStr = "";
				for (int i = 0; i < len; i++) {
					value = rs.getString(i, "VALUE");
					text = rs.getString(i, "TEXT");
					if (value.equals(defaultValue)) {
						selectStr = " checked=\"checked\"";
					}
					buffer.append("<input type=\"checkbox\" ");
					buffer.append(selectStr);
					buffer.append(disabledStr);
					if(!"".equals(nameStr)){
						buffer.append(nameStr);
					}
					if(!"".equals(idStr)){
						buffer.append(idStr);
					}
					if(!"".equals(cssStr)){
						buffer.append(cssStr);
					}
					if(!"".equals(cacheIdStr)){
						buffer.append(cacheIdStr);
					}
					
					buffer.append("/>");
					if (filter != null) {
						if (isExist(argsCodes,text)) {
							if ("true".equals(beforeText)) {
								buffer.append(text);
								retStr = buffer.toString();
							} else {
								retStr = text + buffer.toString();
							}
						}
					} else {
						if ("true".equals(beforeText)) {
							buffer.append(text);
							retStr = buffer.toString();
						} else {
							retStr = text + buffer.toString();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			HttpUtils.writeResponse(response, retStr);
		}
	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		this.doPost(request, response);
	}
	private boolean isExist(String[] argsCodes,String code)
	{
		for (int i = 0; i < argsCodes.length; i++)
		{
			if (argsCodes[i].equals(code))
			{
				return true;
			}
		}
		return false;
	}
}
