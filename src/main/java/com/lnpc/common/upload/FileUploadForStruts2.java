package com.lnpc.common.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.lnpc.common.utils.FileUtils;
import com.lnpc.common.utils.StringUtils;

/**
 * struts2上传方式
 * 
 * @author changjq
 * 
 */
public class FileUploadForStruts2 {
	private List<File> uploadFiles;
	private List<String> uploadFilesFileName;
	private List<String> uploadFilesContentType;
	private String pathName = "files";
	private String defaultPath;
	private HttpServletRequest request;

	/**
	 * 初始化
	 * 
	 * @param request
	 */
	public FileUploadForStruts2(HttpServletRequest request) {
		this.request = request;
		this.defaultPath = this.request.getSession().getServletContext().getRealPath("") + "\\" + pathName + "\\";
	}

	/**
	 * 获取上传文件
	 * 
	 * @author changjq
	 * @return 上传文件
	 */
	public List<File> getUploadFiles() {
		return uploadFiles;
	}

	/**
	 * 设置上传文件
	 * 
	 * @author changjq
	 * @param uploadFiles
	 */
	public void setUploadFiles(List<File> uploadFiles) {
		this.uploadFiles = uploadFiles;
	}

	/**
	 * 获取上传文件名
	 * 
	 * @author changjq
	 * @return 上传文件名
	 */
	public List<String> getUploadFilesFileName() {
		return uploadFilesFileName;
	}

	/**
	 * 设置上传文件名
	 * 
	 * @author changjq
	 * @param 上传文件名
	 */
	public void setUploadFilesFileName(List<String> uploadFilesFileName) {
		this.uploadFilesFileName = uploadFilesFileName;
	}

	/**
	 * 获取上传文件的ContentType
	 * 
	 * @author changjq
	 * @return 上传文件的ContentType
	 */
	public List<String> getUploadFilesContentType() {
		return uploadFilesContentType;
	}

	/**
	 * 设置上传文件的ContentType
	 * 
	 * @author changjq
	 * @param uploadFilesContentType
	 */
	public void setUploadFilesContentType(List<String> uploadFilesContentType) {
		this.uploadFilesContentType = uploadFilesContentType;
	}

	/**
	 * 设置上传文件目录，该目录必须存在且在根目录下
	 * 
	 * @author changjq
	 * @param pathName
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
		this.defaultPath = this.request.getSession().getServletContext().getRealPath("") + "\\" + pathName + "\\";
	}

	/**
	 * 上传单个文件
	 * 
	 * @author changjq
	 * @param file
	 * @param fileName
	 * @return 上传文件路径
	 */
	public String upload(File file, String fileName) {
		try {
			fileName = StringUtils.getRandomFileName(fileName);
			FileUtils.copyFile(new FileInputStream(file), new FileOutputStream(this.defaultPath + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		return this.defaultPath + fileName;
	}

	/**
	 * 上传文件
	 * 
	 * @author changjq
	 */
	public void upload() {
		if (this.uploadFiles != null) {
			for (int i = 0; i < this.uploadFiles.size(); i++) {
				try {
					String fileName = this.uploadFilesFileName.get(i);
					fileName = StringUtils.getRandomFileName(fileName);
					FileUtils.copyFile(new FileInputStream(this.uploadFiles.get(i)), new FileOutputStream(this.defaultPath + fileName));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
