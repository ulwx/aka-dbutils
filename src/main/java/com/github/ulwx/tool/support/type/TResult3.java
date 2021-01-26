package com.github.ulwx.tool.support.type;

public class TResult3<T1,T2,T3> extends TResult2<T1, T2> {

	T3 val3;
	
	public TResult3(T1 val1,T2 val2,T3 val3){
		super(val1,val2);
		this.val3=val3;
	}
	public T3 getThirdValue(){
		return this.val3;
	}
	
	public void setThirdValue(T3 value){
		this.val3 =value;
	}
	
	public String toString(){
		return super.toString()+":"+this.val3;
	}
}
