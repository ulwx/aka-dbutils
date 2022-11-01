package com.github.ulwx.aka.dbutils.sqlite3.domain.db.db_student;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class T2 implements java.io.Serializable {

	private Integer id;/*null;len:2000000000*/
	private Integer a;/*null;len:2000000000*/
	private Integer keyA;/*null;len:2000000000*/
	private Integer keyB;/*null;len:2000000000*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true)
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

	private static final long serialVersionUID =-725624433L;

}