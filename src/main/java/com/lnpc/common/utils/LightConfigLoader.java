package com.lnpc.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LightConfigLoader {
	private static Logger logger = LoggerFactory.getLogger(LightConfigLoader.class);
	private Map<String,String> infoMap=null;
	public Map<String,String> getInfoMap(){
		return this.infoMap;
	}
	public LightConfigLoader(String fileName){
		this.infoMap=loadConfig(fileName);
	}
	private Map<String,String> loadConfig(String fileName){
		Map<String,String> resourceMap=new HashMap<String,String>();
		String resPath="";
		if(fileName.split("/").length>1 || fileName.split("\\\\").length>1){
			resPath=fileName;
		}
		if(!FileUtils.isExistFile(resPath)){
			return resourceMap;
		}
		ResourceHandler resourceHandler=new ResourceHandler();
		try{
			resourceHandler.initPropertyResourceBundle(resPath);
		}
		catch(Exception e){
			logger.error("{} resourceHandler initPropertyResourceBundle failed.",resPath);
			e.printStackTrace();
		}
		Set<String> keys=resourceHandler.resourceBundle.keySet();
		Iterator<String> ite= keys.iterator();
		String tempKey=null;
		while(ite.hasNext()){
			tempKey=ite.next();
			resourceMap.put(tempKey, resourceHandler.getResourceValue(tempKey));
		}
		return resourceMap;
	}
	public String getValue(String key){
		if(null==key){
			return "";
		}
		return this.infoMap.get(key)==null?"":this.infoMap.get(key);
	}
}
