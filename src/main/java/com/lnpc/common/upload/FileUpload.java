package com.lnpc.common.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lnpc.common.utils.StringUtils;

/**
 * servlet方式上传
 * @author changjq
 *
 */
public class FileUpload {
	protected Map<String, String> mpParameters = new HashMap<String, String>(); 

	protected Map<String, FileItem> mpFiles = new HashMap<String, FileItem>();

	private long sizeMax = 2147483648L;

	private String encoding = "UTF-8";

	private String pathName = "files";

	private String defaultPath = null;

	private HttpServletRequest request;
	public FileUpload(HttpServletRequest request) throws FileUploadException, UnsupportedEncodingException
	{
		this.request = request;
		this.process();
	}
	public void process() throws FileUploadException, UnsupportedEncodingException
	{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		factory.setSizeThreshold(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD);
		upload.setHeaderEncoding(encoding);
		upload.setSizeMax(sizeMax);
		List<?> items =null;
		try{
			items = upload.parseRequest(request);
		}
		catch(Exception e){
			return;
		}
		Iterator<?> iterator = items.iterator();
		while (iterator.hasNext())
		{
			FileItem item = (FileItem) iterator.next();
			if (item.isFormField())
			{
				//解决获取表单中字段的乱码问题
				mpParameters.put(item.getFieldName(), item.getString(encoding));
			}
			else
			{
				mpFiles.put(item.getFieldName(), item);
			}
		}
	}
	public void setSizeMax(long sizeMax)
	{
		this.sizeMax = sizeMax;
	}

	public void setFileCatalog(String pathName)
	{
		this.pathName = pathName;
		this.defaultPath = this.request.getSession().getServletContext().getRealPath("") + "\\" + pathName + "\\";
	}
	public String upLoad(FileItem fileItem){
		String strFileName = "";
		if (defaultPath == null)
		{
			defaultPath = this.request.getSession().getServletContext().getRealPath("") + "\\" + pathName + "\\";
		}
		strFileName = getFileName(fileItem);
		if (!strFileName.equals(""))
		{
			strFileName = StringUtils.getRandomFileName(strFileName);
			File file = new File(defaultPath + strFileName);
			try {
				fileItem.write(file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		if (strFileName.equals(""))
		{
			return "";
		}
		return strFileName;
	}
	public void upLoad()
	{
		String strFileName = "";
		Iterator<FileItem> iterator = mpFiles.values().iterator();
		try
		{
			if (defaultPath == null)
			{
				defaultPath = this.request.getSession().getServletContext().getRealPath("") + "\\" + pathName + "\\";
			}

			while (iterator.hasNext())
			{
				FileItem fiItem = (FileItem) iterator.next();
				//strFileName = getRandomFileName(getFileName(fiItem));
				strFileName = getFileName(fiItem);
				if (!strFileName.equals(""))
				{
					strFileName = StringUtils.getRandomFileName(strFileName);
					File file = new File(defaultPath + strFileName);
					fiItem.write(file);
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private String getFileName(FileItem item)
	{
		String fileName;
		if (item.getName().equals(""))
		{
			fileName = item.getFieldName().replaceAll("\\\\", "/");
		}
		else
		{
			fileName = item.getName().replaceAll("\\\\", "/");
		}
		return fileName.substring(fileName.lastIndexOf("/") + 1);
	}

	public String getParameter(String name)
	{
		return (String) mpParameters.get(name);
	}
	public FileItem getFileItem(String name){
		return mpFiles.get(name);
	}
	public int getFilesCount()
	{
		return mpFiles.size();
	}
	public Map<String, FileItem> getMpFiles() {
		return mpFiles;
	}
}
