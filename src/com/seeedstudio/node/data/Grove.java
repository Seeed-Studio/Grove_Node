package com.seeedstudio.node.data;

import com.seeedstudio.node.ui.SensorActivity;


public class Grove {
	public String name;
	public int    type;
	public int    driver;
	public int    image;
	public Object[] data;
	public Class<?> activity;
	
	Grove(String name, int type, int driver, int image, Class<?> activiy) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.image  = image;
		this.activity = activiy;
	}
	
	Grove(String name, int type, int driver, int image, Object[] data) {
		this.name = name;
		this.type = type;
		this.driver = driver;
		this.image  = image;
		this.data = data;
		
		this.activity = SensorActivity.class;
	}
	
	public String toString() {
		return name;
	}
}
