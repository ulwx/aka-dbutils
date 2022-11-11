package com.github.ulwx.aka.dbutils.tidb.domain.db.db_teacher;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class Teacher implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private String name;/*;len:30*/

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

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =2135440310L;

}