package com.seeedstudio.node.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seeedstudio.node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;

public class ActuatorListActivity extends DeviceBaseActivity {
	private DataCenter mDataCenter;
	private ListView mListView;
	private GroveArrayAdapter mListAdapter;
	private int mNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mNext = getIntent().getExtras().getInt(DataCenter.NEXT);
		mDataCenter = DataCenter.getInstance();
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.actuator_list);

		
		mListAdapter = new GroveArrayAdapter(this, mDataCenter.actuatorList);

		// Find the ListView resource.
		mListView = (ListView) findViewById(R.id.actuator_list_view);
		mListView.setAdapter(mListAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String command = "a " + position;
				configureDevice(command.getBytes());
				
				Grove actuator = mListAdapter.getItem(position);
				mDataCenter.setActuator(actuator);
				if (mNext == DataCenter.BACK) {
					ActuatorListActivity.this.finish();
				} else {
					Intent intent = new Intent(ActuatorListActivity.this, actuator.activity);
					startActivity(intent);
				}
			}

		});
	}
}
