package com.seeedstudio.ble.node;

public class SensorData {
	static String[] units = {"%", "°„C"};
	static int[]    ids = {R.drawable.percentage, R.drawable.temperature};
	public int   type;
	public float data;
	public String unit;
	public int    image;
	
	public SensorData() {
		this(0, Float.NaN);
	}
	
	public SensorData(int type) {
		this(type, Float.NaN);
	}
	
	public SensorData(int type, float data) {
		this.type = type;
		this.data = data;
		
		if (type < units.length && type >= 0) {
			unit = units[type];
		} else {
			unit = "";
		}
		
		if (type < units.length && type >= 0) {
			image = ids[type];
		} else {
			image = R.drawable.percentage;
		}
	}
	
	public String getUnit() {
		if (type < units.length && type >= 0) {
			return units[type];
		} else {
			return "";
		}
	}
	
	public int getImageId() {
		if (type < units.length && type >= 0) {
			return ids[type];
		} else {
			return R.drawable.percentage;
		}
	}
	
	public String toString() {
		return String.valueOf(data);
	}
}
