package com.lnpc.common.upload;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.alibaba.fastjson.JSONObject;
import com.lnpc.common.utils.HttpUtils;
/**
 * 针对普通上传方式的验证文件大小
 * @author changjq
 *
 */
public class ValiFileSize extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2080150799617543697L;
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		JSONObject retObj = new JSONObject();
		retObj.put("success", true);
		retObj.put("realSize", "0");
		try {
			FileUpload upload = new FileUpload(request);
			Map<String, FileItem> fileMap = upload.getMpFiles();
			if(fileMap!=null){
				String fileName = request.getParameter("valiFileName");
				String fileSizeStr = request.getParameter("valiFileSize");
				long fileSize = Long.valueOf(fileSizeStr);
				FileItem item = fileMap.get(fileName);
				if(item!=null){
					boolean sizeFlag = true;
					retObj.put("realSize", item.getSize());
					if(item.getSize()>fileSize){
						sizeFlag = false;
					}else{
						String finalFileName =  upload.upLoad(item);
						retObj.put("uploadFileName", finalFileName);
					}
					retObj.put("sizeFlag", sizeFlag);
				}
			}
		} catch (FileUploadException e) {
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
