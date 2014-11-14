package com.seeedstudio.ble.node;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SwitchActivity extends DeviceBaseActivity {
	private static final String TAG = "Node Relay";

	private ListView mActionListView;
	private ArrayAdapter<ActuatorAction> mListAdapter;
	private ArrayList<ActuatorAction> mActionList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.switch_layout);

		mActionList = mDataCenter.actuatorActionList;

		if (mActionList.isEmpty()) {
			float[] onData = { 0 };
			mActionList.add(new ActuatorAction("ON", onData));

			float[] offData = { 1 };
			mActionList.add(new ActuatorAction("OFF", offData));
		}

		mListAdapter = new ArrayAdapter<ActuatorAction>(this,
				R.layout.device_row, mActionList);

		// Find the ListView resource.
		mActionListView = (ListView) findViewById(R.id.action_list_view);
		mActionListView.setAdapter(mListAdapter);
		mActionListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ActuatorAction action = mListAdapter.getItem(position);

						float[] params = action.data;
						if (params != null) {
							String command = "o";
							for (int i = 0; i < params.length; i++) {
								command += " "
										+ DataCenter.floatToString(params[i]);
							}
							configureDevice(command.getBytes());

							Log.v(TAG, "Sending: " + command);
						}
					}

				});
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

	@Override
	protected void onServiceStateChanged(int state) {
		if (state == 2) {
			int actionNumber = mActionList.size();
			for (int i = 0; i < actionNumber; i++) {
				String command = "f " + i;
				float[] data = mActionList.get(i).data;
				for (int j = 0; j < data.length; j++) {
					command += " " + DataCenter.floatToString(data[j]);
				}

				configureDevice(command.getBytes());

				Log.v(TAG, "Sending: " + command);

			}
		}
	}
}
