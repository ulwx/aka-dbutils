package com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_student;

import com.github.ulwx.aka.dbutils.database.MdbOptions;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class StudentManyCourses extends MdbOptions implements java.io.Serializable {

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

	private static final long serialVersionUID =-1030007539L;

}