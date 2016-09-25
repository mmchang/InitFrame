package com.lnpc.common.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class ResourceHandler {
	ResourceBundle resourceBundle;
	private static Logger logger = Logger.getLogger(ResourceHandler.class);
	public void initPropertyResourceBundle(String resPath) throws Exception{
		InputStream in=null;
		FileInputStream fileInputStream=null;
		try{
			fileInputStream=new FileInputStream(resPath);
			resourceBundle=new PropertyResourceBundle(new InputStreamReader(fileInputStream,"UTF-8"));
		}
		catch(Exception e){
			logger.error("load the resource file "+resPath+"failed.");
			e.printStackTrace();
		}
		finally{
			if(null!=fileInputStream){
				try{
					fileInputStream.close();
				}
				catch(Exception e){
					logger.error("close stream failed.");
					e.printStackTrace();
				}
				fileInputStream=null;
			}
			if(null!=in){
				try{
					in.close();
				}
				catch(Exception e){
					logger.error("close stream failed.");
					e.printStackTrace();
				}
				in=null;
			}
		}
	}
	public String getResourceValue(String key){
		String value=null;
		if(key==null){
			return null;
		}
		String tempValue=resourceBundle.getString(key);
		try{
			if(tempValue!=null){
				value=tempValue;
			}
			else{
				return key;
			}
		}
		catch(Exception e){
			logger.error("convert resource value to utf-8 failed.");
			e.printStackTrace();
		}
		return value;
	}
}
