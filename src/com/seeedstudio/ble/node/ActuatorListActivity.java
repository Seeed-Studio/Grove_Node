package com.seeedstudio.ble.node;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ActuatorListActivity extends DeviceBaseActivity {
	private DataCenter mDataCenter;
	private Grove[] mActuators;
	private ListView mListView;
	private GroveArrayAdapter mListAdapter;

	private Class<?>[] actuator_ativities = { SwitchActivity.class,
			LedActivity.class, ColorPixelsActivity.class };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.actuator_list);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mDataCenter = DataCenter.getInstance();
		mActuators = mDataCenter.getActuators();
		mListAdapter = new GroveArrayAdapter(this, mActuators);

		// Find the ListView resource.
		mListView = (ListView) findViewById(R.id.actuator_list_view);
		mListView.setAdapter(mListAdapter);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Class<?> activity = null;

				if (position < actuator_ativities.length) {
					activity = actuator_ativities[position];
				} else {
					activity = LedActivity.class;
				}

				String command = "a " + position;
				configureDevice(command.getBytes());
				mDataCenter.setActuatorId(position);
				Intent intent = new Intent(ActuatorListActivity.this, activity);
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
