package com.github.ulwx.tool.support.type;

public class TLong {
	private Long value=0l;


	public TLong(){
		
	}
	public TLong(Long val){
		this.value=val;
	}
	
	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
