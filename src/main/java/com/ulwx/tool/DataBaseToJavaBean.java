package com.ulwx.tool;

import com.ulwx.database.sql.SqlUtils;

public class DataBaseToJavaBean {

	public static void main(String[] args) {
		////System.out.println(Path.getClassPath());
		SqlUtils.exportTables("test", "test", "c:/ok4/testa", "com.ulwx.database.transactiontest.bean.testa","utf-8",true);

	}
}