package com.seeedstudio.ble.node;

public class SensorEvent {
	public int    type;
	public char   condition;
	public float  value;
	
	@Override
	public String toString() {
		return type + " " + condition + Float.toString(value);
	}
}
