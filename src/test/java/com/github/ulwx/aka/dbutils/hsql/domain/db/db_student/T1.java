package com.github.ulwx.aka.dbutils.hsql.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class T1 implements java.io.Serializable {

	private Integer id;/*null;len:32*/
	private Integer a;/*null;len:32*/
	private String keyB;/*null;len:20*/
	private String keyC;/*null;len:30*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
	public Integer getId(){
		return id;
	}
	public void setA(Integer a){
		this.a = a;
	}
	
	public Integer getA(){
		return a;
	}
	public void setKeyB(String keyB){
		this.keyB = keyB;
	}
	
	public String getKeyB(){
		return keyB;
	}
	public void setKeyC(String keyC){
		this.keyC = keyC;
	}
	
	public String getKeyC(){
		return keyC;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =503053761L;

}