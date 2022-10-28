package com.github.ulwx.aka.dbutils.oracle.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class StudentCourse implements java.io.Serializable {

	private Integer id;/*;len:11*/
	private Integer studentId;/*学生id;len:11*/
	private Integer courseId;/*课程id;len:11*/

	public void setId(Integer id){
		this.id = id;
	}
	@AkaColumn(isNullable=false)
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

	private static final long serialVersionUID =-602317113L;

}