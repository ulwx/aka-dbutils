package com.github.ulwx.aka.dbutils.database.transactiontest.bean.testb;

import com.github.ulwx.aka.dbutils.database.MdbOptions;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDateTime;

/*********************************************

***********************************************/
public class T1 extends MdbOptions implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private Integer a;/*;len:10*/
	private LocalDateTime keyB;/*;len:19*/
	private String keyC;/*;len:30*/

	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return id;
	}
	public void setA(Integer a){
		this.a = a;
	}
	public Integer getA(){
		return a;
	}
	public void setKeyB(LocalDateTime keyB){
		this.keyB = keyB;
	}
	public LocalDateTime getKeyB(){
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

	private static final long serialVersionUID =-384765865L;

}