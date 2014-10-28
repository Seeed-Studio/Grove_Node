package com.seeedstudio.ble.node;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

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

	private Button mConnectButton;

	private ImageView sensorImageView;
	private ImageView nodeImageView;
	private ImageView actuatorImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.node);

		Log.v(TAG, "onCreate");

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		sensorImageView = (ImageView) findViewById(R.id.sensorImageView);
		nodeImageView = (ImageView) findViewById(R.id.nodeImageView);
		actuatorImageView = (ImageView) findViewById(R.id.actuatorImageView);

		sensorImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NodeActivity.this,
						SensorListActivity.class);
				startActivity(intent);
			}
		});

		actuatorImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NodeActivity.this,
						ActuatorListActivity.class);
				startActivity(intent);
			}
		});

		nodeImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NodeActivity.this,
						MapActivity.class);
				startActivity(intent);
			}
		});

		mConnectButton = (Button) findViewById(R.id.connectButton);
		// Handler Disconnect & Connect button
		mConnectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mBtAdapter.isEnabled()) {
					Log.i(TAG, "onClick - BT not enabled yet");
					Intent enableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				} else {

					Log.d(TAG, "Button onClick - ");
					if (mService == null) {
						Log.d(TAG, "Service is Null");
					} else {
						Log.d(TAG,
								"Service State : "
										+ mService.getConnectionState());
					}

//					if (mService == null || mService.getConnectionState() == 0) {
					if (mConnectButton.getText().toString().equals("Connect")) {

						// Connect button pressed, open DeviceListActivity
						// class, with popup windows that scan for devices

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
		});

		service_init();
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
					if (mService.getConnectionState() != 2) {
						mConnectButton.setText("Connect");
					} else {
						mConnectButton.setText("Disconnect");
					}
				}
			});

		}

		public void onServiceDisconnected(ComponentName classname) {
			Log.d(TAG, "onServiceDisconnected");
			// // mService.disconnect(mDevice);
			// mService = null;
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
						mConnectButton.setText("Disconnect");
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
						mConnectButton.setText("Connect");
						mState = UART_PROFILE_DISCONNECTED;
//						mService.close();
//						mService = null;
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

		// if (mService == null || mService.getConnectionState() != 2) {
		// mConnectButton.setText("Connect");
		// } else {
		// mConnectButton.setText("Disconnect");
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				UARTStatusChangeReceiver);

		// if (mConnected) {
		// mConnected = false;
		// if (mDevice != null) {
		// mService.disconnect();
		// }
		// }
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
