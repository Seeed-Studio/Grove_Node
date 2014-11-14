package com.seeedstudio.node.data;


public class SensorEvent {
	public String operator;
	public SensorData data;
	
	public SensorEvent(String operator, SensorData data) {
		this.operator = operator;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return data.name + operator + DataCenter.floatToString(data.data);
	}
}
