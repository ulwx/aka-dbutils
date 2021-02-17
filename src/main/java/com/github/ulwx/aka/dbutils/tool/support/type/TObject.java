package com.github.ulwx.aka.dbutils.tool.support.type;

public class TObject {

	private Object value;

	public TObject(){
		
	}
	public TObject(Object value) {
		// TODO Auto-generated constructor stub
		this.value=value;
	}
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	public String toString(){
		return this.value.toString();
	}
	
}
