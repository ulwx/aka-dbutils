package com.ulwx.database;

public class MDbOption {

	private String op="";
	
	/**
	 * 大于
	 */
	public void gt() {
		this.op=">";
	}
	/**
	 * 等于
	 */
	public void eq() {
		this.op="=";
	}
	/**
	 * 大于等于
	 */
	public void ge() {
		this.op=">=";
	}
	/**
	 * 小于等于
	 */
	public void le() {
		this.op="<=";
	}
	/**
	 * 小于
	 */
	public void lt() {
		this.op="<";
	}
	public MDbOption() {
		// TODO Auto-generated constructor stub
	}

}
