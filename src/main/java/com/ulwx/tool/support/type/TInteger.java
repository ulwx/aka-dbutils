package com.ulwx.tool.support.type;

public class TInteger {

	private Integer value=0;


	public TInteger(){
		
	}
	public TInteger(Integer val){
		this.value=val;
	}
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
