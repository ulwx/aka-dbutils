package com.github.ulwx.aka.dbutils.mysql_shardingjdbc.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class StudentCourse implements java.io.Serializable {

	private Long id;/*;len:10*/
	private Long studentId;/*学生id;len:10*/
	private Long courseId;/*课程id;len:10*/

	public void setId(Long id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
	public Long getId(){
		return id;
	}
	public void setStudentId(Long studentId){
		this.studentId = studentId;
	}
	
	public Long getStudentId(){
		return studentId;
	}
	public void setCourseId(Long courseId){
		this.courseId = courseId;
	}
	
	public Long getCourseId(){
		return courseId;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-250404587L;

}