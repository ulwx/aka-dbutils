package com.github.ulwx.aka.dbutils.tool.support.type;

public class TResult<T> {
	T value;
	public TResult(){
		
	}
	public TResult(T value) {
		// TODO Auto-generated constructor stub
		this.value=value;
	}
	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
	public String toString(){
		return this.value.toString();
	}
}
