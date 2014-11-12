package com.seeedstudio.ble.node;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
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
import android.widget.RadioGroup;
import android.widget.Toast;

public class DeviceBaseActivity extends Activity {
	private static final String TAG = "Node Device Base";

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
	
	protected DataCenter mDataCenter;
	
	private int mServiceState;
	
	private int mReconnectTry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDataCenter = DataCenter.getInstance();
		
		mServiceState = -1;
		mReconnectTry = 0;

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
			
			int state = mService.getConnectionState();
			if (state != mServiceState) {
				mServiceState = state;
				onServiceStateChanged(state);
			}

		}

		public void onServiceDisconnected(ComponentName classname) {
			// // mService.disconnect(mDevice);
			mService = null;
			mServiceState = -1;
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
				mReconnectTry = 0;
				
				int state = mService.getConnectionState();
				if (state != mServiceState) {
					mServiceState = state;
					onServiceStateChanged(state);
				}

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
				int state = mService.getConnectionState();
				if (state != mServiceState) {
					mServiceState = state;
					onServiceStateChanged(state);
				}
				
//				if (mReconnectTry <= 3) {
//					mReconnectTry++;
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					mService.connect();
//				} else 
				{
					runOnUiThread(new Runnable() {
						public void run() {
							Log.d(TAG, "UART_DISCONNECT_MSG");
							mState = UART_PROFILE_DISCONNECTED;
							mService.close();
							// setUiState();
	
						}
					});
				}
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
						 onDeviceDataReceived(rxValue);
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
		}
		return super.onOptionsItemSelected(item);
	}

	protected void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

	}
	
	protected boolean configureDevice(byte[] packet) {
		if (mService == null || mService.getConnectionState() != 2) {
			return false;
		}
		
		mService.writeRXCharacteristic(packet);
		return true;
	}
	
	protected void onDeviceDataReceived(byte[] data) {
		try {
			String rxString = new String(data, "UTF-8");
			Log.d(TAG, "RX: " + rxString);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.toString());
		}
	}
	
	protected void onServiceStateChanged(int state) {
		
	}

}
