package com.ulwx.tool.support.type;

public class TShort {

	private Short value=0;


	public TShort(){
		
	}
	public TShort(Short val){
		this.value=val;
	}
	
	public Short getValue() {
		return value;
	}
	public void setValue(Short value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
