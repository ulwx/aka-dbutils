package com.github.ulwx.tool;

import com.github.ulwx.database.sql.SqlUtils;

public class DataBaseToJavaBean {

	public static void main(String[] args) {
		////System.out.println(Path.getClassPath());
		SqlUtils.exportTables("test", "test", "c:/ok4/testa", "com.github.ulwx.database.transactiontest.bean.testa","utf-8",true);

	}
}