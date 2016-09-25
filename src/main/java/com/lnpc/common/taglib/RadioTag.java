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
 * JSP自定义标签radio<br/>
 * 后续需要整合
 * 
 * @author changjq
 * 
 */
public class RadioTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 256720186005988782L;
	/**
	 * 缓存id
	 */
	private String cacheid = null;

	/**
	 * 过滤的内容
	 */
	private String filter = null;
	/**
	 * 过滤内容解析后的数组
	 */
	private String argsCodes[] = null;
	private String name = "";
	private String beforeText = "true";
	private String checked = null;
	private String disabled = null;
	private String css = null;

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
		String value, text, str;
		String disabledStr = ""; 
		String cssStr = ""; 
		JspWriter out = pageContext.getOut();
		Map<String, RowSet> cacheMap = CacheManager.getCachesMap();

		RowSet rs = cacheMap.get(cacheid);

		try {
			/** no catcheId */
			if (rs == null || rs.getRowCount() == 0) {
				rs = CacheManager.getCache(cacheid);
			}
			if (!"".equals(css) && css !=null) {
				cssStr = "class=\""+css+"\"";
			}
			if ("true".equals(disabled) || "disabled".equals(disabled)) {
				disabledStr = "disabled=\"disabled\"";
			}
			int len = rs.getRowCount();
			for (int i = 0; i < len; i++) {
				String checkedstr = "";
				value = rs.getString(i, "VALUE");
				text = rs.getString(i, "TEXT");
				if (value.equals(checked)) {
					checkedstr = "checked=\"checked\"";
				}
				if (filter != null) {
					if (isExist(text)) {
						if ("true".equals(beforeText)) {
							str = "<input "+cssStr+" "+disabledStr+" "+checkedstr+" type=\"radio\" name =\"" + name + "\" value =\"" + value + "\" cache =\"" + cacheid + "\">" + text;
						} else {
							str = text + "<input "+cssStr+" "+disabledStr+" "+checkedstr+" type=\"radio\" name =\"" + name + "\" value =\"" + value + "\" cache =\"" + cacheid + "\">";
						}
						out.println(str);
					}
				} else {
					if ("true".equals(beforeText)) {
						str = "<input "+cssStr+" "+disabledStr+" "+checkedstr+" type=\"radio\" name =\"" + name + "\" value =\"" + value + "\" cache =\"" + cacheid + "\">" + text;
					} else {
						str = text + "<input "+cssStr+" "+disabledStr+" "+checkedstr+" type=\"radio\" name =\"" + name + "\" value =\"" + value + "\" cache =\"" + cacheid + "\">";
					}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBeforeText() {
		return beforeText;
	}

	public void setBeforeText(String beforeText) {
		this.beforeText = beforeText;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}
}
