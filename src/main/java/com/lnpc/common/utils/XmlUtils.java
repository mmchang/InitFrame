package com.lnpc.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xml工具类（基于dom4j）
 * 
 * @author changjq
 * 
 */
public final class XmlUtils {
	private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

	/**
	 * 
	 * <p>Description: 生成Documnet对象</p>
	 * @param object
	 * @param validate
	 * @return
	 * @author changjq
	 * @date 2016年9月24日
	 */
	private static Document generateDocumnet(Object object,boolean validate){
		SAXReader reader = new SAXReader(validate);
		try {
			Document document = null;
			if(object instanceof String){
				document = reader.read(new StringReader((String)object));
			}else if(object instanceof File){
				document = reader.read((File)object);
			}else if(object instanceof InputStream){
				document = reader.read((InputStream)object);
			}
			return document;
		} catch (DocumentException e) {
			logger.error("Faild to read xmlContent...");
			logger.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获取Document对象
	 * 
	 * @param xmlContent
	 * @return Document对象
	 */
	public static Document createDocument(String xmlContent) {
		return generateDocumnet(xmlContent,false);
	}

	/**
	 * 获取Document对象
	 * 
	 * @param xmlContent
	 * @param validate
	 *            是否验证格式
	 * @return Document对象
	 */
	public static Document createDocument(String xmlContent, boolean validate) {
		return generateDocumnet(xmlContent,validate);
	}

	/**
	 * 获取Document对象
	 * 
	 * @param file
	 * @param validate
	 *            是否验证格式
	 * @return Document对象
	 * @throws Exception
	 */
	public static Document createDocument(File file, boolean validate){
		return generateDocumnet(file,validate);
	}

	/**
	 * 获取Document对象
	 * 
	 * @param file
	 * @return Document对象
	 */
	public static Document createDocument(File file) {
		return generateDocumnet(file, false);
	}


	/**
	 * 获取Document对象
	 * 
	 * @param io
	 * @return Document对象
	 */
	public static Document createDocument(InputStream io,boolean validate) {
		return generateDocumnet(io, validate);
	}

	/**
	 * 获取Document对象
	 * 
	 * @param io
	 * @return Document对象
	 */
	public static Document createDocument(InputStream io) {
		return generateDocumnet(io, false);
	}


	/**
	 * 获取节点xml格式
	 * 
	 * @param parent
	 * @return 节点xml格式
	 */
	public static String print(Element parent) {
		return parent.asXML();
	}

	/**
	 * 获取document的xml格式
	 * 
	 * @param document
	 * @return document的xml格式
	 */
	public static String print(Document document) {
		return document.asXML();	
	}

	/**
	 * 
	 * <p>Description:将document写入targetPath</p>
	 * @param document
	 * @param targetPath
	 * @param encode
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public static void write(Document document, String targetPath,String encode) {
		try {
			XMLWriter writer = new XMLWriter( new OutputStreamWriter(new FileOutputStream(targetPath),encode));
			writer.write(document);
			writer.close();
		} catch (IOException e) {
			logger.error("Faild to wirte xml...");
		}
	}
	
	/**
	 * 
	 * <p>Description:Description:将document写入targetPath</p>
	 * @param document
	 * @param targetPath
	 * @author changjq
	 * @date 2016年9月24日
	 */
	public static void write(Document document, String targetPath){
		write(document, targetPath,"UTF-8");
	}
}
