package com.lnpc.spring.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.lnpc.common.utils.JsonUtils;
import com.lnpc.spring.annotation.LnpcDataCenter;

/**
 * 在SpringMVC中动态添加参数，需要配置在Spring的XML中
 * @author cjq
 *
 */
public class DataCenterArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		HttpServletRequest req = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
		HttpServletResponse resp = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
		return JsonUtils.getDataCenter(req, resp);
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		
		/** 只支持基于注解的参数解析 */
		return methodParameter.getParameterAnnotation(LnpcDataCenter.class) != null;  
	}

}
