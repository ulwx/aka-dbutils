package com.github.ulwx.database;

public abstract class MdbOptions {

	private SelectOptions selectOptions=new SelectOptions();
	
	public SelectOptions selectOptions() {
		return selectOptions;
	}

}
