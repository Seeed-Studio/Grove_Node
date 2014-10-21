package com.seeedstudio.ble.node;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SensorListActivity extends Activity {
	public String[] sensors = new String[] {"General Analog Sensor", "Temperature Sensor", " "};
	
	private ListView sensorListView;
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_list);
		
		// Find the ListView resource. 
	    sensorListView = (ListView) findViewById( R.id.sensorListView );

	    // Create and populate a List of planet names.
	      
	    ArrayList<String> sensorList = new ArrayList<String>();
	    sensorList.addAll( Arrays.asList(sensors) );
	    
	    // Create ArrayAdapter using the planet list.
	    listAdapter = new ArrayAdapter<String>(this, R.layout.device_row, sensorList);
	    
	    sensorListView.setAdapter(listAdapter);
	    
	    sensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion,
					long id) {
				String item = ((TextView)view).getText().toString();
				if (item.equals("General Analog Sensor")) {
					Intent intent = new Intent(SensorListActivity.this, AnalogSensorActivity.class);
					startActivity(intent);
				} else if (item.equals("Temperature Sensor")) {
					Intent intent = new Intent(SensorListActivity.this, TemperatureSensorActivity.class);
					startActivity(intent);
				}
			}
	    	
	    });
	}
}
