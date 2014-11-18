package com.seeedstudio.node.data;

import java.util.ArrayList;

import com.seeedstudio.node.R;
import com.seeedstudio.node.ui.ColorPixelsActivity;
import com.seeedstudio.node.ui.LedActivity;
import com.seeedstudio.node.ui.RelayActivity;
import com.seeedstudio.node.ui.TaskArrayAdapter;

public class DataCenter {
	static final public String NEXT = "NEXT";
	static final public String TYPE = "TYPE";
	static final public int BACK = 0;
	static final public int FORWARD = 1;
	static final public int SENSOR = 0;
	static final public int ACTUATOR = 1;
	
	public ArrayList<Grove> sensorList;
	public ArrayList<Grove> actuatorList;
	public ArrayList<SensorEvent> eventList;
	public ArrayList<ActuatorAction> actionList;
	public ArrayList<Task> taskList;
	public TaskArrayAdapter taskListAdapter;
	public ArrayList<String> iftttList;
	
	private Grove mCurrentSensor;
	private Grove mCurrentActuator;

	private DataCenter() {
		// Sensor List
		sensorList = new ArrayList<Grove>();

		SensorData[] percentage = { new SensorData(SensorData.PERCENTAGE) };
		sensorList.add(new Grove("Rotary Angle Sensor", 0, 0,
				R.drawable.grove_rotary_angle_sensor, percentage));
		sensorList.add(new Grove("Slide Potentiometer Sensor", 0, 0,
				R.drawable.grove_slide_potentiometer_sensor, percentage));
		sensorList.add(new Grove("Light Sensor", 0, 0,
				R.drawable.grove_light_sensor, percentage));
		sensorList.add(new Grove("Sound Sensor", 0, 0,
				R.drawable.grove_sound_sensor, percentage));

		SensorData[] temperature = { new SensorData(SensorData.CELSIUS) };
		sensorList.add(new Grove("Temperature Sensor", 0, 1,
				R.drawable.grove_temperature_sensor, temperature));

		SensorData[] temperature_humidity = {
				new SensorData(SensorData.CELSIUS),
				new SensorData(SensorData.HUMIDITY) };
		sensorList.add(new Grove("Temp&Humi Sensor", 0, 1,
				R.drawable.grove_temp_humi_sensor, temperature_humidity));
		sensorList.add(new Grove("Temperature&Humidity Sensor Pro", 0, 1,
				R.drawable.grove_temperature_humidity_sensor_pro,
				temperature_humidity));

		// Actuator List
		actuatorList = new ArrayList<Grove>();

		actuatorList.add(new Grove("Relay", 1, 0, R.drawable.grove_relay,
				RelayActivity.class));
		actuatorList.add(new Grove("LED", 1, 1, R.drawable.grove_led,
				LedActivity.class));
		actuatorList.add(new Grove("Color Pixels", 1, 1,
				R.drawable.color_pixels_strip, ColorPixelsActivity.class));

		eventList = new ArrayList<SensorEvent>();
		actionList = new ArrayList<ActuatorAction>();
		iftttList = new ArrayList<String>();
		taskList = new ArrayList<Task>();
		
		taskListAdapter = null;

		mCurrentSensor = null;
		mCurrentActuator = null;
	}

	private static class LazyHolder {
		private static final DataCenter INSTANCE = new DataCenter();
	}

	public static DataCenter getInstance() {
		return LazyHolder.INSTANCE;
	}

	public static String floatToString(float d) {
		if (d == (long) d)
			return String.format("%d", (long) d);
		else
			return String.format("%s", d);
	}
	
	public Grove getCurrentSensor() {
		return mCurrentSensor;
	}
	
	public void setSensor(Grove sensor) {
		if (mCurrentSensor != sensor) {
			if (taskListAdapter != null) {
				taskListAdapter.clear();
			}
			mCurrentSensor = sensor;
		}
	}
	
	public Grove getCurrentActuator() {
		return mCurrentActuator;
	}
	
	public void setActuator(Grove actuator) {
		if (mCurrentActuator != actuator) {
			if (taskListAdapter != null) {
				taskListAdapter.clear();
			}
			mCurrentActuator = actuator;
		}
	}
}
