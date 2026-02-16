package com.apex.beans;

public class Engine {
	
	private int strokes;
	private long horsepower;
	private String enginetype;
	
	
	//Default Constructor   ---- Need to add 
	public Engine() {
		
	}
	
	//CONSTRUCTOR INJECTION
	public Engine(int strokes, long horsepower , String enginetype) {
		this.strokes = strokes;
		this.horsepower = horsepower;
		this.enginetype = enginetype;
	}
	
	
	
	//SETTER DEPENDENCY INJECTION
	public void setStrokes(int strokes) {
		this.strokes = strokes;
	}
	public void setHorsepower(long horsepower) {
		this.horsepower = horsepower;
	}
	public void setEnginetype(String enginetype) {
		this.enginetype = enginetype;
	}

	
	@Override
	public String toString() {
		return "Engine [strokes=" + strokes + ", horsepower=" + horsepower + ", enginetype=" + enginetype + "]";
	}
	
	
	
	

}
