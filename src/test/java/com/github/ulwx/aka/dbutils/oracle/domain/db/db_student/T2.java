package com.github.ulwx.aka.dbutils.oracle.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class T2 implements java.io.Serializable {

	private Integer id;/*;len:11*/
	private Integer a;/*;len:11*/
	private Integer keyA;/*;len:11*/
	private Integer keyB;/*;len:11*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isNullable=false)
	public Integer getId(){
		return id;
	}
	public void setA(Integer a){
		this.a = a;
	}
	
	public Integer getA(){
		return a;
	}
	public void setKeyA(Integer keyA){
		this.keyA = keyA;
	}
	
	public Integer getKeyA(){
		return keyA;
	}
	public void setKeyB(Integer keyB){
		this.keyB = keyB;
	}
	
	public Integer getKeyB(){
		return keyB;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =438819912L;

}