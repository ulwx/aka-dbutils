package com.github.ulwx.tool.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

public class FileUtils {

	private static Logger log = LoggerFactory.getLogger(FileUtils.class);

	public static void write(String pathName, String content)
			throws IOException {

		write(pathName, content, false);

	}

	public static void write(String pathName, byte[] bs) throws Exception {
		FileUtils.makeDirectory(FileUtils.getFileParentPath(pathName));

		try {
			File file = new File(pathName);
			org.apache.commons.io.FileUtils.writeByteArrayToFile(file, bs);
		} catch (Exception e) {
			log.error("", e);
			throw e;
		} finally {

		}
	}

	public static void write(String pathName, String content, boolean append)
			throws IOException {
		PrintWriter out = null;
		try {
			FileUtils.makeDirectory(FileUtils.getFileParentPath(pathName));
			FileOutputStream fOut = new FileOutputStream(pathName, append);

			out = new PrintWriter(fOut);
			out.print(content);
		} catch (IOException e) {
			log.error("", e);
			// throw the exception
			throw e;
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	public static void write(String pathName, String content, String charset)
			throws IOException {

		write(pathName, content, false, charset);

	}

	public static void write(String pathName, String content, boolean append,
			String charset) throws IOException {
		OutputStreamWriter out = null;
		try {
			// makeDirFromPathName(pathName);
			FileUtils.makeDirectory(FileUtils.getFileParentPath(pathName));
			FileOutputStream fOut = new FileOutputStream(pathName, append);

			out = new OutputStreamWriter(fOut, charset);
			out.write(content);
		} catch (IOException e) {
			log.error("", e);
			throw e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
					log.error("", e);
				}
			}
		}

	}



	/**
	 * 判断指定的文件是否存在。
	 *
	 * @param fileName
	 *            要判断的文件的文件名
	 * @return 存在时返回true，否则返回false。
	 * @since 0.1
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			return true;
		}
		return false;
	}

	public static boolean isDirExist(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		return false;
	}

	/**
	 * 创建指定的目录。 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。 <b>注意：可能会在返回false的时候创建部分父目录。</b>
	 *
	 * @param file
	 *            要创建的目录
	 * @return 完全创建成功时返回true，否则返回false。
	 * @since 0.1
	 */
	public static boolean makeDirectory(File file) {
		/*
		 * File parent = file.getParentFile(); if (parent != null) { boolean r=
		 * parent.mkdirs(); System.out.println(r); }
		 */

		try {

			boolean r = file.mkdirs();
			// file.
			return r;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// log.error("", e);
			log.error("", e);
		}
		return false;

	}

	/**
	 * 创建指定的目录。 如果指定的目录的父目录不存在则创建其目录书上所有需要的父目录。 <b>注意：可能会在返回false的时候创建部分父目录。</b>
	 *
	 * @param fileName
	 *            要创建的目录的目录名
	 * @return 完全创建成功时返回true，否则返回false。
	 * @since 0.1
	 */
	public static boolean makeDirectory(String fileName) {
		// System.out.println("");
		File file = new File(fileName);

		return makeDirectory(file);
	}



	/**
	 * 返回文件夹里的文件名
	 *
	 * @param filePathName
	 * @return
	 */
	public static String[] list(String filePathName) {
		File f = new File(filePathName);
		String[] result = f.list();
		if (result == null) {
			return new String[0];
		} else {
			return result;
		}
	}

