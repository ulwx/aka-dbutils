package com.github.ulwx.aka.dbutils.tool.support;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ResultSetPrinter {
    public static void printResultSet(ResultSet rs)  {
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            // 获取列数
            int ColumnCount = resultSetMetaData.getColumnCount();
            // 保存当前列最大长度的数组
            int[] columnMaxLengths = new int[ColumnCount];
            // 缓存结果集,结果集可能有序,所以用ArrayList保存变得打乱顺序.
            ArrayList<String[]> results = new ArrayList<>();
            // 按行遍历
            while (rs.next()) {
                // 保存当前行所有列
                String[] columnStr = new String[ColumnCount];
                // 获取属性值.
                for (int i = 0; i < ColumnCount; i++) {
                    // 获取一列
                    columnStr[i] = rs.getString(i + 1);
                    // 计算当前列的最大长度
                    columnMaxLengths[i] = Math.max(columnMaxLengths[i], (columnStr[i] == null) ? 0 : columnStr[i].length());
                }
                // 缓存这一行.
                results.add(columnStr);
            }
            printColumnName(resultSetMetaData, columnMaxLengths);
            printSeparator(columnMaxLengths);
            // 遍历集合输出结果
            Iterator<String[]> iterator = results.iterator();
            String[] columnStr;
            while (iterator.hasNext()) {
                columnStr = iterator.next();
                for (int i = 0; i < ColumnCount; i++) {
                    System.out.printf("|%-" + columnMaxLengths[i] + "s", columnStr[i]);
                }
                System.out.println("|");
            }
            printSeparator(columnMaxLengths);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 输出列名.
     *
     * @param resultSetMetaData 结果集的元数据对象.
     * @param columnMaxLengths  每一列最大长度的字符串的长度.
     * @throws SQLException
     */
    private static void printColumnName(ResultSetMetaData resultSetMetaData, int[] columnMaxLengths) throws SQLException {
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String colName=resultSetMetaData.getColumnName(i + 1);
            columnMaxLengths[i]=Math.max(columnMaxLengths[i],colName.length());
            System.out.printf("|%-" + columnMaxLengths[i] + "s", colName);
        }
        System.out.println("|");
    }

    /**
     * 输出分隔符.
     *
     * @param columnMaxLengths 保存结果集中每一列的最长的字符串的长度.
     */
    private static void printSeparator(int[] columnMaxLengths) {
        for (int i = 0; i < columnMaxLengths.length; i++) {
            System.out.print("+");
            for (int j = 0; j < columnMaxLengths[i]; j++) {
                System.out.print("-");
            }
        }
        System.out.println("+");
    }
}
