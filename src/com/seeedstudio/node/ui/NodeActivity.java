package com.seeedstudio.node.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.seeedstudio.node.R;
import com.seeedstudio.node.ble.DeviceListActivity;
import com.seeedstudio.node.ble.UartService;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;
import com.seeedstudio.node.data.Task;

public class NodeActivity extends Activity {
	private static final String TAG = "Node";

	private static final int REQUEST_SELECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int UART_PROFILE_READY = 10;
	private static final int UART_PROFILE_CONNECTED = 20;
	private static final int UART_PROFILE_DISCONNECTED = 21;
	private static final int STATE_OFF = 10;

	RadioGroup mRg;
	private int mState = UART_PROFILE_DISCONNECTED;
	private UartService mService = null;
	private BluetoothDevice mDevice = null;
	private BluetoothAdapter mBtAdapter = null;
	private boolean mConnected = false;

	private DataCenter mDataCenter;

	private TaskArrayAdapter mListAdapter;
	private ListView mListView;

	private MenuItem mConnectMenuItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		service_init();

		setContentView(R.layout.main);
		mListView = (ListView) findViewById(R.id.task_list_view);

		mDataCenter = DataCenter.getInstance();

		if (mDataCenter.taskListAdapter == null) {
			mListAdapter = new TaskArrayAdapter(this, mDataCenter.taskList);
			mDataCenter.taskListAdapter = mListAdapter;
		} else {
			mListAdapter = mDataCenter.taskListAdapter;
		}

		mListView.setAdapter(mListAdapter);

		// Create a ListView-specific touch listener. ListViews are given
		// special treatment because
		// by default they handle touches for their list items... i.e. they're
		// in charge of drawing
		// the pressed state (the list selector), handling list item clicks,
		// etc.
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				mListView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							if (position != mListAdapter.getCount()) {
								mListAdapter.remove(mListAdapter
										.getItem(position));
							}
						}
						mListAdapter.notifyDataSetChanged();
					}
				});
		mListView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		mListView.setOnScrollListener(touchListener.makeScrollListener());

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	public void selectSensor(View v) {
		Intent intent = new Intent(NodeActivity.this, GroveListActivity.class);
		intent.putExtra(DataCenter.NEXT, DataCenter.BACK);
		intent.putExtra(DataCenter.TYPE, DataCenter.SENSOR);
		startActivity(intent);
	}

	public void selectActuator(View v) {
		Intent intent = new Intent(NodeActivity.this,
				GroveListActivity.class);
		intent.putExtra(DataCenter.NEXT, DataCenter.BACK);
		intent.putExtra(DataCenter.TYPE, DataCenter.ACTUATOR);
		startActivity(intent);
	}

	public void addTask(View v) {
		
		Intent intent;
		Grove grove = mDataCenter.getCurrentSensor();
		if (grove != null) {
			intent = new Intent(this, grove.activity);
		} else {
			intent = new Intent(this, GroveListActivity.class);
			intent.putExtra(DataCenter.NEXT, DataCenter.FORWARD);
			intent.putExtra(DataCenter.TYPE, DataCenter.SENSOR);
		}
		
		startActivity(intent);
	}

	public void connect() {
		if (!mBtAdapter.isEnabled()) {
			Log.i(TAG, "BT not enabled yet");
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {

			Log.d(TAG, "Action - connect");
			if (mService == null) {
				Log.d(TAG, "Service is Null");
				return;
			} else {
				Log.d(TAG, "Service State : " + mService.getConnectionState());
			}

			if (mService.getConnectionState() != UartService.STATE_CONNECTED) {
				// Start DeviceListActivity to scan for devices
				Intent newIntent = new Intent(NodeActivity.this,
						DeviceListActivity.class);
				startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
			} else {
				// Disconnect button pressed
				if (mService != null) {
					mService.disconnect();
				}

			}
		}
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

			runOnUiThread(new Runnable() {
				public void run() {

				}
			});

		}

		public void onServiceDisconnected(ComponentName classname) {
			Log.d(TAG, "onServiceDisconnected");
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
				Log.d(TAG, "Connected to: " + mDevice.getName());
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
							}
						} catch (Exception e) {
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

		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}

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
	protected void onDestroy() {
		super.onDestroy();

		Log.v(TAG, "onDestroy");

		Intent intent = new Intent(this, UartService.class);
		stopService(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case REQUEST_SELECT_DEVICE:
			// When the DeviceListActivity return, with the selected device
			// address
			if (resultCode == Activity.RESULT_OK && data != null) {
				String deviceAddress = data
						.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
				mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
						deviceAddress);

				Log.d(TAG, "... onActivityResultdevice.address==" + mDevice
						+ "mserviceValue" + mService);
				mService.connect(deviceAddress);

			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, "Bluetooth has turned on ",
						Toast.LENGTH_SHORT).show();

			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, "Problem in BT Turning ON ",
						Toast.LENGTH_SHORT).show();
				// finish();
			}
			break;
		default:
			Log.e(TAG, "wrong request code");
			break;
		}
	}

	@Override
	public void onBackPressed() {
		if (mState == UART_PROFILE_CONNECTED) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			showMessage("NODE running in background.\n             Disconnect to exit");
			Log.v(TAG, "Node is running in backgroud.");
		}
	}

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.node, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		String title;
		if (mService != null && mService.getConnectionState() == UartService.STATE_CONNECTED) {
			title = "DISCONNECT";
		} else {
			title = "CONNECT";
		}
		menu.findItem(R.id.action_connect).setTitle(title);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_connect) {
			connect();
		} else if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
