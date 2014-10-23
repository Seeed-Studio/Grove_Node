package com.seeedstudio.ble.node;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TemperatureSensorActivity extends DeviceBaseActivity {
	private static final String TAG = "Node Temperature Sensor";

	private TextView mDataTextView;
	private ListView mEventListView;

	private ArrayAdapter<String> mListAdapter;
	private ArrayList<String> mEventNameList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.temperature_sensor);

		mDataTextView = (TextView) findViewById(R.id.data_text_view);
		mEventListView = (ListView) findViewById(R.id.event_list_view);

		mEventNameList =  (ArrayList<String>) mDataCenter.getEventNameList().clone();

		mListAdapter = new ArrayAdapter<String>(this, R.layout.device_row,
				mEventNameList);

		mEventListView.setAdapter(mListAdapter);

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
								
								String command = "e " + event.toString(); 
								configureDevice(command.getBytes());

								mListAdapter
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

	protected void onDeviceDataReceived(byte[] data) {
		String rxString = null;
		try {
			rxString = new String(data, "UTF-8");
			Log.d(TAG, "RX: " + rxString);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
			return;
		}
		
		String[] slices = rxString.split(" ");
		if (slices[0].equals("i") && (slices.length == 3)) {
			int dimention = Integer.parseInt(slices[1]);
			double value = Float.parseFloat(slices[2]);
			value = ((int) (value * 10)) / 10.0;
			mDataTextView.setText(String.valueOf(value));
		}

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
