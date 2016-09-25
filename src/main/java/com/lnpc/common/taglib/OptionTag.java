package com.lnpc.common.taglib;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.lnpc.common.rowset.RowSet;
import com.lnpc.manage.CacheManager;
/**
 * JSP自定义标签option
 * @author changjq
 *
 */
public class OptionTag extends TagSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
		 * 缓存id
		 */
		private String cacheid = null;

		/**
		 * 是否允许空值
		 */
		private String allowNull = null;

		/**
		 * 如果允许空值，默认值
		 */
		private String defaultNullText = null;
		/**
		 * 默认值
		 */
		private String defaultValue = null;
		/**
		 * 过滤的内容
		 */
		private String filter = null;
		/**
		 * 所有、全部字样
		 */
		private String allText = null;
		/**
		 * 过滤内容解析后的数组
		 */
		private String argsCodes[] = null;

		public int doStartTag() throws JspTagException
		{
			if (filter != null)
			{
				argsCodes = filter.split("\\,");
			}
			return EVAL_BODY_INCLUDE;
		}
		/**
		 * 是否过滤
		 * @author changjq
		 * @date 2015年6月5日
		 * @param code
		 * @return true 不过滤;false 过滤
		 */
		public boolean isExist(String code)
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

		public int doEndTag() throws JspTagException
		{
			String selectStr = "selected = 'selected'";
			String value, text;

			JspWriter out = pageContext.getOut();

			try
			{
				/**  */
				Map<String, RowSet> cacheMap = CacheManager.getCachesMap();

				RowSet rs  = cacheMap.get(cacheid);

				/** no catcheId */
				if (rs == null || rs.getRowCount() == 0)
				{
					/** new rowset */
					rs = CacheManager.getCache(cacheid);
				}

				/**  */
				if ("true".equalsIgnoreCase(allowNull))
				{
					if(!"".equals(this.defaultValue)){
						selectStr="";
					}
					if (defaultNullText != null)
					{
						out.println("<option "+ selectStr+" value=\"\">" + defaultNullText + "</option>");
					}
					else
					{
						out.println("<option "+ selectStr+" value=\"\"></option>");
					}
				}
				if(allText!=null){
					out.println("<option value=\""+allText+"\">" + allText + "</option>");
				}

				/**  */
				int len = rs.getRowCount();
				for (int i = 0; i < len; i++)
				{
					value = rs.getString(i, "VALUE");
					text = rs.getString(i, "TEXT");
					if(value.equals(this.defaultValue)){
						selectStr = "selected = 'selected'";
					}
					else{
						selectStr = "";
					}
					if (filter != null)
					{
						if (isExist(value))
						{
							out.println("<option "+ selectStr+" value=\"" + value + "\">" + text + "</option>");
						}
					}
					else
					{
						out.println("<option "+ selectStr+" value=\"" + value + "\">" + text + "</option>");
					}

				}
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			return SKIP_BODY;
		}

		public void release()
		{
			super.release();
		}

		public String getAllowNull()
		{
			return allowNull;
		}

		public void setAllowNull(String allowNull)
		{
			this.allowNull = allowNull;
		}

		public String getCacheid()
		{
			return cacheid;
		}

		public void setCacheid(String cacheid)
		{
			this.cacheid = cacheid;
		}

		public String getDefaultNullText()
		{
			return defaultNullText;
		}

		public void setDefaultNullText(String defaultNullText)
		{
			this.defaultNullText = defaultNullText;
		}

		public String getFilter()
		{
			return filter;
		}

		public void setFilter(String filter)
		{
			this.filter = filter;
		}

		public String getAllText()
		{
			return allText;
		}

		public void setAllText(String allText)
		{
			this.allText = allText;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		
}