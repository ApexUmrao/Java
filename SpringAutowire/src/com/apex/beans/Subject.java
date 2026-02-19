package com.apex.beans;

import java.util.List;

public class Subject {
	
	private List<String> sub;
	
	

	public void setSub(List<String> sub) {
		this.sub = sub;
	}



	@Override
	public String toString() {
		return "Subject [sub=" + sub + "]";
	}
	
	

}
