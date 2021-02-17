package com.github.ulwx.aka.dbutils.tool.support.type;

public class TChar {

	private Character value;


	public TChar(){
		
	}
	public TChar(Character val){
		this.value=val;
	}
	
	public Character getValue() {
		return value;
	}
	public void setValue(Character value) {
		this.value = value;
	}
	public String toString(){
		return value+"";
	}
}
