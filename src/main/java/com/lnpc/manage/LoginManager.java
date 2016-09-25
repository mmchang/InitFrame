package com.lnpc.manage;

import java.io.File;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.lnpc.common.DataCenter;
import com.lnpc.common.config.Constant;
import com.lnpc.common.utils.BeanUtils;
import com.lnpc.common.utils.XmlUtils;
import com.lnpc.iface.login.ILogin;
/**
 * 登陆管理器
 * @author changjq
 *
 */
public class LoginManager {
	
	private LoginManager(){
		
	}
	/**
	 * 加载login-config.xml文件中的登陆实现类，并执行check方法<br>
	 * 使用manageLogin或webLogin代替
	 * @author changjq
	 * @date 2015年6月5日
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@Deprecated
	public final static void login(DataCenter request,DataCenter response) throws Exception{
		final String path = ConfigManager.getSysPath(Constant.LOGIN_PATH);
		commonLogin(request, response, path);
	}
	
	private final static void commonLogin(DataCenter request,DataCenter response,String path) throws Exception{
		Document document = XmlUtils.createDocument(new File(path));
		List<Element> loginList = XmlUtils.selectNodes(document, "loginModlue");
		for(int i=0;i<loginList.size();i++){
			Element elem = loginList.get(i);
			if("true".equals(elem.getAttributeValue("load"))){
				String loginClass = elem.getTextTrim();
				ILogin loginService = (ILogin) BeanUtils.getBean(loginClass);
				loginService.check(request,response);
			}
		}
	}
	
	/**
	 * 后台登录调用
	 * @Title: manageLogin
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月10日 下午2:26:36 
	 * @param request
	 * @param response
	 * @return: void
	 * @throws Exception 
	 */
	public final static void manageLogin(DataCenter request,DataCenter response) throws Exception{
		final String path = ConfigManager.getSysPath(Constant.MANAGE_LOGIN_PATH);
		commonLogin(request, response, path);
	}
	
	/**
	 * 前台登录调用
	 * @Title: webLogin
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @author: cjq  
	 * @date:2015年12月10日 下午2:26:54 
	 * @param request
	 * @param response
	 * @return: void
	 * @throws Exception 
	 */
	public final static void webLogin(DataCenter request,DataCenter response) throws Exception{
		final String path = ConfigManager.getSysPath(Constant.WEB_LOGIN_PATH);
		commonLogin(request, response, path);
	}
}
