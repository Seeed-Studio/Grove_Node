package com.seeedstudio.ble.node;

public class Grove {
	public String name;
	public int    type;
	public int    driver;
	public int    image;
	public Object[] data;
	
	Grove(String name, int type, int driver, int image) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.image  = image;
	}
	
	Grove(String name, int type, int driver, int image, Object[] data) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.image  = image;
		this.data = data;
	}
	
	
}
