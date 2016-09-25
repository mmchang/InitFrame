package com.lnpc.iface.login;

import com.lnpc.common.DataCenter;

/**
 * 登陆接口<br>
 * 所有实现类应该存在与login-config.xml中
 * 
 * @author changjq
 * 
 */
public interface ILogin {
	/**
	 * 登陆需要验证的方法
	 * 
	 * @author changjq
	 * @param request
	 * @param response
	 * @throws Exception
	 *             required
	 */
	public void check(DataCenter request, DataCenter response) throws Exception;
}
