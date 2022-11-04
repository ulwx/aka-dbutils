package com.github.ulwx.aka.dbutils.H2.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDateTime;

/*********************************************

***********************************************/
public class Course implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private String name;/*;len:20*/
	private Integer classHours;/*;len:10*/
	private Integer teacherId;/*;len:10*/
	private LocalDateTime creatime;/*;len:29*/

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

	private static final long serialVersionUID =1796151755L;

}