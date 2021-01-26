package com.ulwx.database.transactiontest.dao.testb;


import com.ulwx.database.transactiontest.bean.testb.T1;
import com.ulwx.database.transactiontest.dao.Dao;
import com.ulwx.tool.MDbUtils;

public class T1Dao {

	public T1Dao() {
		// TODO Auto-generated constructor stub
	}
	public static T1 getOne() throws Exception {
		T1 t1=new T1();
		return MDbUtils.queryOne(Dao.testb, t1);
	}
	
	public static void updateA(int v) throws Exception {
		T1 t1=new T1();
		t1.setId(1);
		t1.setA(v);
		 MDbUtils.update(Dao.testb, t1, "id");
	}

}
