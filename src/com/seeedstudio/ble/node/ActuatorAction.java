package com.seeedstudio.ble.node;

public class ActuatorAction {
	public String name;
	public float[] data;
	
	public ActuatorAction(String name, float[] data) {
		this.name = name;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
