/*
 * Copyright 2004-2008 the original author or authors.
 * Licensed under 3G门户
 */
package com.github.ulwx.aka.dbutils.database.dbpool;

import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.Path;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReadConfig {

	private Logger log = LoggerFactory.getLogger(ReadConfig.class);
	private Map<String, Map<String, String>> properties = null;
	private Map<String,Map<String,Map<String,String>>> slaveProperites=null;
	private Map<String, String> glsettings = null;
	private volatile   static ReadConfig config = null;

	private ReadConfig() {
		parse();
	}

	public static ReadConfig getInstance() {
		if(config==null){
			synchronized (ReadConfig.class) {
				if(config==null) config=new ReadConfig();
			}
		}
		return config;
	}


	public Map<String, Map<String, Map<String, String>>> getSlaveProperites() {
		return slaveProperites;
	}

	public void setSlaveProperites(
			Map<String, Map<String, Map<String, String>>> slaveProperites) {
		this.slaveProperites = slaveProperites;
	}

	public Map<String, Map<String, String>> getProperties() {
		return properties;
	}
	public Map<String, String> getGlSettings() {
		return glsettings;
	}

	/**
	 * 读配置文件
	 * 
	 * @return
	 */
	private void parse() {

		Map<String, Map<String, String>> maps = new ConcurrentHashMap<String, Map<String, String>>();
		Map<String,Map<String,Map<String,String>>> slaveMaps = new ConcurrentHashMap<String,Map<String,Map<String,String>>>();
		Map<String,String> settings=new ConcurrentHashMap<>();
		SAXReader reader=null;
		try {
			reader = new SAXReader();
	        //读取文件 转换成Document  
	        Document doc = reader.read(Path.getResource("/dbpool.xml"));
	        //获取根节点元素对象  
	        Element root = doc.getRootElement();
			List<Node> tableNameRule=root.selectNodes("./setting/table-name-rule");
			if(tableNameRule!=null && tableNameRule.size()>0) {
				settings.put("table-name-rule", ((Element)tableNameRule.get(0)).getTextTrim());
			}

			List<Node> tableColumRule=root.selectNodes("./setting/table-colum-rule");
			if(tableColumRule!=null && tableColumRule.size()>0) {
				settings.put("table-colum-rule",((Element)tableColumRule.get(0)).getTextTrim());
			}

	        @SuppressWarnings("unchecked")
			Iterator<Element> iterator=root.elementIterator("dbpool");
	        while(iterator.hasNext()){  
	            Element config_el = iterator.next();  
	            String configName = config_el.attributeValue("name");
				String type = config_el.attributeValue("type");
				Map<String, String> map = new HashMap<String, String>();
				maps.put(configName, map);
				map.put("type", type);
				String tableNameRule2 = StringUtils.trim(config_el.attributeValue("table-name-rule"));
				String tableColumRule2 = StringUtils.trim(config_el.attributeValue("table-colum-rule"));
				if(StringUtils.isEmpty(tableNameRule2)){
					tableNameRule2=settings.get("table-name-rule");
					if(StringUtils.isEmpty(tableNameRule2)){
						throw  new RuntimeException("没有定义table-name-rule规则");
					}
				}
				map.put("table-name-rule", tableNameRule2);
				if(StringUtils.isEmpty(tableColumRule2)){
					tableColumRule2=settings.get("table-colum-rule");
					if(StringUtils.isEmpty(tableColumRule2)){
						throw  new RuntimeException("没有定义table-colum-rule规则");
					}
				}
				map.put("table-colum-rule", tableColumRule2);

				@SuppressWarnings("unchecked")
				Iterator<Element> nList = config_el.elementIterator("property");
				while(nList.hasNext()) {
					Element propertye_nn=nList.next();
					String name = ((Element)propertye_nn).attributeValue("name");
					String value = ((Element)propertye_nn).getTextTrim();
					map.put(name, value);

				}
				Map<String,Map<String,String>> slaveServers=new HashMap<String,Map<String,String>>();
				slaveMaps.put(configName, slaveServers);
				Iterator<Node> nSlaveList = config_el.selectNodes(".//server").iterator();
				while(nSlaveList.hasNext()) {
					Element server_el=(Element) nSlaveList.next();
					String serverName=server_el.attributeValue("name");
					Iterator<Element> nSlavePList = server_el.elementIterator("property");
					Map<String, String> mapp = new HashMap<String, String>();
					slaveServers.put(serverName, mapp);
					while(nSlavePList.hasNext()) {
						Element pro_el = (Element) nSlavePList.next();
						String name = pro_el.attributeValue("name");
						String value = pro_el.getTextTrim();
						mapp.put(name, value);

					}
				}
	        }


		} catch (Exception e) {
			log.error("读取XML文档异常。。。。", e);
		}finally{
		
		}
		properties = maps;
		slaveProperites=slaveMaps;
		glsettings=settings;
	}

	/**
	 * 测试配置文件
	 */
	public void testReadCfg() {

	}

	public static void main(String[] args) {
		// ReadConfig.parse();
		//
		// System.out.println(CollectionUtils.toString(ReadConfig.properties));
		// System.out.println(CollectionUtils.toString(ReadConfig.trans));
		ReadConfig rf=new ReadConfig();
		System.out.println(ObjectUtils.toString(rf.properties));
		System.out.println(ObjectUtils.toString(rf.slaveProperites));
	}
}
