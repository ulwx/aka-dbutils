package com.github.ulwx.aka.dbutils.database.nsql;

import com.github.ulwx.aka.dbutils.database.DbContext;
import com.github.ulwx.aka.dbutils.database.DbException;
import com.github.ulwx.aka.dbutils.tool.support.FileUtils;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils;
import com.github.ulwx.aka.dbutils.tool.support.StringUtils.GroupHandler;
import com.github.ulwx.aka.dbutils.tool.support.type.TBoolean;
import com.github.ulwx.aka.dbutils.tool.support.type.TInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

/**
 * SQL语句抽象，提供基于命名参数的SQL语句功能
 */
public final class MDTemplate {

    private static Logger log = LoggerFactory.getLogger(MDTemplate.class);
    private static String javaNameReg = "[A-Za-z_$][A-Za-z_$\\d]*";
    private static String javaExpressReg = "\\=?.*?";

    private static void endMethod(StringBuilder sb, TInteger tabNum) {
        sb.append(TAB(tabNum.getValue()) + "return retString;\n");
        tabNum.setValue(tabNum.getValue() - 1);
        sb.append(TAB(tabNum.getValue()) + "}\n");
    }

    public static String TAB(int i) {
        return StringUtils.repeat("	", i);
    }

    public static void startClass(StringBuilder sb, String className, String packageName, TInteger tabNum) {
        String importPackageName = MDTemplate.class.getPackage().getName();
        sb.append("\npackage " + packageName + ";\n");
        sb.append("import java.util.Map;\n");
        sb.append("import " + importPackageName + ".NFunction;\n");
        sb.append("import " + importPackageName + ".MDTemplate;\n");
        sb.append("import " + importPackageName + ".MDMehtodOptions;\n");
        sb.append("import static " + importPackageName + ".NFunction.*;\n");
        sb.append("public class " + className + " {\n");
        tabNum.setValue(tabNum.getValue() + 1);
    }

    public static void endClass(StringBuilder sb, TInteger tabNum) {
        tabNum.setValue(tabNum.getValue() - 1);
        sb.append("}\n");

    }

    public static void startMethod(StringBuilder sb, String className, String curMethodName, TInteger tabNum) {
        sb.append(TAB(tabNum.getValue()) + "public static String " + curMethodName
                + "(Map<String, Object> args)throws Exception{\n");
        tabNum.setValue(tabNum.getValue() + 1);
        sb.append(TAB(tabNum.getValue()) + "String retString=\"\";\n");
        sb.append(TAB(tabNum.getValue()) + "MDMehtodOptions options = new MDMehtodOptions();\n");
        sb.append(TAB(tabNum.getValue()) + "options.setSource(trimRight(\"" + className + "\",2)+\".md:" +
                curMethodName + "\");\n");
    }

