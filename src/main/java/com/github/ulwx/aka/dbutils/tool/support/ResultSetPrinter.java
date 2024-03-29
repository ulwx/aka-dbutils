package com.github.ulwx.aka.dbutils.tool.support;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResultSetPrinter {
    public static void printResultSet(ResultSet rs) {
        printResultSet(rs, null);
    }

    public static void printResultSet(ResultSet rs, PrintWriter writer) {
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            // 获取列数
            int ColumnCount = resultSetMetaData.getColumnCount();
            List<String> columNames=fetchColumnNames(resultSetMetaData);
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
            printColumnName(columNames,columnMaxLengths, writer);
            printSeparator(columnMaxLengths, writer);
            // 遍历集合输出结果
            Iterator<String[]> iterator = results.iterator();
            String[] columnStr;
            while (iterator.hasNext()) {
                columnStr = iterator.next();
                for (int i = 0; i < ColumnCount; i++) {
                    String str = String.format("|%-" + columnMaxLengths[i] + "s", columnStr[i], columnStr[i]);
                    if (writer != null) {
                        writer.printf(str);
                    } else {
                        System.out.printf(str);
                    }
                }
                if (writer != null) {
                    writer.println("|");
                } else {
                    System.out.println("|");
                }
            }
            printSeparator(columnMaxLengths, writer);
            if (writer == null) {
                System.out.print("\n");
            } else {
                writer.print("\n");
            }
        } catch (SQLException exception) {
        }
    }
    private static List<String> fetchColumnNames(ResultSetMetaData resultSetMetaData) throws SQLException {
        ArrayList list = new ArrayList<>();
         int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            String colName = resultSetMetaData.getColumnName(i + 1);
            list.add(colName);

        }
        return list;
    }

    /**
     * 输出列名.
     *
     * @param columNames 存放的为列名.
     * @param columnMaxLengths  每一列最大长度的字符串的长度.
     * @throws SQLException
     */
    private static void printColumnName(List<String> columNames,
                                        int[] columnMaxLengths, PrintWriter writer) throws SQLException {
       // int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 0; i < columNames.size(); i++) {
            String colName = columNames.get(i);
            columnMaxLengths[i] = Math.max(columnMaxLengths[i], colName.length());
            if (writer == null) {
                System.out.printf("|%-" + columnMaxLengths[i] + "s", colName);
            } else {
                writer.printf("|%-" + columnMaxLengths[i] + "s", colName);
            }
        }
        if (writer == null) {
            System.out.println("|");
        } else {
            writer.println("|");
        }
    }

    /**
     * 输出分隔符.
     *
     * @param columnMaxLengths 保存结果集中每一列的最长的字符串的长度.
     */
    private static void printSeparator(int[] columnMaxLengths, PrintWriter writer) {
        for (int i = 0; i < columnMaxLengths.length; i++) {
            if (writer == null) {
                System.out.print("+");
            } else {
                writer.print("+");
            }
            for (int j = 0; j < columnMaxLengths[i]; j++) {
                if (writer == null) {
                    System.out.print("-");
                } else {
                    writer.print("-");
                }
            }
        }
        if (writer == null) {
            System.out.println("+");
        } else {
            writer.println("+");
        }

    }
}
