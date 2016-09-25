/**
 * 
 */
package com.lnpc.common.taglib;

import java.util.Map;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.lnpc.common.rowset.RowSet;
import com.lnpc.manage.CacheManager;

/**
 * JSP自定义标签li
 * 
 * @author changjq
 * 
 */
public class LiTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4503974893647038162L;
	/**
	 * 缓存id
	 */
	private String cacheid = null;
	
	/**
	 * 是否添加a标签
	 */
	private String href = null;

	/**
	 * 过滤的内容
	 */
	private String filter = null;
	/**
	 * 过滤内容解析后的数组
	 */
	private String argsCodes[] = null;

	public int doStartTag() throws JspTagException {
		if (filter != null) {
			argsCodes = filter.split("\\,");
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 是否过滤
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param code
	 * @return true 不过滤;false 过滤
	 */
	public boolean isExist(String code) {
		for (int i = 0; i < argsCodes.length; i++) {
			if (argsCodes[i].equals(code)) {
				return true;
			}
		}
		return false;
	}

	public int doEndTag() throws JspTagException {
		String value, text;
		JspWriter out = pageContext.getOut();
		Map<String, RowSet> cacheMap = CacheManager.getCachesMap();

		RowSet rs = cacheMap.get(cacheid);
		try {
			/** no catcheId */
			if (rs == null || rs.getRowCount() == 0) {
				rs = CacheManager.getCache(cacheid);
			}

			int len = rs.getRowCount();
			for (int i = 0; i < len; i++) {
				value = rs.getString(i, "VALUE");
				text = rs.getString(i, "TEXT");
				if (filter != null) {
					if (isExist(text)) {
						if("true".equals(this.href)){
							text = "<a href=\"#\">"+text+"</a>";
						}
						final String str = "<li val =\"" + value + "\" cache =\"" + cacheid + "\">" + text + "</li>";
						out.println(str);
					}
				} else {
					if("true".equals(this.href)){
						text = "<a href=\"#\">"+text+"</a>";
					}
					final String str = "<li val =\"" + value + "\" cache =\"" + cacheid + "\">" + text + "</li>";
					out.println(str);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return SKIP_BODY;
	}

	public void release() {
		super.release();
	}

	public String getCacheid() {
		return cacheid;
	}

	public void setCacheid(String cacheid) {
		this.cacheid = cacheid;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}
