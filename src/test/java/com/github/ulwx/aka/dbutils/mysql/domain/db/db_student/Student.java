package com.github.ulwx.aka.dbutils.mysql.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.MdbOptions;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDate;

/*********************************************
学生
***********************************************/
public class Student extends MdbOptions implements java.io.Serializable {

	private Integer id;/*学生id;len:10*/
	private String name;/*学生姓名;len:20*/
	private Integer age;/*年龄;len:10*/
	private LocalDate birthDay;/*出生日期;len:10*/

	public void setId(Integer id){
		this.id = id;
	}
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

	private static final long serialVersionUID =-1187210994L;

}