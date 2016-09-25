package com.lnpc.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lnpc.common.config.Constant;
import com.lnpc.spring.annotation.WebPassport;

/**
 * 前台登录验证
 * @author cjq
 *
 */
public class WebPassportInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			WebPassport passport = ((HandlerMethod) handler).getMethodAnnotation(WebPassport.class);

			if (passport == null) {
				return true;
			} else {
				if (request.getSession().getAttribute(Constant.Login.CURRENT_USER) != null) {
					return true;
				} else {
					response.sendRedirect("/web/login/login.htm");
					return false;
				}
			}
		} else
			return true;
	}
}
