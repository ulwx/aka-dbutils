package com.ulwx.tool.support.type;

public class TDouble {

	private Double value=0d;


	public TDouble(){
		
	}
	public TDouble(Double val){
		this.value=val;
	}
	
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
