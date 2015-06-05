package com.seeedstudio.node.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.SVBar;
import com.seeedstudio.grove_node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.ble.UartService;
import com.seeedstudio.node.data.ActuatorAction;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;
import com.seeedstudio.node.data.Task;

public class ColorPixelsActivity extends DeviceBaseActivity implements OnColorChangedListener{
	private static final String TAG = "Node Color Pixels";
	
	private ColorPicker mColorPicker;
	private Grove mActuator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.color_pixels);
		
		SVBar svbar = (SVBar) findViewById(R.id.svbar);	
		mColorPicker = (ColorPicker) findViewById(R.id.color_picker);
		mColorPicker.addSVBar(svbar);
		mColorPicker.setOnColorChangedListener(this);
		
		mActuator = mDataCenter.getCurrentActuator();
	}
	
	public void addTask(View v) {
		int count = mDataCenter.taskListAdapter.getCount();
		float[] rgb = new float[3];
		int color = mColorPicker.getColor();
		rgb[2] = color & 0xFF;
		rgb[1] = (color >> 8) & 0xFF;
		rgb[0] = (color >> 16) & 0xFF;
		ActuatorAction action = new ActuatorAction("action " + count, rgb);
		mDataCenter.taskListAdapter.add(new Task(count, mDataCenter.requirements, action));
		
		Intent intent = new Intent(this, NodeActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onColorChanged(int color) {
		int blue = color & 0xFF;
		int green = (color >> 8) & 0xFF;
		int red = (color >> 16) & 0xFF;
		
		String command = "o " + red + " " + green + " " + blue + " 0";

		configureDevice(command.getBytes());
		
		Log.d(TAG, "Sending: " + command);
	}
	
	@Override
	protected void onServiceStateChanged(int state) {
		if (state == UartService.STATE_CONNECTED) {
			String command = "a " + mActuator.driver;

			configureDevice(command.getBytes());
		}
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
