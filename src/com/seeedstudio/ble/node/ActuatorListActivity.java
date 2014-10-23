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

public class ActuatorListActivity extends DeviceBaseActivity {
	public String[] actuators = new String[] { "LED", "Chainable LED" };

	private ListView actuatorListView;
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.actuator_list);

		// Find the ListView resource.
		actuatorListView = (ListView) findViewById(R.id.actuatorListView);

		// Create and populate a List of planet names.

		ArrayList<String> actuatorList = new ArrayList<String>();
		actuatorList.addAll(Arrays.asList(actuators));

		// Create ArrayAdapter using the planet list.
		listAdapter = new ArrayAdapter<String>(this, R.layout.device_row,
				actuatorList);

		actuatorListView.setAdapter(listAdapter);

		actuatorListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Class<?> activity = null;

						if (position == 0) {
							activity = LedActivity.class;
						} else if (position == 1) {
							activity = LedActivity.class;
						} else {
							activity = LedActivity.class;
						}

						String command = "a " + position;
						configureDevice(command.getBytes());
						mDataCenter.setActuatorId(position);
						Intent intent = new Intent(ActuatorListActivity.this,
								activity);
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
