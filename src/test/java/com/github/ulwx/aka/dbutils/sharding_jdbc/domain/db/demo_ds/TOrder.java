package com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds;
import java.util.*;
import java.sql.*;
import java.time.*;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;
import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;

/*********************************************

***********************************************/
public class TOrder implements java.io.Serializable {

	private Long orderId;/*;len:19*/
	private Integer userId;/*;len:10*/
	private Long addressId;/*;len:19*/
	private String status;/*;len:50*/

	public void setOrderId(Long orderId){
		this.orderId = orderId;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
	public Long getOrderId(){
		return orderId;
	}
	public void setUserId(Integer userId){
		this.userId = userId;
	}
	@AkaColumn(isNullable=false)
	public Integer getUserId(){
		return userId;
	}
	public void setAddressId(Long addressId){
		this.addressId = addressId;
	}
	@AkaColumn(isNullable=false)
	public Long getAddressId(){
		return addressId;
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

	private static final long serialVersionUID =1037649792L;

}