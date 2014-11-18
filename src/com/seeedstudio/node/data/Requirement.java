package com.seeedstudio.node.data;


public class Requirement {
	public String operator;
	public SensorData data;
	public String logic;
	
	public Requirement(String operator, SensorData data) {
		this.operator = operator;
		this.data = data;
		this.logic = "";
	}
	
	public Requirement(String operator, SensorData data, String logic) {
		this.operator = operator;
		this.data = data;
		this.logic = logic;
	}
	
	@Override
	public String toString() {
		return logic + " " + data.name + " " + operator + " " + DataCenter.floatToString(data.data);
	}
}
