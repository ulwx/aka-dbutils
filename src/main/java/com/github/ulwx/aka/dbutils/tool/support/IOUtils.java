package com.github.ulwx.aka.dbutils.tool.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class IOUtils {
	private static Logger log = LoggerFactory.getLogger(IOUtils.class);

	/**
	 * Get the contents of an InputStream as a String using the default
	 * character encoding of the platform.
	 * 
	 * @param in
	 * @return
	 */
	public static String toString(InputStream in, boolean close)
			throws Exception {
		try {
			return org.apache.commons.io.IOUtils.toString(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.error("", e);
			throw e;

		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}

	}

	/**
	 * 没有关闭流的连接
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 */
	public static String toString(InputStream in, String encoding, boolean close)
			throws Exception {
		try {

			return org.apache.commons.io.IOUtils.toString(in, encoding);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}

	}

	public static void main(String[] args) throws Exception {



	}

	/**
	 * 读固定长度的字节,如果少于给定的字节,抛出异常
	 * 
	 * @param in
	 * @param readLen
	 * @param close
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFully(InputStream in, int readLen, boolean close)
			throws IOException {

		// DataInputStream s=new DataInputStream(in);

		byte[] bs = new byte[readLen];
		// in.read(bs);
		// 读满一个数组
		int bytesRead = 0;
		try {

			// BufferedInputStream br=new BufferedInputStream(in);
			while (bytesRead < readLen) {

				int result = in.read(bs, bytesRead, readLen - bytesRead);

				if (result == -1)
					break;

				bytesRead += result;

			}
			if (bytesRead < readLen) {
				// bs = Arrays.copyOfRange(bs, 0, bytesRead);
				throw new IOException("haven't  read given" + " number["
						+ readLen + "]bytes");
			}
		} catch (IOException ex) {
			throw ex;
			// bs = new byte[0];
		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}
		return bs;
	}

	/**
	 * 读固定长度的字符,如果少于给定的字节,抛出异常
	 * 
	 * @param in
	 * @param readLen
	 * @param close
	 * @return
	 * @throws Exception
	 */
	public static char[] readFully(Reader in, int readLen, boolean close)
			throws IOException {

		char[] bs = new char[readLen];
		// in.read(bs);
		// 读满一个数组
		int bytesRead = 0;
		try {
			while (bytesRead < readLen) {

				int result = in.read(bs, bytesRead, readLen - bytesRead);

				if (result == -1)
					break;

				bytesRead += result;

			}
			if (bytesRead < readLen) {
				throw new IOException("haven't  read given" + " number["
						+ readLen + "]chars");
			}
		} catch (IOException ex) {
			throw ex;
			// bs = new char[0];
		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}
		return bs;
	}

	public static byte[] toByteArray(InputStream in, boolean close)
			throws Exception {
		try {

			return org.apache.commons.io.IOUtils.toByteArray(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}

	}

	public static byte[] toByteArray(Reader in, String charset, boolean close)
			throws Exception {
		try {
			return org.apache.commons.io.IOUtils.toByteArray(in, charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.error("", e);
			throw e;
		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}

	}

	/**
	 * 把input流拷到output流，所有的流都不会关闭
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static int copy(InputStream input, OutputStream output, boolean close)
			throws IOException {
		try {
			return org.apache.commons.io.IOUtils.copy(input, output);
		} finally {
			if (close) {
				input.close();
				output.close();
			}
		}
	}

	/**
	 * 把input流拷贝到output，encoding为转换时利用的编码，所有的流都不会关闭
	 * 
	 * @param input
	 * @param output
	 * @param encoding
	 * @throws IOException
	 */
	public static void copy(InputStream input, Writer output, String encoding,
			boolean close) throws IOException {
		try {
			org.apache.commons.io.IOUtils.copy(input, output, encoding);
		} finally {
			if (close) {
				input.close();
				output.close();
			}
		}
	}

	/**
	 * 把input流拷贝到output流，encoding为转换时的编码，所有的流都不会关闭
	 * 
	 * @param input
	 * @param output
	 * @param encoding
	 * @throws IOException
	 */
	public static void copy(Reader input, OutputStream output, String encoding,
			boolean close) throws IOException {
		try {
			org.apache.commons.io.IOUtils.copy(input, output, encoding);
		} finally {
			if (close) {
				input.close();
				output.close();
			}
		}
	}

	/**
	 * 比较两个流的内容是否相等，所有的流都不会关闭
	 * 
	 * @param input1
	 * @param input2
	 * @return
	 * @throws IOException
	 */
	public static boolean contentEquals(InputStream input1, InputStream input2,
			boolean close) throws IOException {
		try {
			return org.apache.commons.io.IOUtils.contentEquals(input1, input2);
		} finally {
			if (close) {
				input1.close();
				input2.close();
			}
		}
	}

	/**
	 * 比较两个流的内容是否相等，所有的流都不会关闭
	 * 
	 * @param input1
	 * @param input2
	 * @return
	 * @throws IOException
	 */
	public static boolean contentEquals(Reader input1, Reader input2,
			boolean close) throws IOException {
		try {
			return org.apache.commons.io.IOUtils.contentEquals(input1, input2);
		} finally {
			if (close) {
				input1.close();
				input2.close();

			}
		}
	}

	/**
	 * 跳过多少个自己，不会关闭流
	 * 
	 * @param input
	 * @param toSkip
	 * @return
	 * @throws IOException
	 */
	public static long skip(InputStream input, long toSkip, boolean close)
			throws IOException {
		try {
			return org.apache.commons.io.IOUtils.skip(input, toSkip);
		} finally {
			if (close)
				input.close();

		}
	}

	/**
	 * 跳过多少个字符，不会关闭流
	 * 
	 * @param input
	 * @param toSkip
	 * @return
	 * @throws IOException
	 */
	public static long skip(Reader input, long toSkip, boolean close)
			throws IOException {

		try {
			return org.apache.commons.io.IOUtils.skip(input, toSkip);
		} finally {
			if (close)
				input.close();
		}
	}

	public static String toString(Reader in, boolean close) throws Exception {
		try {
			return org.apache.commons.io.IOUtils.toString(in);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			try {
				if (close)
					in.close();
			} catch (IOException e) {

			}
		}

	}

	public static void close(InputStream in) throws Exception {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
				// e.printStackTrace();
			}
		}
	}

	public static void close(OutputStream out) throws Exception {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
				// e.printStackTrace();
			}
		}
	}

	public static void close(Writer out) throws Exception {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
				// e.printStackTrace();
			}
		}
	}

	public static void close(Reader in) throws Exception {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
				// e.printStackTrace();
			}
		}
	}
}
