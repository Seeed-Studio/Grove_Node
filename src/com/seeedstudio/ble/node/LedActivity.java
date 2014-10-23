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

public class LedActivity extends DeviceBaseActivity {
	private static final String TAG = "Node LED";
	
	private ListView actionListView;
	private ArrayAdapter<String> listAdapter;
	private String[] actions = {"ON", "OFF"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.led);
		
		float[] onParam = new float[1];
		onParam[0] = 1;
		mDataCenter.addAction("On", onParam);
		
		float[] offParam = new float[1];
		offParam[0] = 0;
		mDataCenter.addAction("Off", offParam);
		
		float[] blinkParams = new float[2];
		blinkParams[0] = (float)0.5;
		blinkParams[1] = 1;
		mDataCenter.addAction("Blink", blinkParams);
		
		ArrayList<String> actionList = (ArrayList<String>) mDataCenter.getActionNameList().clone();
		listAdapter = new ArrayAdapter<String>(this, R.layout.device_row, actionList);

		// Find the ListView resource. 
		actionListView = (ListView) findViewById( R.id.ledActionListView );
	    actionListView.setAdapter(listAdapter);
	    actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion,
					long id) {
				String item = ((TextView)view).getText().toString();
				float[] params = mDataCenter.getAction(item);
				if (params != null) {
					String command = "o";
					for (int i = 0; i < params.length; i++) {
						command += " " + Float.toString(params[i]);
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
}
