package com.github.ulwx.aka.dbutils.hive.domain.db.db_student;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class StudentCourse implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private Integer studentId;/*学生id;len:10*/
	private Integer courseId;/*课程id;len:10*/

	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return id;
	}
	public void setStudentId(Integer studentId){
		this.studentId = studentId;
	}
	
	public Integer getStudentId(){
		return studentId;
	}
	public void setCourseId(Integer courseId){
		this.courseId = courseId;
	}
	
	public Integer getCourseId(){
		return courseId;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-1233788459L;

}