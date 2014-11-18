package com.seeedstudio.node.data;


public class Event {
	public String operator;
	public SensorData data;
	
	public Event(String operator, SensorData data) {
		this.operator = operator;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return data.name + operator + DataCenter.floatToString(data.data);
	}
}
