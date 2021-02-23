package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.database.sql.SqlUtils;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;

public class NFunction {

    private static Logger log = LoggerFactory.getLogger(NFunction.class);
    public static class Options {

    }
    public static boolean isNotEmpty(Object obj) {
        if (obj == null) return false;
        if (obj instanceof String) {
            if (obj.equals("")) {
                return false;
            }

        } else if (obj.getClass().isArray()) {//如果是数组
            if (Array.getLength(obj) == 0) {
                return false;
            }
        } else if (obj instanceof Collection) {
            if (((Collection<?>) obj).size() == 0) {
                return false;
            }
        }
        return true;

    }


    public static boolean isNull(Object obj) {
        if (obj == null) return true;
        return false;
    }

    public static String trim(String str, String trimStr) {
        return StringUtils.trimString(str, trimStr);
    }

    public static String ltrim(String str, String trimStr) {
        return StringUtils.trimLeadingString(str, trimStr);
    }

    public static String rtrim(String str, String trimStr) {
        return StringUtils.trimTailString(str, trimStr);
    }

    public static String argValue(String groupStr, Map<String, Object> args,
                                        String arrayToStringDelimeter,MDMehtodOptions options ) {
        String key = groupStr.trim();
        String retValue = "";
        boolean prePercentSigns = false;
        boolean lastPercentSigns = false;
        boolean isArray = false;
        boolean isList = false;
        Object val = null;
        if (key.startsWith("[Array]")) {
            isArray = true;
            key = StringUtils.trimLeadingString(key, "[Array]");
            if (!args.containsKey(key)) {
                throw new DbException("解析参数失败！[" + options.getSource() + "]"
                        + "，参数[" + key + "]在md文件里没有定义");
            }
            val = args.get(key);
            // 判断是否为数组类型
            if (val != null) {
                if (val.getClass().isArray()) {
                    //
                } else {
                    throw new DbException("解析参数失败！[" + options.getSource() + "]"
                            + "，参数[" + key + "]应该为数组");
                }
            }
        } else {
            if (key.startsWith("%")) {
                prePercentSigns = true;
                key = key.substring(1);
            }
            if (key.length() > 0 && key.endsWith("%")) {
                lastPercentSigns = true;
                key = key.substring(0, key.length() - 1);
            }
            if (!args.containsKey(key)) {
                log.error("arg map=" + ObjectUtils.toString(args));
                throw new DbException("解析参数失败！[" + options.getSource() + "]"
                        + "，参数[" + key + "]在md文件里没有定义");
            }
            val = args.get(key);
            if (val != null) {
                if (val.getClass().isArray()) {
                    isArray = true;
                } else if (val instanceof List) {// list类型
                    isList = true;
                } else if (val instanceof Set) {// Set类型
                    isList = true;
                }
            }

        }

        if (isArray || isList) {// 数组或list
            if (prePercentSigns || lastPercentSigns) {
                throw new DbException("解析参数失败！[" + options.getSource() + "]"
                        + "，参数[" + key + "]为数组或集合类型，不支持在${" + groupStr + "}含有%");
            }
            if (val == null) {

            } else {
                if (isArray) {
                    int len = Array.getLength(val);
                    for (int m = 0; m < len; m++) {
                        Object item = Array.get(val, m);
                        if (m == 0) {
                            retValue = "";
                        } else {
                            retValue = retValue + arrayToStringDelimeter;
                        }
                        if (item instanceof String) {
                            item = SqlUtils.encodeSQLStr(item.toString());
                            item = "'" + item + "'";
                        }
                        retValue = retValue + item;


                    }
                } else if (isList) {
                    List<?> listValue = null;
                    if (val instanceof Set) {
                        listValue = new ArrayList<>();
                        listValue.addAll((Set) val);
                    } else {
                        listValue = (List<?>) val;
                    }

                    for (int m = 0; m < listValue.size(); m++) {
                        Object item = listValue.get(m);
                        if (m == 0) {
                            retValue = "";
                        } else {
                            retValue = retValue + arrayToStringDelimeter;
                        }
                        if (item instanceof String) {
                            item = SqlUtils.encodeSQLStr(item.toString());
                            item = "'" + item + "'";
                        }
                        retValue = retValue + item;

                    }
                }

            }
        } else {
            if (val == null) {
                ////
            } else {
                if (val instanceof String) {
                    val =  SqlUtils.encodeSQLStr(val.toString());
                    if (prePercentSigns) {
                        val = "%" + val;
                    }
                    if (lastPercentSigns) {
                        val = val + "%";
                    }
                    val = "'" + val + "'";
                } else {
                    if (prePercentSigns || lastPercentSigns) {
                        throw new DbException("解析参数失败！[" + options.getSource() + "]"
                                + "，参数[" + key + "]不为String类型，不支持在${" + groupStr + "}含有%");
                    }
                }
            }

            retValue = val+"";
        }
        return retValue;
    }

    public static String trimRight(String s,int len){
        return s.substring(0,s.length()-len);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("abc", null);
        map.put("qq", 123);
        map.put("like", "123%");
        map.put("aa", new String[]{"123", "456", "678"});
        map.put("ints", new Integer[]{123, 456, 678});
        MDMehtodOptions options = new MDMehtodOptions();
        options.setSource("md:xxx");
        map.put(MDMehtodOptions.class.getName(),options);
        String ret = argValue("%like%", map, ",",options);
        System.out.println(ret);
    }
}
