package com.github.ulwx.database;

import java.util.HashMap;
import java.util.Map;

public class DataBaseKeyMap {

	public static Map<String,String> map=null;
	static{
		init();
	}
	public static void init(){
		map=new HashMap<String,String>();
		map.put("$class", "class");
	}
	public static Map<String,String> getMap(){
		if(map==null){
			init();
		}
		return map;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//TreeClass
	}

}
