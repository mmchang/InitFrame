package com.lnpc.common.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.lnpc.common.DataCenter;

/**
 * http相关操作类
 * 
 * @author changjq
 * 
 */
public class HttpUtils {
	private static Logger logger = Logger.getLogger(HttpUtils.class);

	/**
	 * 根据不同浏览器的编码，获取编码后的下载文件名
	 * 
	 * @author changjq
	 * @param fileSaveName
	 *            源文件名
	 * @param request
	 * @return 编码后的文件名
	 */
	public static String getEncodeSaveName(String fileSaveName, HttpServletRequest request) {
		String agent = request.getHeader("User-Agent");
		String savingName = "";
		if (null != agent) {
			agent = agent.toLowerCase();
			try {
				if (agent.indexOf("firefox") != -1) {
					savingName = new String(fileSaveName.getBytes(), "iso8859-1");
				} else {
					savingName = java.net.URLEncoder.encode(fileSaveName, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("Error occurs when transfer code");
				e.printStackTrace();
			}
		}
		return savingName;
	}

	/**
	 * 异步向客户端写数据（UTF8）
	 * 
	 * @author changjq
	 * @param response
	 * @param data
	 */
	public static void writeResponse(HttpServletResponse response, Object data) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(String.valueOf(data));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回客户端时调用的方法，做一些反回前的处理
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param str
	 * @param req
	 * @param resp
	 * @return struts2's result
	 */
	public static String result(String str, DataCenter req, DataCenter resp) {
		String ret = null;
		if ("true".equals(req.getAsync())) {
			writeResponse(ServletActionContext.getResponse(), resp.toJSON());
		} else {
			if (resp.finish() == 0) {
				ret = str;
			}
		}
		return ret;
	}
}
