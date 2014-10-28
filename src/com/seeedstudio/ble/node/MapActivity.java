package com.seeedstudio.ble.node;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class MapActivity extends DeviceBaseActivity {
	private static final String TAG = "Node Mapping";
	
	private Spinner mEventSpinner;
	private Spinner mActionSpinner;
	private ListView mListView;
	
	ArrayAdapter<String> mEventAdapter;
	ArrayAdapter<String> mActionAdapter;
	ArrayAdapter<String> mListAdapter;
	DataCenter mDataCenter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
		mDataCenter = DataCenter.getInstance();
		ArrayList<String> eventList = mDataCenter.getEventNameList();
		mEventAdapter = new ArrayAdapter<String>(this, R.layout.device_row, eventList);
		
		ArrayList<String> actionList = mDataCenter.getActionNameList();
		mActionAdapter = new ArrayAdapter<String>(this, R.layout.device_row, actionList);
		
		mEventSpinner = (Spinner) findViewById(R.id.event_spinner);
		mActionSpinner = (Spinner) findViewById(R.id.action_spinner);
		
		mEventSpinner.setAdapter(mEventAdapter);
		mActionSpinner.setAdapter(mActionAdapter);
		
		ArrayList<String> list = new ArrayList<String>();
		mListAdapter = new ArrayAdapter<String>(this, R.layout.device_row, list);
		
		mListView = (ListView) findViewById(R.id.ifttt_list_view);
		mListView.setAdapter(mListAdapter);
	}
	
	public void onAddButtonClick(View v) {
		String eventName = mEventSpinner.getSelectedItem().toString();
		String actionName = mActionSpinner.getSelectedItem().toString();
		String ifttt = "if " + eventName + " then " + actionName;
		if (0 > mListAdapter.getPosition(ifttt)) {
			mListAdapter.add(ifttt);
		}
		
		int eventIndex = mDataCenter.getEventIndex(eventName);
		int actionIndex = mDataCenter.getActionIndex(actionName);
		
		String command = "m " + eventIndex + " " + actionIndex;
		configureDevice(command.getBytes());
		
		Log.v(TAG, "Mapping: " + command);
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
