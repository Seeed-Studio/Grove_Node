package com.seeedstudio.ble.node;

import java.util.ArrayList;
import java.util.List;

public class DataCenter {
	private ArrayList<Grove> mSensorList;
	private ArrayList<Grove> mActuatorList;
	private ArrayList<String> mEventNameList;
	private ArrayList<SensorEvent> mEventDataList;
	private ArrayList<String> mActionNameList;
	private ArrayList<float[]> mActionDataList;
	private int  mSensorId;
	private int mActuatorId;

	private DataCenter() {
		mSensorList = new ArrayList<Grove>();
		
		mSensorList.add(new Grove("Rotary Angle Sensor", 0, 0, R.drawable.grove_rotary_angle_sensor));
		mSensorList.add(new Grove("Slide Potentiometer Sensor", 0, 0, R.drawable.grove_slide_potentiometer_sensor));
		mSensorList.add(new Grove("Light Sensor", 0, 0, R.drawable.grove_light_sensor));
		mSensorList.add(new Grove("Sound Sensor", 0, 0, R.drawable.grove_sound_sensor));
		
		SensorData[] data = new SensorData[1];
		data[0] = new SensorData(1);
		mSensorList.add(new Grove("Temperature Sensor", 0, 1, R.drawable.grove_temperature_sensor, data));
			
		mActuatorList = new ArrayList<Grove>();
		
		mActuatorList.add(new Grove("Relay", 1, 0, R.drawable.grove_relay));
		mActuatorList.add(new Grove("LED", 1, 1, R.drawable.grove_led));
		mActuatorList.add(new Grove("Color Pixels", 1, 1, R.drawable.color_pixels_strip));
		
		mEventNameList = new ArrayList<String>();
		mEventDataList = new ArrayList<SensorEvent>();
		mActionNameList = new ArrayList<String>();
		mActionDataList = new ArrayList<float[]>();
		mSensorId = -1;
		mActuatorId = -1;
	}
	
	private static class LazyHolder {
		private static final DataCenter INSTANCE = new DataCenter();
	}
	
	public static DataCenter getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public Grove[] getSensors() {
		Grove[] sensors = new Grove[mSensorList.size()];
		return (Grove[]) mSensorList.toArray(sensors);
	}
	
	public Grove[] getActuators() {
		Grove[] actuators = new Grove[mActuatorList.size()];
		return (Grove[]) mActuatorList.toArray(actuators);
	}
	
	public int getSensorId() {
		return mSensorId;
	}
	
	public void setSensorId(int id) {
		if (mSensorId != id) {
			mSensorId = id;
			removeAllEvent();
		}
	}
	
	public int getActuatorId() {
		return mActuatorId;
	}
	
	public void setActuatorId(int id) {
		if (mActuatorId != id) {
			mActuatorId = id;
			removeAllAction();
		}
	}
	
	public boolean addEvent(String name, SensorEvent event) {
		int index = mEventNameList.indexOf(name);
		if (index < 0) {
			mEventNameList.add(name);
			mEventDataList.add(event);
		} else {
			mEventDataList.set(index, event);
		}
		
		return true;
	}
	
	public boolean changeEvent(String name, SensorEvent event) {
		int index = mEventNameList.indexOf(name);
		if (index < 0) {
			return false;
		}
		
		mEventDataList.set(index, event);
		return true;
	}
	
	public boolean removeEvent(String name) {
		int index = mEventNameList.indexOf(name);
		if (index < 0) {
			return false;
		}
		
		mEventNameList.remove(index);
		mEventDataList.remove(index);
		return true;
	}
	
	public boolean removeAllEvent() {
		mEventNameList.clear();
		mEventDataList.clear();
		return true;
	}
	
	public SensorEvent getEvent(String name) {
		int index = mEventNameList.indexOf(name);
		if (index < 0) {
			return null;
		}
		
		return (SensorEvent)mEventDataList.get(index);
	}
	
	public int getEventNumber() {
		return mEventNameList.size();
	}
	
	public int getEventIndex(String name) {
		return mEventNameList.indexOf(name);
	}
	
	public ArrayList<String> getEventNameList() {
		return mEventNameList;
	}
	
	
	public boolean addAction(String name, float[] params) {
		int index = mActionNameList.indexOf(name);
		if (index < 0) {
			mActionNameList.add(name);
			mActionDataList.add(params);
		} else {
			mActionDataList.set(index, params);
		}
		
		return true;
	}
	
	public boolean changeAction(String name, float[] params) {
		int index = mActionNameList.indexOf(name);
		if (index < 0) {
			return false;
		}
		
		mActionDataList.set(index, params);
		return true;
	}
	
	public boolean removeAction(String name) {
		int index = mActionNameList.indexOf(name);
		if (index < 0) {
			return false;
		}
		
		mActionNameList.remove(index);
		mActionDataList.remove(index);
		return true;
	}
	
	public boolean removeAllAction() {
		mActionNameList.clear();
		mActionDataList.clear();
		return true;
	}
	
	public float[] getAction(String name) {
		int index = mActionNameList.indexOf(name);
		if (index < 0) {
			return null;
		}
		
		return mActionDataList.get(index);
	}
	
	public int getActionNumber() {
		return mActionNameList.size();
	}
	
	public int getActionIndex(String name) {
		return mActionNameList.indexOf(name);
	}
	
	public ArrayList<String> getActionNameList() {
		return mActionNameList;
	}
	
	public ArrayList<float[]> getActionDataList() {
		return mActionDataList;
	}
}
