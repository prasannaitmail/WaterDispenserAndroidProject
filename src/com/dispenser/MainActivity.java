package com.dispenser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dispenser.adapter.TabsPagerAdapter;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsPagerAdapter mTabAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Diagnostic", "Set" ,"Settings"};
	
	// Debugging
		private static final String TAG = "Main";
		private static final boolean D = true;
		// Message types sent from the BluetoothChatService Handler
		public static final int MESSAGE_STATE_CHANGE = 1;
		public static final int MESSAGE_READ = 2;
		public static final int MESSAGE_WRITE = 3;
		public static final int MESSAGE_DEVICE_NAME = 4;
		public static final int MESSAGE_TOAST = 5;
		// Key names received from the BluetoothChatService Handler
		public static final String DEVICE_NAME = "device_name";
		public static final String TOAST = "toast";
		// Intent request codes
		private static final int REQUEST_CONNECT_DEVICE = 1;
		private static final int REQUEST_ENABLE_BT = 2;
		
		// Layout Views
		
		private EditText mPS1,mPS2,mPS3,mFT,mFT1,mFW,valve1,valve2;

		
		private Button mSendButtonOn,mEnable,mDisable,mSet;
		private Button mSendButtonOff;
		
		// Name of the connected device
		private String mConnectedDeviceName;
		
		// String buffer for outgoing messages
		private StringBuffer mOutStringBuffer;
		// Local Bluetooth adapter
		private BluetoothAdapter mBluetoothAdapter;
		// Member object for the chat services
		static ChatService mChatService;


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if (D) {
			Log.e(TAG, "+++ ON CREATE +++");
		}

		// Set up the window layout
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		
	//	getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
	//							  R.layout.custom_title);

		// Set up the custom title
	//	mTitle = (TextView) findViewById(R.id.title_left_text);
	//	mTitle.setText(R.string.app_name);
	//	mTitle = (TextView) findViewById(R.id.title_right_text);

	
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
						   Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// Initialization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mTabAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mTabAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}
 
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (D) {
			Log.e(TAG, "++ ON START ++");
		}

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null) {
				setupChat();
			}
		}
	}
	
	private void setupChat() {
		mSet=(Button) findViewById(R.id.setBtn);
	
		Log.d(TAG, "setupChat()");
		
		// Diagnostic
		mPS1=(EditText) findViewById(R.id.ps1);
		mPS2=(EditText) findViewById(R.id.ps2);
		mPS3=(EditText) findViewById(R.id.ps3);
		
		mFT=(EditText) findViewById(R.id.ft);
		mFT1=(EditText) findViewById(R.id.ft1);
		
		mFW=(EditText) findViewById(R.id.fw);
		
		valve1=(EditText) findViewById(R.id.valve1);
		valve2=(EditText) findViewById(R.id.valve2);
		
		
		
		
		
		// Preference 
		
		

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new ChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}
	
	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D) {
			Log.e(TAG, "+ ON RESUME +");
		}

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == ChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
	}
	
	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D) {
			Log.e(TAG, "- ON PAUSE -");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null) {
			mChatService.stop();
		}
		if (D) {
			Log.e(TAG, "--- ON DESTROY ---");
		}
	}
	
	private void ensureDiscoverable() {
		if (D) {
			Log.d(TAG, "ensure discoverable");
		}
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
	
	/**
	 * Sends a message.
	 *
	 * @param message
	 *            A string of text to send.
	 */
	void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != ChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
			.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {

			// XXX !!!
			message = message + "\r\n"; // terminate for pc bluetooth spp server

			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			// mOutEditText.setText(mOutStringBuffer);
		}
	}
	
	// The Handler that gets information back from the BluetoothChatService
		private final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MESSAGE_STATE_CHANGE:
					if (D) {
						Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					}
					switch (msg.arg1) {
					case ChatService.STATE_CONNECTED:
						Toast.makeText(getBaseContext(), R.string.title_connected_to+" " +mConnectedDeviceName, Toast.LENGTH_LONG).show();
					//	mTitle.append();
					//	mConversationArrayAdapter.clear();
						break;
					case ChatService.STATE_CONNECTING:
						Toast.makeText(getBaseContext(), R.string.title_connecting, Toast.LENGTH_LONG).show();
						break;
					case ChatService.STATE_LISTEN:
					case ChatService.STATE_NONE:
						Toast.makeText(getBaseContext(), R.string.title_not_connected, Toast.LENGTH_LONG).show();
						//mTitle.setText();
						break;
					}
					break;
				case MESSAGE_WRITE:
					byte[] writeBuf = (byte[]) msg.obj;
					// construct a string from the buffer
					String writeMessage = new String(writeBuf);
				//	mConversationArrayAdapter.add("Me:  " + writeMessage);
					break;
				case MESSAGE_READ:
					byte[] readBuf = (byte[]) msg.obj;
					// construct a string from the valid bytes in the buffer
					final String readMessage = new String(readBuf, 0, readBuf.length);
//	                mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
//	                        + readMessage);
					Runnable done = new Runnable()
				    {
				        public void run()
				        {
				           	String[] a=readMessage.split("=");
				           	String[] b=a[1].split(",");
				           	if(b.length==10){
				    	//	for(int i=0;i<b.length;i++){
				    			Toast.makeText(getBaseContext(), b[0]+"\n"+b[1]+"\n"+b[3]+"\n"+b[4]+"\n"+b[5]+"\n"+b[6]+"\n"+b[7]+"\n"+b[8]+"\n"+b[9], Toast.LENGTH_SHORT).show();
				    			
				    		//	mPS1.setText(b[0]);
				    			/*mPS2.setText(b[1]);
				    			mPS3.setText(b[2]);
				    			mFW.setText(b[3]);
				    			mFT.setText(b[4]);
				    			mFT1.setText(b[5]);
				    			valve2.setText(b[6]);
				    			valve1.setText(b[7]); */
				    		
				    		}
				        }
				    };
				    done.run();
					break;
					
				case MESSAGE_DEVICE_NAME:
					// save the connected device's name
					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(),
								   "Connected to " + mConnectedDeviceName,
								   Toast.LENGTH_SHORT).show();
					break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(),
								   msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
					.show();
					break;
				}
			}	
		};

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (D) {
				Log.d(TAG, "onActivityResult " + resultCode);
			}
			switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					// Get the device MAC address
					String address = data.getExtras().getString(
										 DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// Get the BLuetoothDevice object
					BluetoothDevice device = mBluetoothAdapter
											 .getRemoteDevice(address);
					// Attempt to connect to the device
					mChatService.connect(device);
				}
				break;
			case REQUEST_ENABLE_BT:
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so set up a chat session
					setupChat();
				} else {
					// User did not enable Bluetooth or an error occured
					Log.d(TAG, "BT not enabled");
					Toast.makeText(this, R.string.bt_not_enabled_leaving,
								   Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		}

		
/*		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.setBtn:
				
				
				break;

			default:
				break;
			}
		}
*/}
