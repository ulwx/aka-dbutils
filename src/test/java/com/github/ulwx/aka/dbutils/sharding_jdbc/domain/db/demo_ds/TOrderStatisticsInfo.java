package com.github.ulwx.aka.dbutils.sharding_jdbc.domain.db.demo_ds;

import com.github.ulwx.aka.dbutils.database.annotation.AkaColumn;
import com.github.ulwx.aka.dbutils.tool.support.ObjectUtils;

import java.time.LocalDate;

/*********************************************

***********************************************/
public class TOrderStatisticsInfo implements java.io.Serializable {

	private Long id;/*;len:19*/
	private Long userId;/*;len:19*/
	private LocalDate orderDate;/*;len:10*/
	private Integer orderNum;/*;len:10*/

	public void setId(Long id){
		this.id = id;
	}
	@AkaColumn(isAutoincrement=true,isNullable=false)
	public Long getId(){
		return id;
	}
	public void setUserId(Long userId){
		this.userId = userId;
	}
	@AkaColumn(isNullable=false)
	public Long getUserId(){
		return userId;
	}
	public void setOrderDate(LocalDate orderDate){
		this.orderDate = orderDate;
	}
	@AkaColumn(isNullable=false)
	public LocalDate getOrderDate(){
		return orderDate;
	}
	public void setOrderNum(Integer orderNum){
		this.orderNum = orderNum;
	}
	
	public Integer getOrderNum(){
		return orderNum;
	}

	public String toString(){
		return  ObjectUtils.toString(this);
	}

	private static final long serialVersionUID =-191124843L;

}