package com.github.ulwx.aka.dbutils.tool.support;

/**
 * <p>Title:Path </p>
 * <p>Description: 文件路径的处</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company:ldsoft </p>
 * @author xuyw
 * @version 1.0
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


public class Path {
	private static Logger log = LoggerFactory.getLogger(Path.class);
	
	/**
	 * 得到当前工程的类加载路径
	 * @return
	 */
	public static String getClassPath() {
		return getRootClassPath();
	}
	public static String getRootClassPath(){

		String str= Path.class.getResource("/").getPath();
		if(!str.startsWith("file:")) {
			str="file:"+str;
		}   
	    try{
	    	return new File(new URI(str)).getAbsolutePath();
	    	
	    }catch(Exception e){
	    	return null;
	    }
	}
	

	public static String getCurClassExecutePath(Class clazz) {
		try{
		return new File(clazz.getResource("").toURI()).getAbsolutePath();
		}catch(Exception e){
			return null;
		}
	}
	public static String  getRootClassPathFromCurTreadLoader(String context) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(context);
		if(url==null) return "";
		String protocol = url.getProtocol();

		// 如果是以文件的形式保存在服务器上
		if ("file".equals(protocol)  ) {
			String filePath="";
			try {
				filePath = URLDecoder.decode(url.getFile(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				
			}
			return filePath;
		}
		return "";
	}
	
	public static void findAndAddInPackageByFile(String fromDir, final boolean recursive,
			List<String> fileNames,String suffix) {
		
		// 获取此包的目录 建立一个File
		File dir = new File(fromDir);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以suffix结尾的文件
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(suffix));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddInPackageByFile( file.getAbsolutePath(), recursive,
						fileNames,suffix);
			} else {
				String fileName = file.getAbsolutePath();
				try {
					// 添加到集合中去
					fileNames.add(fileName);
				} catch (Exception e) {
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					log.error("", e);
				}
			}
		}

	}

	public static String getCurClassExecutePath() {
		return getCurClassExecutePath(Path.class);
	}

	public static String getRootClassPath(String name) {
		return getRootClassPath()+File.separator+name ;
	}



	/**
	 * 可读入jar包里的文件,传入的参数如：/1.xml
	 * 
	 * @param relaPathFile
	 * @return
	 */
	public static InputStream getResource(String relaPathFile) {
		return Path.class.getResourceAsStream(relaPathFile);
	}

	public static InputStream getClassPathResource(String fileName)
			throws IOException {

		FileInputStream fin = new FileInputStream(getRootClassPath(fileName));
		return fin;
	}

	public static void main(String args[]) throws Exception{
		String str=Path.getCurClassExecutePath();
		List<String> list=new ArrayList<>();
		findAndAddInPackageByFile(str,true,list,".class");
		
		System.out.println(ObjectUtils.toString(list));
		
		System.out.println(getRootClassPath("com"));
		System.out.println(getRootClassPath());
		
	}

}
