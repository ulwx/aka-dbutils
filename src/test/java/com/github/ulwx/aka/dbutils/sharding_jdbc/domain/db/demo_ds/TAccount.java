package com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class TAccount implements java.io.Serializable {

	private Long accountId;/*;len:19*/
	private Integer userId;/*;len:10*/
	private String status;/*;len:50*/

	public void setAccountId(Long accountId){
		this.accountId = accountId;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
	public Long getAccountId(){
		return accountId;
	}
	public void setUserId(Integer userId){
		this.userId = userId;
	}
	@AkaColumn(isNullable=false)
	public Integer getUserId(){
		return userId;
	}
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getStatus(){
		return status;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-1391968639L;

}