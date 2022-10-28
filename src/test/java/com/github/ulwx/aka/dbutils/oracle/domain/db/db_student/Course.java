package com.github.ulwx.aka.dbutils.oracle.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDateTime;

/*********************************************
课程
***********************************************/
public class Course implements java.io.Serializable {

	private Integer id;/*课程id;len:11*/
	private String name;/*课程名称;len:20*/
	private Integer classHours;/*学时;len:11*/
	private Integer teacherId;/*对应于db_teacher数据库里的teacher表;len:11*/
	private LocalDateTime creatime;/*建立时间;len:7*/

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
	public void setClassHours(Integer classHours){
		this.classHours = classHours;
	}
	
	public Integer getClassHours(){
		return classHours;
	}
	public void setTeacherId(Integer teacherId){
		this.teacherId = teacherId;
	}
	
	public Integer getTeacherId(){
		return teacherId;
	}
	public void setCreatime(LocalDateTime creatime){
		this.creatime = creatime;
	}
	
	public LocalDateTime getCreatime(){
		return creatime;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =1311995339L;

}