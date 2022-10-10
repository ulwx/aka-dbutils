package com.github.ulwx.aka.dbutils.sqlserver.domain.db.db_teacher;

import com.github.ulwx.aka.dbutils.database.MdbOptions;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

/*********************************************

***********************************************/
public class Teacher extends MdbOptions implements java.io.Serializable {

	private Integer id;/*;len:10*/
	private String name;/*;len:30*/

	public void setId(Integer id){
		this.id = id;
	}
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

	private static final long serialVersionUID =517918446L;

}