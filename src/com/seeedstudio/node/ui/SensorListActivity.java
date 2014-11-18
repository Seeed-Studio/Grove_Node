package com.seeedstudio.node.ui;

import android.os.Bundle;
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
		
		mDataCenter = DataCenter.getInstance();
		mListAdapter = new GroveArrayAdapter(this, mDataCenter.sensorList);
		
		// Find the ListView resource. 
	    mSensorListView = (ListView) findViewById( R.id.sensor_list_view );
	    mSensorListView.setAdapter(mListAdapter);
	    
	    mSensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				String command = "s " + position;
				configureDevice(command.getBytes());
				
				Grove sensor = mListAdapter.getItem(position);
				mDataCenter.setSensor(sensor);
				
				SensorListActivity.this.finish();
			}
	    	
	    });
	}
}
