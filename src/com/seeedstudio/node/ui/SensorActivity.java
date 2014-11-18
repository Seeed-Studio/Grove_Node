package com.seeedstudio.node.ui;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.seeedstudio.node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;
import com.seeedstudio.node.data.SensorData;
import com.seeedstudio.node.data.SensorEvent;

public class SensorActivity extends DeviceBaseActivity {
	static final String TAG = "Node Sensor";

	private SensorData[] mSensorData;

	private DataCenter mDataCenter;
	private ListView mDataListView;
	private SensorDataArrayAdapter mDataListAdapter;

	private ListView mEventListView;
	private SensorEventArrayAdapter mEventListAdapter;
	private SensorEvent[] mSensorEvents;

	private ImageView mTypeImageView;
	private TextView mOperatorTextView;
	private EditText mValueEditText;

	private int mCurrentDataIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.sensor);
		mDataListView = (ListView) findViewById(R.id.data_list_view);
		mEventListView = (ListView) findViewById(R.id.event_list_view);

		View header = getLayoutInflater().inflate(R.layout.list_header, null);
		TextView text = (TextView) header.findViewById(R.id.header_text_view);
		text.setText("Event List");

		mEventListView.addHeaderView(header);
		
		mDataCenter = DataCenter.getInstance();
		Grove sensor = mDataCenter.getCurrentSensor();
		setTitle(sensor.name);
		
		mSensorData = (SensorData[]) sensor.data;
		mDataListAdapter = new SensorDataArrayAdapter(this, mSensorData);
		mDataListView.setAdapter(mDataListAdapter);

		mEventListAdapter = new SensorEventArrayAdapter(this,
				mDataCenter.eventList);
		mEventListView.setAdapter(mEventListAdapter);
		
		

		// Create a ListView-specific touch listener. ListViews are given
		// special treatment because
		// by default they handle touches for their list items... i.e. they're
		// in charge of drawing
		// the pressed state (the list selector), handling list item clicks,
		// etc.
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				mEventListView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							if (position == 0) {
								// Swipe the header to all items
								mEventListAdapter.clear();
								return;
							}
							SensorEvent item = mEventListAdapter
									.getItem(position - 1);
							mEventListAdapter.remove(item);
						}
					}
				});
		mEventListView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		mEventListView.setOnScrollListener(touchListener.makeScrollListener());

		mTypeImageView = (ImageView) findViewById(R.id.type_image_view);
		mOperatorTextView = (TextView) findViewById(R.id.operator_text_view);
		mValueEditText = (EditText) findViewById(R.id.value_edit_text);

		mCurrentDataIndex = 0;
		mTypeImageView.setImageResource(mSensorData[mCurrentDataIndex].image);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void next(View v) {
		Intent intent;
		
		Grove grove = mDataCenter.getCurrentActuator();
		if (grove != null) {
			intent = new Intent(this, grove.activity);
		} else {
			intent = new Intent(this, GroveListActivity.class);
			intent.putExtra(DataCenter.NEXT, DataCenter.FORWARD);
			intent.putExtra(DataCenter.TYPE, DataCenter.ACTUATOR);
		}
		
		startActivity(intent);
	}

	public void changeType(View v) {
		mCurrentDataIndex++;
		if (mCurrentDataIndex >= mSensorData.length) {
			mCurrentDataIndex = 0;
		}

		mTypeImageView.setImageResource(mSensorData[mCurrentDataIndex].image);
	}

	public void changeOperator(View v) {
		String operator = mOperatorTextView.getText().toString();
		if (operator.equals(">")) {
			operator = "<";
		} else if (operator.equals("<")) {
			operator = "=";
		} else {
			operator = ">";
		}

		mOperatorTextView.setText(operator);
	}

	public void addEvent(View v) {
		String operator = mOperatorTextView.getText().toString();
		String valueString = mValueEditText.getText().toString();
		float value;
		try {
			value = Float.parseFloat(valueString);
		} catch (NumberFormatException e) {
			Log.d(TAG, "Invalid Input");
			return;
		}

		SensorData data = new SensorData(mSensorData[mCurrentDataIndex].type, value);
		SensorEvent event = new SensorEvent(operator, data);

		mEventListAdapter.add(event);
		
		mValueEditText.setText("");
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
			float value = Float.parseFloat(slices[2]);
			float ten = 10;
			value = ((int) (value * 10)) / ten;
			if (dimention < mSensorData.length
					&& mSensorData[dimention].data != value) {
				mSensorData[dimention].data = value;
				mDataListAdapter.notifyDataSetChanged();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
		}
		return super.onOptionsItemSelected(item);
	}
}
