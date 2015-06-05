package com.seeedstudio.node.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.seeedstudio.grove_node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.ble.UartService;
import com.seeedstudio.node.data.ActuatorAction;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;
import com.seeedstudio.node.data.Task;

public class RelayActivity extends DeviceBaseActivity {
	private static final String TAG = "Node Relay";
	
	private Grove mActuator;
	private Switch mSwitch;
	private boolean mSwitchIsOn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.relay);
		
		mSwitch = (Switch) findViewById(R.id.relay_switch);
		mSwitch.setChecked(false);
		
		mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    	String command = "o 0";
				
				if (isChecked) {
					command = "o 1";
				}

				configureDevice(command.getBytes());
		    }
		});
		
		mActuator = mDataCenter.getCurrentActuator();
	}
	
	@Override
	protected void onServiceStateChanged(int state) {
		if (state == UartService.STATE_CONNECTED) {
			String command = "a " + mActuator.driver;
			configureDevice(command.getBytes());
			
			command = "o 0";
			configureDevice(command.getBytes());
			mSwitchIsOn = false;
		}
	}
	
	public void addTask(View v) {
		int count = mDataCenter.taskListAdapter.getCount();
		float[] state = new float[1];
		if (mSwitch.isChecked()) {
			state[0] = 1;
		} else {
			state[0] = 0;
		}
		ActuatorAction action = new ActuatorAction("action " + count, state);
		mDataCenter.taskListAdapter.add(new Task(count, mDataCenter.requirements, action));
		
		Intent intent = new Intent(this, NodeActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grove, menu);
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
		} else if (id == R.id.action_change) {
			Intent intent = new Intent(this, GroveListActivity.class);
			intent.putExtra(DataCenter.NEXT, DataCenter.FORWARD);
			intent.putExtra(DataCenter.TYPE, DataCenter.ACTUATOR);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
