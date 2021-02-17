package com.github.ulwx.aka.dbutils.database.transactiontest.dao.testb;


import com.github.ulwx.aka.dbutils.database.transactiontest.bean.testb.T1;
import com.github.ulwx.aka.dbutils.database.transactiontest.dao.Dao;
import com.github.ulwx.aka.dbutils.tool.MD;
import com.github.ulwx.aka.dbutils.tool.MDbUtils;

public class T1Dao {

	public T1Dao() {
		// TODO Auto-generated constructor stub
	}
	public static T1 getOne() throws Exception {
		T1 t1=new T1();
		return MDbUtils.queryOneBy(Dao.testb, t1);
	}
	
	public static void updateA(int v) throws Exception {
		T1 t1=new T1();
		t1.setId(1);
		t1.setA(v);
		MDbUtils.updateBy(Dao.testb, t1, MD.of(t1::getId));
	}


}
