package com.seeedstudio.ble.node;

public class SensorEvent {
	public int    type;
	public char   operator;
	public float  value;
	
	@Override
	public String toString() {
		return type + " " + operator + " " + Float.toString(value);
	}
}
