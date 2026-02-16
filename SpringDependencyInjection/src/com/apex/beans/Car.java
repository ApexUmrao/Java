package com.apex.beans;

public class Car {
	
	private int seater;
	private String colour;
	private Engine engine;
	
	//Default Constructor 
	public Car() {
		
	}
	
	//CONSTRUCTOR INJECTION
	public Car(int seater, String colour, Engine engine) {
		this.seater = seater;
		this.colour = colour;
		this.engine = engine;
	}
	
	//SETTER INJECTION
	public void setSeater(int seater) {
		this.seater = seater;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public void setEngine(Engine engine) {
		this.engine = engine;
	}
	
	
	public void show() {
		System.out.println(" Seater --> "+ seater);
		System.out.println(" Colour --> "+ colour);
		System.out.println(" Engine --> "+ engine);

	}
	

}
