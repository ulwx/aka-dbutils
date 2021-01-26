package com.ulwx.tool.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class EscapeUtil {



	public static String escapeUrl(String parms, String charset) {

		try {

			return URLEncoder.encode(parms, charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}

	public static String unescapeUrl(String parms, String charset) {

		try {
			return URLDecoder.decode(parms, charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return "";
		}
	}


}
