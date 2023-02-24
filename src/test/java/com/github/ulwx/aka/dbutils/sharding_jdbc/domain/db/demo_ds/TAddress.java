package com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class TAddress implements java.io.Serializable {

	private Long addressId;/*;len:19*/
	private String addressName;/*;len:100*/

	public void setAddressId(Long addressId){
		this.addressId = addressId;
	}
	@AkaColumn(isNullable=false)
	public Long getAddressId(){
		return addressId;
	}
	public void setAddressName(String addressName){
		this.addressName = addressName;
	}
	@AkaColumn(isNullable=false)
	public String getAddressName(){
		return addressName;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-1372019622L;

}