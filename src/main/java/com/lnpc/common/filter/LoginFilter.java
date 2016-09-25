/**
 * 
 */
package com.lnpc.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lnpc.common.DataCenter;
import com.lnpc.common.config.Constant;

/**
 * 登陆过滤器（前后台通用）<br>
 * 使用此过滤器要求前（后）台的请求地址的第一层命名空间必须一致<br>
 * 如：后台一致使用/manage/...开头；前台一致使用/web/...开头（可自定义）<br>
 * 需要配置的init-param如下:<br>
 * ADMIN_REDIRECT_URL:后台过滤失败后的转发地址<br>
 * ADMIN_SESSION_KEY:后台SESSION存储的KEY<br>
 * ADMIN_START_WITH:后台统一的第一命名空间 如 manage<br>
 * ADMIN_NO_CHECK_URL: 后台不需要过滤的地址列表，多个以";"相隔<br>
 * USER_REDIRECT_URL:前台过滤失败后的转发地址<br>
 * USER_SESSION_KEY:前台SESSION存储的KEY<br>
 * USER_START_WITH:前台统一的第一命名空间 如 web<br>
 * USER_CHECK_URL:前台需要过滤的地址列表，多个以";"相隔<br>
 * @author changjq
 * 
 */
public abstract class LoginFilter implements Filter {
	/**
	 * 过滤失败的转发地址（后台）
	 */
	private String adminRedirectURL = null;

	/**
	 * 过滤失败的转发地址（前台）
	 */
	private String userRedirectURL = null;

	/**
	 * 不需要过滤的访问地址集合（后台）
	 */
	private List<String> adminNotCheckURLList = new ArrayList<String>();

	/**
	 * 需要过滤的访问地址集合（前台）
	 */
	private List<String> userCheckURLList = new ArrayList<String>();

	/**
	 * session key（后台）
	 */
	private String adminSessionKey = null;

	/**
	 * session key（前台）
	 */
	private String userSessionKey = null;

	/**
	 * URL开始字符串（后台）
	 */
	private String adminStartWith = null;

	/**
	 * URL开始字符串（前台）
	 */
	private String userStartWith = null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		adminNotCheckURLList.clear();
		userCheckURLList.clear();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (!this.definedFilter(servletRequest, servletResponse, filterChain)) {
			return;
		}
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
		String firstNamespace = this.getFirstNamespace(request);
		if (firstNamespace.equals(adminStartWith)) {// 后台请求
			if ((!adminNoFilterList(request)) && session.getAttribute(adminSessionKey) == null) {
				response.sendRedirect(request.getContextPath() + adminRedirectURL);
				return;
			}
		} else if (firstNamespace.equals(userStartWith)) {// 前台请求
			if ((userFilterList(request)) && session.getAttribute(userSessionKey) == null) {
				String uri = request.getRequestURI();
				if(request.getQueryString()!=null){
					uri = uri+ "?" + request.getQueryString();
				}
				String redirect = null;
				uri = java.net.URLEncoder.encode(uri,"utf-8");
				if(userRedirectURL.contains("?")){
					redirect = request.getContextPath() + userRedirectURL+"&"+Constant.LNPC_REDIRECT_URI+"="+uri;
				}else{
					redirect = request.getContextPath() + userRedirectURL+"?"+Constant.LNPC_REDIRECT_URI+"="+uri;
				}
				response.sendRedirect(redirect);
				return;
			}
		} else {
			
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	/**
	 * 自定义过滤方法,在doFilter之前调用
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param servletRequest
	 * @param servletResponse
	 * @param filterChain
	 * @return true 继续执行;false 转发
	 * @throws IOException
	 * @throws ServletException
	 */
	protected abstract boolean definedFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException;

	/**
	 * 判断当前请求是否需要过滤（后台）
	 * 
	 * @author changjq
	 * @date 2015年7月6日
	 * @param request
	 * @return true 不需要;false 需要
	 */
	protected boolean adminNoFilterList(HttpServletRequest request) {
		String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
		return adminNotCheckURLList.contains(uri);
	}

	/**
	 * 判断当前请求是否需要过滤（前台）
	 * 
	 * @author changjq
	 * @date 2015年7月6日
	 * @param request
	 * @return true 需要;false 不需要
	 */
	protected boolean userFilterList(HttpServletRequest request) {
		String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
		return userCheckURLList.contains(uri);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		adminRedirectURL = filterConfig.getInitParameter("ADMIN_REDIRECT_URL");
		userRedirectURL = filterConfig.getInitParameter("USER_REDIRECT_URL");
		adminSessionKey = filterConfig.getInitParameter("ADMIN_SESSION_KEY");
		userSessionKey = filterConfig.getInitParameter("USER_SESSION_KEY");
		adminStartWith = filterConfig.getInitParameter("ADMIN_START_WITH");
		userStartWith = filterConfig.getInitParameter("USER_START_WITH");
		String adminNotCheckURLListStr = filterConfig.getInitParameter("ADMIN_NO_CHECK_URL");
		String userCheckURLListStr = filterConfig.getInitParameter("USER_CHECK_URL");

		if (adminNotCheckURLListStr != null) {
			StringTokenizer st = new StringTokenizer(adminNotCheckURLListStr, ";");
			adminNotCheckURLList.clear();
			while (st.hasMoreTokens()) {
				adminNotCheckURLList.add(st.nextToken());
			}
		}
		if (userCheckURLListStr != null) {
			StringTokenizer st = new StringTokenizer(userCheckURLListStr, ";");
			userCheckURLList.clear();
			while (st.hasMoreTokens()) {
				userCheckURLList.add(st.nextToken());
			}
		}
		this.definedInit(filterConfig);
	}

	/**
	 * 获取请求地址的一个命名空间
	 * 
	 * @author changjq
	 * @date 2015年7月6日
	 * @param request
	 * @return 请求地址的一个命名空间
	 */
	protected String getFirstNamespace(HttpServletRequest request) {
		String currentServletPath = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
		currentServletPath = currentServletPath.substring(1);
		int index = currentServletPath.indexOf("/");
		return currentServletPath.substring(0, index);
	}

	/**
	 * 自定义init方法,在init之后调用
	 * 
	 * @author changjq
	 * @date 2014年11月28日
	 * @param filterConfig
	 * @throws ServletException
	 */
	protected abstract void definedInit(FilterConfig filterConfig) throws ServletException;

	protected String getAdminSessionKey() {
		return adminSessionKey;
	}
	
	protected String getUserSessionKey() {
		return userSessionKey;
	}

	protected String getAdminStartWith() {
		return adminStartWith;
	}

	protected String getUserStartWith() {
		return userStartWith;
	}

}