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

import org.apache.commons.fileupload.FileUploadException;

import com.lnpc.common.config.Constant;
import com.lnpc.common.upload.FileUpload;

/**
 * 抽象登陆过滤器
 * 
 * @author changjq
 * 
 */
public abstract class CheckLoginFilter implements Filter {
	/**
	 * 过滤失败的转发地址
	 */
	private String redirectURL = null;
	/**
	 * 不需要过滤的访问地址集合
	 */
	private List<String> notCheckURLList = new ArrayList<String>();
	/**
	 * session key
	 */
	private String sessionKey = null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		notCheckURLList.clear();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		if (!this.definedFilter(servletRequest, servletResponse, filterChain)) {
			return;
		}
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
		if ((!checkRequestURIIntNotFilterList(request)) && session.getAttribute(sessionKey) == null) {
			response.sendRedirect(request.getContextPath() + redirectURL);
			return;
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
	 * 判断当前请求是否需要过滤
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param request
	 * @return true 不需要;false 需要
	 */
	private boolean checkRequestURIIntNotFilterList(HttpServletRequest request) {
		String uri = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
		return notCheckURLList.contains(uri);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		redirectURL = filterConfig.getInitParameter("redirectURL");
		sessionKey = filterConfig.getInitParameter("checkSessionKey");
		String notCheckURLListStr = filterConfig.getInitParameter("notCheckURLList");

		if (notCheckURLListStr != null) {
			StringTokenizer st = new StringTokenizer(notCheckURLListStr, ";");
			notCheckURLList.clear();
			while (st.hasMoreTokens()) {
				notCheckURLList.add(st.nextToken());
			}
		}
		this.definedInit(filterConfig);
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
}
