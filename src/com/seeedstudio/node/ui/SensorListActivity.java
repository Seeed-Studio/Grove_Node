package com.seeedstudio.node.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seeedstudio.node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;

public class SensorListActivity extends DeviceBaseActivity {
	
	private DataCenter mDataCenter;
	private ListView   mSensorListView;
	private GroveArrayAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_list);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mDataCenter = DataCenter.getInstance();
		mListAdapter = new GroveArrayAdapter(this, mDataCenter.sensorList);
		
		// Find the ListView resource. 
	    mSensorListView = (ListView) findViewById( R.id.sensor_list_view );
	    mSensorListView.setAdapter(mListAdapter);
	    
	    mSensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				int sensor = position;

				String command = "s " + position;
				configureDevice(command.getBytes());
				mDataCenter.setSensorId(position);
				Intent intent = new Intent(SensorListActivity.this, SensorActivity.class);
				intent.putExtra("sensor", sensor);
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
