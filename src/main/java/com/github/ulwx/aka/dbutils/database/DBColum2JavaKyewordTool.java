package com.github.ulwx.aka.dbutils.database;

import java.util.HashSet;
import java.util.Set;

public class DBColum2JavaKyewordTool {

    private final static Set<String> javaKeywordSet = new HashSet<>();
    static {
        String[] keys = new String[]{
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
                "long", "native", "new", "package", "private", "protected", "public", "return", "strictfp",
                "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
                "try", "void", "volatile", "while"};
        for(String key :keys){
            javaKeywordSet.add(key);
        }

    }
    public static String toDbColumName(String name){
        if(name.startsWith("$")){
            String str=name.substring(1);
            if(containJavaKeword(str)){
                return str;
            }
        }
        return  name;
    }
    public static boolean containJavaKeword(String str){
        return javaKeywordSet.contains(str);
    }
    public static String toJavaPropery(String columName){
        if(containJavaKeword(columName.trim()))
            return "$"+columName;
        return columName;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
