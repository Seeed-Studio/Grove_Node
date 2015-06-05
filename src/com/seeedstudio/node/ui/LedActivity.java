package com.seeedstudio.node.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.seeedstudio.grove_node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.ble.UartService;
import com.seeedstudio.node.data.ActuatorAction;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;
import com.seeedstudio.node.data.Task;

public class LedActivity extends DeviceBaseActivity {
	private static final String TAG = "Node LED";
	private Grove mActuator;
	private ImageView mImageView;
	private boolean mLightIsOn = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.led);

		mActuator = mDataCenter.getCurrentActuator();
		
		mImageView = (ImageView) findViewById(R.id.led_image_view);
		mImageView.setImageResource(R.drawable.lightbulb_on);
	}
	
	public void addTask(View v) {
		int count = mDataCenter.taskListAdapter.getCount();
		float[] status = new float[1];
		ActuatorAction action = new ActuatorAction("action " + count, status);
		mDataCenter.taskListAdapter.add(new Task(count, mDataCenter.requirements, action));
		
		Intent intent = new Intent(this, NodeActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onServiceStateChanged(int state) {
		if (state == UartService.STATE_CONNECTED) {
			String command = "a " + mActuator.driver;
			configureDevice(command.getBytes());
			
			command = "o 1";
			configureDevice(command.getBytes());
			mLightIsOn = true;
		}
	}
	
	public void changeState(View v) {
		int id = R.drawable.lightbulb_on;
		String command = "o 1";
		
		if (mLightIsOn) {
			mLightIsOn = false;
			id = R.drawable.lightbulb;
			command = "o 0";
		} else {
			mLightIsOn = true;
		}
		mImageView.setImageResource(id);
		configureDevice(command.getBytes());
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
