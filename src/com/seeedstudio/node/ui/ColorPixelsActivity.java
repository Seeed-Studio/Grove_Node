package com.seeedstudio.node.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.holocolorpicker.SVBar;
import com.seeedstudio.node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Task;

public class ColorPixelsActivity extends DeviceBaseActivity implements OnColorChangedListener{
	private static final String TAG = "Node Color Pixels";
	
	private DataCenter mDataCenter;
	private ColorPicker mColorPicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.color_pixels);
		
		SVBar svbar = (SVBar) findViewById(R.id.svbar);	
		mColorPicker = (ColorPicker) findViewById(R.id.color_picker);
		mColorPicker.addSVBar(svbar);
		mColorPicker.setOnColorChangedListener(this);
		
		mDataCenter = DataCenter.getInstance();
	}
	
	public void addTask(View v) {
		int count = mDataCenter.taskListAdapter.getCount();
		mDataCenter.taskListAdapter.add(new Task("Task" + count));
		
		Intent intent = new Intent(this, NodeActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onColorChanged(int color) {
		int blue = color & 0xFF;
		int green = (color >> 8) & 0xFF;
		int red = (color >> 16) & 0xFF;
		
		String command = "o " + red + " " + green + " " + blue;

		configureDevice(command.getBytes());
		
		Log.d(TAG, "Sending: " + command);
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
		
	}


}
