package com.seeedstudio.node.data;

import java.util.ArrayList;

import com.seeedstudio.node.R;

public class DataCenter {
	public ArrayList<SensorEvent>  sensorEventList;
	public ArrayList<ActuatorAction> actuatorActionList;
	
	public ArrayList<String> iftttList;
	
	private ArrayList<Grove> mSensorList;
	private ArrayList<Grove> mActuatorList;

	private int mSensorId;
	private int mActuatorId;

	private DataCenter() {
		mSensorList = new ArrayList<Grove>();

		SensorData[] percentage = { new SensorData(SensorData.PERCENTAGE) };
		mSensorList.add(new Grove("Rotary Angle Sensor", 0, 0,
				R.drawable.grove_rotary_angle_sensor, percentage));
		mSensorList.add(new Grove("Slide Potentiometer Sensor", 0, 0,
				R.drawable.grove_slide_potentiometer_sensor, percentage));
		mSensorList.add(new Grove("Light Sensor", 0, 0,
				R.drawable.grove_light_sensor, percentage));
		mSensorList.add(new Grove("Sound Sensor", 0, 0,
				R.drawable.grove_sound_sensor, percentage));

		SensorData[] temperature = { new SensorData(SensorData.CELSIUS) };
		mSensorList.add(new Grove("Temperature Sensor", 0, 1,
				R.drawable.grove_temperature_sensor, temperature));
		
		SensorData[] temperature_humidity = {
				new SensorData(SensorData.CELSIUS),
				new SensorData(SensorData.HUMIDITY) };
		mSensorList.add(new Grove("Temp&Humi Sensor", 0, 1,
				R.drawable.grove_temp_humi_sensor,
				temperature_humidity));
		mSensorList.add(new Grove("Temperature&Humidity Sensor Pro", 0, 1,
				R.drawable.grove_temperature_humidity_sensor_pro,
				temperature_humidity));

		mActuatorList = new ArrayList<Grove>();

		mActuatorList.add(new Grove("Relay", 1, 0, R.drawable.grove_relay));
		mActuatorList.add(new Grove("LED", 1, 1, R.drawable.grove_led));
		mActuatorList.add(new Grove("Color Pixels", 1, 1,
				R.drawable.color_pixels_strip));
		
		sensorEventList = new ArrayList<SensorEvent>();
		actuatorActionList = new ArrayList<ActuatorAction>();
		iftttList = new ArrayList<String>();

		mSensorId = -1;
		mActuatorId = -1;
	}

	private static class LazyHolder {
		private static final DataCenter INSTANCE = new DataCenter();
	}

	public static DataCenter getInstance() {
		return LazyHolder.INSTANCE;
	}
	
	public static String floatToString(float d) {
		if(d == (long) d)
	        return String.format("%d",(long)d);
	    else
	        return String.format("%s",d);
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
			
			sensorEventList.clear();
			iftttList.clear();
		}
	}

	public int getActuatorId() {
		return mActuatorId;
	}

	public void setActuatorId(int id) {
		if (mActuatorId != id) {
			mActuatorId = id;
			
			actuatorActionList.clear();
			iftttList.clear();
		}
	}
	
}
