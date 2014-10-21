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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GroveNodeActivity extends Activity {
	private static final String TAG = "Grove - NODE";

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
	private Button mConfigureButton;
	private TextView mDataTextView;
	private TextView mBaseDataTextView;
	private TextView mOperatorTextView;
	private TextView mOutputTextView;

	private final Context context = this;

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

		setContentView(R.layout.activity_main);

		mDataTextView = (TextView) findViewById(R.id.data_text_view);
		mBaseDataTextView = (TextView) findViewById(R.id.base_data_text_view);
		mOutputTextView = (TextView) findViewById(R.id.output_text_view);

		mBaseDataTextView.setText("50");
		mBaseDataTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.input, null);

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				alertDialogBuilder.setView(promptsView);

				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.input_number_text_view);

				// set dialog message
				alertDialogBuilder
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// get user input and set it to result
										// edit text
										String text = userInput.getText()
												.toString();
										float percentage;
										try {
											percentage = Float
													.parseFloat(text);
										} catch (NumberFormatException e) {
											showMessage("Invalid input");
											return;
										}
										
										if (percentage >= 0
												&& percentage <= 100.0) {
											mBaseDataTextView.setText(Float
													.toString(percentage));
											
											int threshold = (int)(percentage * 100);
											String output = mOutputTextView.getText().toString();
											String operator = output.equals("HIGH") ? ">" : "<";
											if (mConnected) {
												byte[] packet = new byte[8];

												mService.writeRXCharacteristic((operator + " " + threshold).getBytes());
											}
										} else {
											showMessage("Out of range");
										}

									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		});

		mOutputTextView.setText("HIGH");
		mOutputTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String output = mOutputTextView.getText().toString();
				if (output.equals("HIGH")) {
					mOutputTextView.setText("LOW");
				} else {
					mOutputTextView.setText("HIGH");
				}
				
				String percentageString = mBaseDataTextView.getText().toString();
				float percentage = Float.parseFloat(percentageString);
				int threshold = (int)(percentage * 100);
				String operator = output.equals("HIGH") ? "<" : ">";
				if (mConnected) {
					mService.writeRXCharacteristic((operator + " " + threshold).getBytes());
				}
			}
		});

		mConnectButton = (Button) findViewById(R.id.connect_button);
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
					if (mConnectButton.getText().equals("Connect")) {

						// Connect button pressed, open DeviceListActivity
						// class, with popup windows that scan for devices

						Intent newIntent = new Intent(GroveNodeActivity.this,
								DeviceListActivity.class);
						startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
					} else {
						// Disconnect button pressed
						if (mDevice != null) {
							mService.disconnect();

						}
					}
				}
			}
		});

		mConfigureButton = (Button) findViewById(R.id.configure_button);
		mConfigureButton.setVisibility(View.GONE);
		mConfigureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mConnected) {
					byte[] packet = new byte[8];

					mService.writeRXCharacteristic(packet);
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
							String[] slices = rxString.split(" ");
							if (slices[0].equals("*") && (slices.length == 3)) {
								int sensorInputRaw = Integer.parseInt(slices[1]);
								float percentage = (float)sensorInputRaw / 100;
								mDataTextView.setText(String.valueOf(percentage));
							} else if ((slices.length == 2) && (slices[0].equals(">") || slices[0].equals("<"))) {
								mOutputTextView.setText(slices[0].equals(">") ? "HIGH" : "LOW");
								float threshold = Float.parseFloat(slices[1]) / 100;
								mBaseDataTextView.setText(Float.toString(threshold));
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

		LocalBroadcastManager.getInstance(this).registerReceiver(
				UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
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
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");

		if (mConnected) {
			mConnected = false;
			if (mDevice != null) {
				mService.disconnect();
			}
		}
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
				finish();
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
		} else {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.popup_title)
					.setMessage(R.string.popup_message)
					.setPositiveButton(R.string.popup_yes,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).setNegativeButton(R.string.popup_no, null)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

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

	private void showMessage(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

	}
}
