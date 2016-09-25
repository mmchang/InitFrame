package com.lnpc.spring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lnpc.common.config.Constant;
import com.lnpc.spring.annotation.ManagePassport;

/**
 * 后台登录验证
 * @author cjq
 *
 */
public class ManagePassportInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
			ManagePassport passport = ((HandlerMethod) handler).getMethodAnnotation(ManagePassport.class);

			if (passport == null) {
				return true;
			} else {
				if (request.getSession().getAttribute(Constant.Login.CURRENT_ADMIN) != null) {
					return true;
				} else {
					response.sendRedirect("/manage/login/login.htm");
					return false;
				}
			}
		} else
			return true;
	}
}
