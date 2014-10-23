package com.seeedstudio.ble.node;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SensorListActivity extends DeviceBaseActivity {
	public String[] sensors = new String[] {"General Analog Sensor", "Temperature Sensor"};
	
	private ListView mSensorListView;
	private ArrayAdapter<String> mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_list);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Find the ListView resource. 
	    mSensorListView = (ListView) findViewById( R.id.sensor_list_view );

	    // Create and populate a List of planet names.
	      
	    ArrayList<String> sensorList = new ArrayList<String>();
	    sensorList.addAll( Arrays.asList(sensors) );
	    
	    // Create ArrayAdapter using the planet list.
	    mListAdapter = new ArrayAdapter<String>(this, R.layout.device_row, sensorList);
	    
	    mSensorListView.setAdapter(mListAdapter);
	    
	    mSensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Class<?> activity = null;
				
				if (position == 0) {
					activity = AnalogSensorActivity.class;
				} else if (position == 1) {
					activity = TemperatureSensorActivity.class;
				} else {
					activity = AnalogSensorActivity.class;
				}
				
				String command = "s " + position;
				configureDevice(command.getBytes());
				mDataCenter.setSensorId(position);
				Intent intent = new Intent(SensorListActivity.this, activity);
				startActivity(intent);
			}
	    	
	    });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
