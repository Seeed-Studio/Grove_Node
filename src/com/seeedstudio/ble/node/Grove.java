package com.seeedstudio.ble.node;

public class Grove {
	public String name;
	public int    type;
	public int    driver;
	public int    image;
	public SensorData[] data;
	
	Grove(String name, int type, int driver, int image) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.image  = image;
		
		this.data = new SensorData[1];
		this.data[0] = new SensorData(0, Float.NaN);
	}
	
	Grove(String name, int type, int driverId, int image, SensorData[] data) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.image  = image;
		this.data = data;
	}
}
