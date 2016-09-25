package com.lnpc.common.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.lnpc.common.utils.HttpUtils;

/**
 * 文件下载类
 * 
 * @author changjq
 * 
 */
public class FileDownload {
	private static Logger logger = Logger.getLogger(FileDownload.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	/**
	 * 下载文件的保存名称
	 */
	private String saveFileName;
	/**
	 * 下载文件路径
	 */
	private String downloadFilePath;
	/**
	 * 下载流
	 */
	private InputStream fileInputStream;

	/**
	 * 构造函数 初始化
	 * 
	 * @param req
	 * @param resp
	 */
	public FileDownload(HttpServletRequest req, HttpServletResponse resp) {
		this.request = req;
		this.response = resp;
	}

	/**
	 * 流方式下载
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return true 成功;false 失败
	 */
	public boolean downloadByStream() {
		if (this.fileInputStream == null) {
			logger.error("failed to download file,the inputStream is null");
			return false;
		}
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-disposition", "attachment;filename=" + HttpUtils.getEncodeSaveName(saveFileName, request));
		ServletOutputStream output = null;
		try {
			output = response.getOutputStream();
			byte[] block = new byte[2048];
			int len = 0;
			while ((len = fileInputStream.read(block)) != -1) {
				output.write(block, 0, len);
			}
			output.flush();
			return true;
		} catch (IOException e) {
			logger.error("failed to download file " + e.getMessage());
			return false;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	/**
	 * 文件路径方式下载，此文件必须存在
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @return true 成功;false 失败
	 */
	public boolean downloadByPath() {
		boolean ret = false;
		File file = new File(this.downloadFilePath);
		if (!file.exists()) {
			logger.error("failed to download file:" + this.downloadFilePath + ".");
			return false;
		}
		long fileLength = file.length();
		String length = String.valueOf(fileLength);
		response.setHeader("Content_Length", length);
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			this.fileInputStream = input;
			ret = this.downloadByStream();
		} catch (FileNotFoundException e) {
			logger.error("failed to download file:" + this.downloadFilePath + ".");
		}
		return ret;
	}

	/**
	 * 设置下载文件名
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param saveFileName
	 */
	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	/**
	 * 设置下载的文件全路径
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param downloadFilePath
	 */
	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}

	/**
	 * 设置下载的文件流
	 * 
	 * @author changjq
	 * @date 2015年6月5日
	 * @param fileInputStream
	 */
	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

}
