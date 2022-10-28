package com.github.ulwx.aka.dbutils.tool.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class EscapeUtil {


    private static final String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     * @param keyword
     * @return
     */
    public static String escapeRegex(String keyword) {
        if (StringUtils.hasText(keyword)) {
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
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

    public static void main(String[] args) {

    }


}
