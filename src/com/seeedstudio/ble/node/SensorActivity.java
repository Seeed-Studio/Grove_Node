package com.seeedstudio.ble.node;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class SensorActivity extends Activity {
	static final String TAG = "Node Sensor";
	
	private SensorData[] mSensorData;
	
	private DataCenter mDataCenter;
	private ListView   mDataListView;
	private SensorDataArrayAdapter mDataListAdapter;
	
	private ListView   mEventListView;
	private ArrayAdapter<String> mEventListAdapter;
	private ArrayList<String> mEventNameList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
	    int id = getIntent().getExtras().getInt("sensor");
		
		mDataCenter = DataCenter.getInstance();
		Grove sensor = mDataCenter.getSensors()[id];
		
		setTitle(sensor.name);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.sensor);
		
		mSensorData = sensor.data;
		mDataListAdapter = new SensorDataArrayAdapter(this, mSensorData);
		mDataListView = (ListView) findViewById(R.id.data_list_view);
		mDataListView.setAdapter(mDataListAdapter);
		
		mEventNameList =  (ArrayList<String>) mDataCenter.getEventNameList().clone();
		mEventListAdapter = new ArrayAdapter<String>(this, R.layout.device_row,
				mEventNameList);
		mEventListView = (ListView) findViewById(R.id.event_list_view);
		mEventListView.setAdapter(mEventListAdapter);
	}
	
	public void addEvent(View v) {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.add_event, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(promptsView);

		final TextView conditionTextView = (TextView) promptsView
				.findViewById(R.id.event_condition_text_view);
		conditionTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String condition = conditionTextView.getText().toString();
				if (condition.equals(">")) {
					condition = "<";
				} else if (condition.equals("<")) {
					condition = "=";
				} else {
					condition = ">";
				}

				conditionTextView.setText(condition);
			}
		});

		final TextView valueTextView = (TextView) promptsView
				.findViewById(R.id.event_value_edit_text);
		// set dialog message
		alertDialogBuilder
				.setTitle("Add a event")
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// get user input and set it to result
								// edit text
								// String name =
								// nameEditText.getText().toString();
								String condition = conditionTextView.getText()
										.toString();
								String value = valueTextView.getText()
										.toString();
								String equation = "t" + condition + value;
								SensorEvent event = new SensorEvent();
								event.type = 0;
								event.condition = condition.charAt(0);
								try {
									event.value = Float.parseFloat(value);
								} catch (NumberFormatException e) {
									Log.d(TAG, "Invalid Input");
									return;
								}

								int n = mDataCenter.getEventNumber();
								mDataCenter.addEvent(equation, event);
								
//								String command = "e " + n + " " + event.toString();
//								configureDevice(command.getBytes());

								mEventListAdapter
										.add(equation);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sensor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
		} else if (id == R.id.action_done) {
			Intent intent = new Intent(this, NodeActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
