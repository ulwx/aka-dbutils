package com.github.ulwx.tool.support.type;

public class TByte {

	private Byte value;


	public TByte(){
		
	}
	public TByte(Byte v){
		this.value=v;
	}
	
	public Byte getValue() {
		return value;
	}
	public void setValue(Byte value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
