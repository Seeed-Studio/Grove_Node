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

public class AnalogSensorActivity extends Activity {
	private static final String TAG = "Node Analog Sensor";

	private static final int REQUEST_SELECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int UART_PROFILE_READY = 10;
	private static final int UART_PROFILE_CONNECTED = 20;
	private static final int UART_PROFILE_DISCONNECTED = 21;
	private static final int STATE_OFF = 10;

	RadioGroup mRg;
	private int mState = UART_PROFILE_DISCONNECTED;
	private UartService mService = null;
	// private BluetoothDevice mDevice = null;
	// private BluetoothAdapter mBtAdapter = null;
	private boolean mConnected = false;

	private TextView mDataTextView;
	private ListView mEventListView;

	private ArrayAdapter<String> mListAdapter;
	private ArrayList<String> mEventNameList;

	private DataCenter mDataCenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.analog_sensor);

		mDataCenter = DataCenter.getInstance();

		mDataTextView = (TextView) findViewById(R.id.data_text_view);
		mEventListView = (ListView) findViewById(R.id.event_list_view);
		
		mEventNameList = (ArrayList<String>) mDataCenter.getEventNameList().clone();

		mListAdapter = new ArrayAdapter<String>(this, R.layout.device_row,
				mEventNameList);


		mEventListView.setAdapter(mListAdapter);

		service_init();
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
								String equation = "x" + condition + value;
								SensorEvent event = new SensorEvent();
								event.type = 0;
								event.condition = condition.charAt(0);
								event.value = Float.parseFloat(value);
								mDataCenter.addEvent(equation, event);
								int n = mDataCenter.getEventNumber();

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

	// UART service connected/disconnected
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className,
				IBinder rawBinder) {
			mService = ((UartService.LocalBinder) rawBinder).getService();
			Log.d(TAG, "onServiceConnected mService= " + mService);
			if (!mService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}

		}

		public void onServiceDisconnected(ComponentName classname) {
			// // mService.disconnect(mDevice);
			mService = null;
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		// Handler events that received from UART service
		public void handleMessage(Message msg) {

		}
	};

	private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			final Intent mIntent = intent;
			// *********************//
			if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
				Log.d(TAG, "Connected to device");
				mConnected = true;

				runOnUiThread(new Runnable() {
					public void run() {
						Log.d(TAG, "UART_CONNECT_MSG");
						mState = UART_PROFILE_CONNECTED;
					}
				});
			}

			// *********************//
			if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
				Log.d(TAG, "Disconnected");
				mConnected = false;
				runOnUiThread(new Runnable() {
					public void run() {
						Log.d(TAG, "UART_DISCONNECT_MSG");
						mState = UART_PROFILE_DISCONNECTED;
						mService.close();
						// setUiState();

					}
				});
			}

			// *********************//
			if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
				mService.enableTXNotification();
			}
			// *********************//
			if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

				final byte[] rxValue = intent
						.getByteArrayExtra(UartService.EXTRA_DATA);
				runOnUiThread(new Runnable() {
					public void run() {
						try {
							String rxString = new String(rxValue, "UTF-8");
							Log.d(TAG, "RX: " + rxString);

							String[] slices = rxString.split(" ");
							if (slices[0].equals("i") && (slices.length == 3)) {
								int dimention = Integer.parseInt(slices[1]);
								double value = Float.parseFloat(slices[2]);
								value = ((int) (value * 10)) / 10.0;
								mDataTextView.setText(String.valueOf(value));
							}
						} catch (UnsupportedEncodingException e) {
							Log.e(TAG, e.toString());
						}
					}
				});
			}
			// *********************//
			if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
				showMessage("Device doesn't support UART. Disconnecting");
				mService.disconnect();
			}

		}
	};

	private void service_init() {
		Intent bindIntent = new Intent(this, UartService.class);
		bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
		return intentFilter;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");

		LocalBroadcastManager.getInstance(this).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				UARTStatusChangeReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.device, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_done) {
			Intent intent = new Intent(this, NodeActivity.class);
			startActivity(intent);
			return true;
		} else if (id == android.R.id.home) {
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

	}
}