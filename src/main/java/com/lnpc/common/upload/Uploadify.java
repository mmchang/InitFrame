package com.lnpc.common.upload;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.lnpc.common.utils.HttpUtils;
/**
 * 针对uploadify插件的上传servlet
 * @author changjq
 *
 */
public class Uploadify extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5080687975412396803L;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		JSONObject retObj = new JSONObject();
		retObj.accumulate("success", true);
		try {
			FileUpload upload = new FileUpload(request);
			String folder = request.getParameter("uploadFolder");
			if(folder!=null){
				upload.setFileCatalog(folder);
			}
			Map<String, FileItem> fileMap = upload.getMpFiles();
			Set<String> keySet = fileMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while(iterator.hasNext()){
				String key = (String) iterator.next();
				FileItem item = fileMap.get(key);
				String realFileName = upload.upLoad(item);
				retObj.accumulate(key, realFileName);
			}
			
		} catch (FileUploadException e) {
			retObj.accumulate("success", false);
			e.printStackTrace();
		}
		finally{
			HttpUtils.writeResponse(response, retObj.toString());
		}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		this.doPost(request, response);
	}
}
