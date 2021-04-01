package com.github.ulwx.aka.dbutils.database.utils;

public class Table2JavaNameUtils {
    /**
     * 将驼峰式命名的字符串转换为下划线大写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
     * 例如：HelloWorld->hello_world
     *
     * @param name 转换前的驼峰式命名的字符串
     * @return 转换后下划线大写方式命名的字符串
     */
    public static String camelToUnderLine(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            // 循环处理其余字符  
            for (int i = 0; i < name.length(); i++) {

                String s = name.charAt(i) + "";
                if (i == 0) {
                    result.append(s.toLowerCase());
                    continue;
                }
                // 在大写字母前添加下划线  
                if (s.equals(s.toUpperCase()) && Character.isUpperCase(s.charAt(0))) {
                    result.append("_");
                }
                // 其他字符直接转成大写  
                result.append(s.toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。</br>
     * 例如：HELLO_WORLD->HelloWorld  ,hello_word->HelloWorld, Hello_word->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String underLineToCamel(String name) {
        StringBuilder result = new StringBuilder();

        // 快速检查  
        if (name == null || name.isEmpty()) {
            // 没必要转换  
            return "";
        } else {
            // 用下划线将原始字符串分割
            String camels[] = name.split("_");
            for (String camel : camels) {
                // 跳过原始字符串中开头、结尾的下换线或双重下划线  
                if (camel.isEmpty()) {
                    continue;
                }
                result.append(camel.substring(0, 1).toUpperCase() + camel.substring(1));
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(underLineToCamel("$h4_4Ello$wO_rd_"));
        System.out.println(camelToUnderLine("H44El$lowO_rd"));
    }
}
