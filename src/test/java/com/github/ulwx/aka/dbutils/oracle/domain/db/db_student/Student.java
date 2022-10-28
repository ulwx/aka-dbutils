package com.github.ulwx.aka.dbutils.oracle.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDateTime;

/*********************************************
学生
***********************************************/
public class Student implements java.io.Serializable {

	private Integer id;/*学生id;len:11*/
	private String name;/*学生姓名;len:20*/
	private Integer age;/*年龄;len:11*/
	private LocalDateTime birthDay;/*出生日期;len:7*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isNullable=false)
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
	public void setBirthDay(LocalDateTime birthDay){
		this.birthDay = birthDay;
	}
	
	public LocalDateTime getBirthDay(){
		return birthDay;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =2119430059L;

}