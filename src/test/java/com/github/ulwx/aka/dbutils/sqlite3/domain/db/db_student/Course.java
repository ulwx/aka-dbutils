package com.github.ulwx.aka.dbutils.sqlite3.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class Course implements java.io.Serializable {

	private Integer id;/*null;len:2000000000*/
	private String name;/*null;len:20*/
	private Integer classHours;/*null;len:2000000000*/
	private Integer teacherId;/*null;len:2000000000*/
	private String creatime;/*null;len:2000000000*/

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
	public void setCreatime(String creatime){
		this.creatime = creatime;
	}
	
	public String getCreatime(){
		return creatime;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =573010028L;

}