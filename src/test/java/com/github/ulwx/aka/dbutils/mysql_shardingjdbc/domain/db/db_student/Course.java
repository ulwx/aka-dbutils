package com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDateTime;

/*********************************************
课程
***********************************************/
public class Course implements java.io.Serializable {

	private Long id;/*课程id;len:10*/
	private String name;/*课程名称;len:20*/
	private Integer classHours;/*学时;len:10*/
	private Long teacherId;/*对应于db_teacher数据库里的teacher表;len:10*/
	private LocalDateTime creatime;/*建立时间;len:19*/

	public void setId(Long id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
	public Long getId(){
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
	public void setTeacherId(Long teacherId){
		this.teacherId = teacherId;
	}
	
	public Long getTeacherId(){
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

	private static final long serialVersionUID =1335368081L;

}