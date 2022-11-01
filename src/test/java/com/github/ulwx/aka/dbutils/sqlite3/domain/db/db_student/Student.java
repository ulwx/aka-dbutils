package com.github.ulwx.aka.dbutils.sqlite3.domain.db.db_student;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class Student implements java.io.Serializable {

	private Integer id;/*null;len:2000000000*/
	private String name;/*null;len:20*/
	private Integer age;/*null;len:2000000000*/
	private String birthDay;/*null;len:2000000000*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true)
	public Integer getId(){
		return id;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	public void setAge(Integer age){
		this.age = age;
	}
	
	public Integer getAge(){
		return age;
	}
	public void setBirthDay(String birthDay){
		this.birthDay = birthDay;
	}
	
	public String getBirthDay(){
		return birthDay;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-166317428L;

}