package com.github.ulwx.aka.dbutils.derby.domain.db.db_student;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class Student implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private String name;/*;len:20*/
	private Integer age;/*;len:10*/
	private LocalDate birthDay;/*;len:10*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
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
	public void setBirthDay(LocalDate birthDay){
		this.birthDay = birthDay;
	}
	
	public LocalDate getBirthDay(){
		return birthDay;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =436724715L;

}