	public static String[] getTopLevelDirName(String dirpath) {
		File file = new File(dirpath);
		if (file == null)
			return null;
		ArrayList list = new ArrayList();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					list.add(files[i].getName());
				}
			}
		} else {
			return null;
		}
		return StringUtils.toStringArray(list);
	}

	/**
	 * 从文件路径得到文件名。
	 * 
	 * @param filePath
	 *            文件的路径，可以是相对路径也可以是绝对路径
	 * @return 对应的文件名
	 * @since 0.4
	 */
	public static String getFileName(String filePath) {

		int i = getPathLastIndex(filePath);
		int len = filePath.length();
		// System.out.println(i+"  "+len);
		if (i == len - 1) {
			return "";
		}
		File file = new File(filePath);
		return file.getName();
	}

	/**
	 * 从文件名得到文件绝对路径。
	 * 
	 * @param fileName
	 *            文件名
	 * @return 对应的文件路径
	 * @since 0.4
	 */
	public static String getFilePath(String fileName) {
		File file = new File(fileName);
		return file.getAbsolutePath();
	}



	public static String getParentPath(String pathname) {
		return getFileParentPath(pathname);
	}

	/**
	 * 得到文件名中的父路径部分。 对两种路径分隔符都有效。 不存在时返回""。
	 * 如果文件名是以路径分隔符结尾的则不考虑该分隔符，例如"/path/"返回""。
	 * 
	 * @param fileName
	 *            文件名
	 * @return 父路径，不存在或者已经是父目录时返回""
	 * @since 0.5
	 */
	public static String getFileParentPath(String fileName) {
		int point = getPathLastIndex(fileName);
		int length = fileName.length();
		if (point == -1) {
			return "";
		} else if (point == length - 1) {
			int secondPoint = getPathLastIndex(fileName, point - 1);
			if (secondPoint == -1) {
				return "";
			} else {
				return fileName.substring(0, secondPoint);
			}
		} else {
			// System.out.println("point="+point);
			return fileName.substring(0, point);
		}
	}

	/**
	 * 如 c:/sun/ 返回 c:/sun
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getDirectoryPath(String fileName) {
		if (fileName == null)
			return "";
		fileName = fileName.trim();
		int point = getPathLastIndex(fileName);
		if (point != -1) {
			if (point == (fileName.length() - 1))
				return fileName.substring(0, point);

		}

		return fileName;
	}

	/**
	 * 得到路径分隔符在文件路径中首次出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
	 *
	 * @param fileName
	 *            文件路径
	 * @return 路径分隔符在路径中首次出现的位置，没有出现时返回-1。
	 * @since 0.5
	 */
	public static int getPathIndex(String fileName) {
		int point = fileName.indexOf('/');
		if (point == -1) {
			point = fileName.indexOf('\\');
		}
		return point;
	}

	/**
	 * 得到路径分隔符在文件路径中指定位置后首次出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
	 *
	 * @param fileName
	 *            文件路径
	 * @param fromIndex
	 *            开始查找的位置
	 * @return 路径分隔符在路径中指定位置后首次出现的位置，没有出现时返回-1。
	 * @since 0.5
	 */
	public static int getPathIndex(String fileName, int fromIndex) {
		int point = fileName.indexOf('/', fromIndex);
		if (point == -1) {
			point = fileName.indexOf('\\', fromIndex);
		}
		return point;
	}

	public static String getFileNameBeforeType(String filename) {
		int pos = filename.lastIndexOf(".");
		if (pos == -1)
			return "";
		return filename.substring(0, pos);
	}

	/**
	 * 得到路径分隔符在文件路径中最后出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
	 *
	 * @param fileName
	 *            文件路径
	 * @return 路径分隔符在路径中最后出现的位置，没有出现时返回-1。
	 * @since 0.5
	 */
	public static int getPathLastIndex(String fileName) {
		int point1 = fileName.lastIndexOf('/');
		int point2 = fileName.lastIndexOf('\\');
		int point = point1 > point2 ? point1 : point2;

		return point;
	}

	/**
	 * 得到路径分隔符在文件路径中指定位置前最后出现的位置。 对于DOS或者UNIX风格的分隔符都可以。
	 *
	 * @param fileName
	 *            文件路径
	 * @param fromIndex
	 *            开始查找的位置
	 * @return 路径分隔符在路径中指定位置前最后出现的位置，没有出现时返回-1。
	 * @since 0.5
	 */
	public static int getPathLastIndex(String fileName, int fromIndex) {
		int point = fileName.lastIndexOf('/', fromIndex);
		if (point == -1) {
			point = fileName.lastIndexOf('\\', fromIndex);
		}
		return point;
	}



	/**
	 * 已经关闭流的连接
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(Reader in) throws Exception {
		// return FileCopyUtils.copyToString(in);
		return IOUtils.toString(in, true);

	}

	/**
	 * 已经关闭流的连接
	 * 
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(InputStream in, String charset)
			throws Exception {

		// InputStreamReader inputReader=new InputStreamReader(in,charset);;
		// return FileUtils.readTxt(inputReader);
		return IOUtils.toString(in, charset, true);

	}

	/**
	 * 已经关闭流的连接
	 * 
	 * @param file
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(File file, String charset) throws IOException {
		return org.apache.commons.io.FileUtils.readFileToString(file, charset);
	}

	/**
	 * 读取文本文件内容
	 * 
	 * @param filePathAndName
	 *            带有完整绝对路径的文件名
	 * @param encoding
	 *            文本文件打开的编码方式
	 * @return 返回文本文件的内容
	 */
	public static String readTxt(String filePathAndName, String charset)
			throws IOException {

		File file = new File(filePathAndName);
		return FileUtils.readTxt(file, charset);
	}


}
