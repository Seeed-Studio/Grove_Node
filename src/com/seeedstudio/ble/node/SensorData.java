package com.seeedstudio.ble.node;

public class SensorData {
	static final public int UNKNOWN    = 0;
	static final public int PERCENTAGE = 1;
	static final public int CELSIUS    = 2;
	static final public int HUMIDITY   = 3;
	public int   type;
	public float data;
	public String name;
	public String unit;
	public int    image;
	
	static final String[] names = {"x", "%", "t", "h"};
	static final String[] units = {"", "%", "°„C", "%"};
	static final int[]    images = {R.drawable.percentage, R.drawable.percentage, R.drawable.temperature, R.drawable.humidity};
	
	public SensorData() {
		this(UNKNOWN, Float.NaN);
	}
	
	public SensorData(int type) {
		this(type, Float.NaN);
	}
	
	public SensorData(int type, float data) {
		this.type = type;
		this.data = data;
		
		if (type < units.length && type >= 0) {
			unit = units[type];
			image = images[type];
		} else {
			unit = units[UNKNOWN];
			image = images[UNKNOWN];;
		}
	}
	
	public String toString() {
		return String.valueOf(data);
	}
}