    public static void figureTabNumForNextLine(String s, TBoolean LeftBruce, TBoolean RightBruce) {
        Stack<Character> stack = new Stack<Character>();
        LeftBruce.setValue(false);
        RightBruce.setValue(false);
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (s.charAt(i) == '{') {
                stack.push('{');
            } else if (s.charAt(i) == '}') {
                if (!stack.isEmpty() && stack.peek() == '{') {
                    stack.pop();
                } else {
                    RightBruce.setValue(true);
                }
            }
        }
        if (!stack.isEmpty() && stack.peek() == '{') {
            LeftBruce.setValue(true);
        }

    }

    /**
     * 处理java代码的处理器
     *
     * @param sb       总构造字符串
     * @param handLine 当前需要处理的java代码行
     * @param tabNum   当前tab的位置，用于美化
     */
    public static void handerJava(StringBuilder sb, String handLine, TInteger tabNum) {
        handLine = StringUtils.trimLeadingString(handLine, "@");
        handLine = StringUtils.trimLeadingString(handLine, " ");
        handLine = StringUtils.replaceAll(handLine, "\\$\\$\\.(" + javaNameReg + ")", 1, new GroupHandler() {
            @Override
            public String handler(String groupStr) {

                return "args.get(\"" + groupStr + "\") ";
            }
        });

        handLine = StringUtils.replaceAll(handLine, "\\$\\$\\:(" + javaNameReg + ")", 1, new GroupHandler() {
            @Override
            public String handler(String groupStr) {

                return " NFunction.isNotEmpty(args.get(\"" + groupStr + "\")) ";
            }
        });
        TBoolean leftBruce = new TBoolean(false);
        TBoolean rightBruce = new TBoolean(false);

        figureTabNumForNextLine(handLine, leftBruce, rightBruce);
        if (rightBruce.getValue()) {
            tabNum.setValue(tabNum.getValue() - 1);
        }
        sb.append(TAB(tabNum.getValue()) + handLine + "\n");

        if (leftBruce.getValue()) {
            tabNum.setValue(tabNum.getValue() + 1);
        }
    }

    public static void handerString(StringBuilder sb, String handLine, TInteger tabNum, String packageName,
                                    String className) {
        // 判断handLine里是否有${}，表名是java表达式
        handLine = StringUtils.replaceAll(handLine, "\\$\\{(" + javaExpressReg + ")}", 1, new GroupHandler() {
            @Override
            public String handler(String groupStr) {
                String key = StringUtils.trim(groupStr);
                if (key.startsWith("=")) {
                    return "\"+(" + key.substring(1) + ")+\"";
                } else if (key.startsWith("&")) {
                    // ${&getDataCount2}
                    // ${&com.github.ulwx.database.test.TestDao.md:getDataCount2}
                    String tempStr = key.substring(1);
                    String[] strs = tempStr.split(":");
                    if (strs.length == 1) {

                        strs = new String[]{packageName + "." + StringUtils.trimTailString(className, "Md") + ".md",
                                strs[0]};
                    }
                    return "\"+(" + MDTemplate.class.getSimpleName() + ".getResultString(\"" + strs[0].trim() + "\", \""
                            + strs[1].trim() + "\", args)" + ")+\"";
                } else {

                    return "\"+(" + NFunction.class.getSimpleName() + ".argValue(\"" + groupStr + "\",args,\",\",options)" + ")+\"";
                }

            }
        });
        sb.append(TAB(tabNum.getValue()) + "retString=retString+\" " + handLine + "\";\n");
    }

    public static String parseFromMdFileToJavaSource(String packageName, String className, String mdContent)
            throws Exception {

        //处理注释
        String commentReg = "(?s)\\/\\*.*?\\*\\/";
        mdContent = mdContent.replaceAll(commentReg, " ");
        try (BufferedReader reader = new BufferedReader(new StringReader(mdContent))) {
            TInteger tabNum = new TInteger(0);
            StringBuilder sb = new StringBuilder();
            startClass(sb, className, packageName, tabNum);

            String tempString = "";

            String curMethodName = "";
            String preMethodName = "";
            String handLine = "";
            String preHandLine = "";
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                String tempStr = StringUtils.trim(tempString);
                if (StringUtils.isEmpty(tempStr)) {
                    continue;
                }
                preHandLine = handLine;
                handLine = StringUtils.trim(tempString);

                if (handLine.startsWith("@")) {// 表明为java代码
                    if (StringUtils.hasText(curMethodName)) {
                        handerJava(sb, handLine, tabNum);
                    }
                } else if (handLine.startsWith("===")) {// ===
                    preMethodName = curMethodName;
                    curMethodName = preHandLine;
                    if (StringUtils.hasText(curMethodName)) {
                        if (StringUtils.hasText(preMethodName)) {
                            endMethod(sb, tabNum);
                        }
                        startMethod(sb, className, curMethodName, tabNum);
                    } else {
                        throw new DbException("===上没有方法名");
                    }
                } else {// sql
                    if (StringUtils.hasText(curMethodName)) {// 已经找到了方法名称
                        String str = reader.readLine(); // 提前读下一行
                        if (str != null) {
                            str = str.trim();

                            if (str.startsWith("===")) {// 表明handLine为方法名
                                preMethodName = curMethodName;
                                curMethodName = handLine;
                                if (StringUtils.hasText(curMethodName)) {
                                    if (StringUtils.hasText(preMethodName)) {
                                        endMethod(sb, tabNum);
                                    }
                                    startMethod(sb, className, curMethodName, tabNum);
                                } else {
                                    throw new DbException("===上没有方法名");
                                }

                            } else if (str.startsWith("@")) {// handerString(sb, handLine, tabNum);

                                handerString(sb, handLine, tabNum, packageName, className);

                                if (StringUtils.hasText(curMethodName)) {
                                    handerJava(sb, str, tabNum);
                                }

                            } else {// sql
                                handerString(sb, handLine, tabNum, packageName, className);

                                if (StringUtils.hasText(str)) {
                                    handerString(sb, str, tabNum, packageName, className);
                                } else {
                                    continue;

                                }
                            }

                        } else {
                            handerString(sb, handLine, tabNum, packageName, className);
                            break;
                        }

                    }
                }

            } // while

            if (StringUtils.hasText(curMethodName)) {

                endMethod(sb, tabNum);

            }

            // 根据txt生产java，并动态编译
            endClass(sb, tabNum);

            return sb.toString();

        }

    }

    /**
     * 根据md文件生成java源代码
     *
     * @param packageName 指定包名 例如指定 com.github.ulwxbase.dao 则会去找com/ulwxbase/dao目录查找
     * @param className   指定生成的类名，例如SysRightDaoMd，会去查找packageName包路径下的SysRightDao.md文件
     * @return
     */
    public static String parseFromMdFileToJavaSource(String packageName, String className) throws Exception {

        String filePahtMd = packageName.replace(".", "/");
        filePahtMd = filePahtMd + "/" + StringUtils.trimTailString(className, "Md") + ".md";
        // 查找对应的sql语句
        if (DbContext.permitDebugLog())
            log.debug("convert to java source from  md-file-path:" + filePahtMd + " ");
        InputStream in = MDTemplate.class.getResourceAsStream("/" + filePahtMd);

        BufferedReader bufReader = null;
        String mdContent = "";
        try {

            if (in == null) {
                throw new DbException("无法根据" + filePahtMd + "获取到md文件");
            }
            bufReader = new BufferedReader(new InputStreamReader(in, "utf-8"));

            mdContent = FileUtils.readTxt(bufReader);
            if (StringUtils.hasText(mdContent)) {
                return parseFromMdFileToJavaSource(packageName, className, mdContent);
            } else {
                throw new DbException("无法根据" + packageName + "." + className + "获取到md文件");
            }

        } catch (Exception e) {
            if (e instanceof DbException) throw (DbException) e;
            throw new DbException("解析md文件为java代码失败！" + packageName + "." + className + ",md content:" + mdContent, e);
        } finally {

            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (Exception e1) {
                    log.error("", e1);
                }
            }

        }

    }

    /**
     * 获得md模板运行后的字符串
     *
     * @param mdPath     md文件的包路径全名称，例如，格式为： com.github.ulwx.database.test.SysRightDao.md
     * @param methodName 模板里的方法名，例如 getDataCount
     * @param args       Map对象用于存放参数
     * @return
     * @throws Exception
     */
    public static String getResultString(String mdPath, String methodName, Map<String, Object> args) throws Exception {

        Class<?> clazz = CompilerTask.preCompileSingle(mdPath);
        if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
            log.debug("mdPath=" + mdPath + ",methodName=" + methodName + ",args=" + ObjectUtils.toString(args) + ",class=" + clazz);
        }
        Method method = clazz.getMethod(methodName, Map.class);
        String resultStr = (String) method.invoke(null, args);
        if (log.isDebugEnabled() && DbContext.permitDebugLog()) {
            log.debug("mdmethod call and return sql=" + resultStr);
        }
        return resultStr;
    }

    public static void main(String[] args) throws Exception {

    }

}
