package com.seeedstudio.node.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.seeedstudio.node.R;
import com.seeedstudio.node.ble.DeviceBaseActivity;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;
import com.seeedstudio.node.data.Requirement;
import com.seeedstudio.node.data.SensorData;

public class SensorActivity extends DeviceBaseActivity implements
		OnItemClickListener {
	static final String TAG = "Node Sensor";

	private SensorData[] mSensorData;

	private DataCenter mDataCenter;
	private ListView mDataListView;
	private SensorDataArrayAdapter mDataListAdapter;

	private ListView mRequirementListView;
	private RequirementArrayAdapter mRequirementListAdapter;
	private ArrayList<Requirement> mRequirementList;

	private RelativeLayout mRequirementLayout;
	private ImageView mTypeImageView;
	private TextView mOperatorTextView;
	private TextView mLogicTextView;
	private EditText mValueEditText;

	private int mCurrentDataIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.sensor);
		mDataListView = (ListView) findViewById(R.id.data_list_view);
		mRequirementListView = (ListView) findViewById(R.id.requirement_list_view);

		View footer = getLayoutInflater().inflate(
				R.layout.requirement_list_footer, null);
		mRequirementListView.addFooterView(footer);

		mDataCenter = DataCenter.getInstance();
		Grove sensor = mDataCenter.getCurrentSensor();
		setTitle(sensor.name);

		mSensorData = (SensorData[]) sensor.data;
		mDataListAdapter = new SensorDataArrayAdapter(this, mSensorData);
		mDataListView.setAdapter(mDataListAdapter);

		mRequirementList = new ArrayList<Requirement>();
		mRequirementListAdapter = new RequirementArrayAdapter(this,
				mRequirementList);
		mRequirementListView.setAdapter(mRequirementListAdapter);

		// Create a ListView-specific touch listener. ListViews are given
		// special treatment because
		// by default they handle touches for their list items... i.e. they're
		// in charge of drawing
		// the pressed state (the list selector), handling list item clicks,
		// etc.
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				mRequirementListView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							if (position == mRequirementListAdapter.getCount()) {
								return;
							}
							Requirement item = mRequirementListAdapter
									.getItem(position);
							mRequirementListAdapter.remove(item);
						}
					}
				});
		mRequirementListView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		mRequirementListView.setOnScrollListener(touchListener
				.makeScrollListener());

		mRequirementListView.setOnItemClickListener(this);

		mCurrentDataIndex = 0;

		mRequirementLayout = (RelativeLayout) findViewById(R.id.add_requirement_layout);
		mLogicTextView = (TextView) findViewById(R.id.logic_text_view);
		mTypeImageView = (ImageView) findViewById(R.id.type_image_view);
		mOperatorTextView = (TextView) findViewById(R.id.operator_text_view);
		mValueEditText = (EditText) findViewById(R.id.value_edit_text);
		mTypeImageView.setImageResource(mSensorData[mCurrentDataIndex].image);

		mValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int id, KeyEvent event) {
				// if (id == EditorInfo.IME_ACTION_DONE)
				{
					String operator = mOperatorTextView.getText().toString();
					String valueString = mValueEditText.getText().toString();
					String logic = mLogicTextView.getText().toString();
					float value;
					try {
						value = Float.parseFloat(valueString);
					} catch (NumberFormatException e) {
						Log.d(TAG, "Invalid Input");
						return true;
					}
	
					SensorData data = new SensorData(
							mSensorData[mCurrentDataIndex].type, value);
	
					mValueEditText.setText("");
	
					Requirement requirement = new Requirement(operator, data, logic);
					mRequirementListAdapter.add(requirement);
	
					mRequirementLayout.setVisibility(View.INVISIBLE);
				}

				return true;
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (position == mRequirementListAdapter.getCount()) {
			if (mRequirementListAdapter.isEmpty()) {
				mLogicTextView.setText("");
			} else {
				mLogicTextView.setText("and");
			}
			
			mRequirementLayout.setVisibility(View.VISIBLE);
			mValueEditText.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mValueEditText, InputMethodManager.SHOW_IMPLICIT);
		}
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

	public void addRequirement(View v) {
		String operator = mOperatorTextView.getText().toString();
		String valueString = mValueEditText.getText().toString();
		float value;
		try {
			value = Float.parseFloat(valueString);
		} catch (NumberFormatException e) {
			Log.d(TAG, "Invalid Input");
			return;
		}

		SensorData data = new SensorData(mSensorData[mCurrentDataIndex].type,
				value);

		mValueEditText.setText("");

		Requirement requirement = new Requirement(operator, data);
		mRequirementListAdapter.add(requirement);

		mRequirementLayout.setVisibility(View.INVISIBLE);
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
	
	public void changeLogic(View v) {
		String logic = mLogicTextView.getText().toString();
		if (logic.equals(">")) {
			logic = "and";
		} else {
			logic = "or";
		}

		mLogicTextView.setText(logic);
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
			intent.putExtra(DataCenter.TYPE, DataCenter.SENSOR);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
