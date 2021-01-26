package com.github.ulwx.tool.support.type;

public class TBoolean {

	private Boolean value;


	public TBoolean(){
		
	}
	public TBoolean(Boolean value){ 
		this.value=value;
	}
	
	public Boolean getValue() {
		return value;
	}
	public void setValue(Boolean value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
