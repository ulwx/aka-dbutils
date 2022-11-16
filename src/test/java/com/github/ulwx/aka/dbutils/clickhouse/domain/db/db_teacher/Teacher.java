package com.github.ulwx.aka.dbutils.clickhouse.domain.db.db_teacher;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class Teacher implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private String name;/*;len:0*/

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

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-1862737679L;

}