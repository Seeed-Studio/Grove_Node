package com.seeedstudio.ble.node;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AnalogSensorActivity extends DeviceBaseActivity {
	private static final String TAG = "Node Analog Sensor";

	private TextView mDataTextView;
	private ListView mEventListView;

	private ArrayAdapter<String> mListAdapter;
	private ArrayList<String> mEventNameList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.analog_sensor);

		mDataTextView = (TextView) findViewById(R.id.data_text_view);
		mEventListView = (ListView) findViewById(R.id.event_list_view);

		mEventNameList = (ArrayList<String>) mDataCenter.getEventNameList()
				.clone();

		mListAdapter = new ArrayAdapter<String>(this, R.layout.device_row,
				mEventNameList);

		mEventListView.setAdapter(mListAdapter);
	}

	public void addEvent(View v) {
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.add_event, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder.setView(promptsView);

		final TextView typeTextView = (TextView) promptsView
				.findViewById(R.id.event_type_text_view);

		final TextView conditionTextView = (TextView) promptsView
				.findViewById(R.id.event_condition_text_view);

		typeTextView.setText("x");

		conditionTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String operator = conditionTextView.getText().toString();
				if (operator.equals(">")) {
					operator = "<";
				} else if (operator.equals("<")) {
					operator = "=";
				} else {
					operator = ">";
				}

				conditionTextView.setText(operator);
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
								String operator = conditionTextView.getText()
										.toString();
								String value = valueTextView.getText()
										.toString();
								String equation = "x" + operator + value;
								SensorEvent event = new SensorEvent();
								event.type = 0;
								event.operator = operator.charAt(0);
								try {
									event.value = Float.parseFloat(value);
								} catch (NumberFormatException e) {
									Log.d(TAG, "Invalid Input");
									return;
								}

								int n = mDataCenter.getEventNumber();
								mDataCenter.addEvent(equation, event);

								String command = "e " + n + " " + event.toString();
								configureDevice(command.getBytes());
								
								Log.v(TAG, "configure :" + command);

								mListAdapter.add(equation);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
