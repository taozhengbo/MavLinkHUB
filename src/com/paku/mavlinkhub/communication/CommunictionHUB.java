package com.paku.mavlinkhub.communication;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

public class CommunictionHUB extends FragmentActivity {
	
	public static final int REQUEST_ENABLE_BT = 1;
	BluetoothAdapter mBluetoothAdapter;

	private void Init() {

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        final ArrayList<String> pairedDevicesView = new ArrayList<String>();
		
		if (mBluetoothAdapter == null || pairedDevices.size() == 0) {
			// No BT adapter or no paired devices.
		}
		else
		{
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}

		}
		

		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
			pairedDevicesView.clear();
		    for (BluetoothDevice device : pairedDevices) {

				// Add the name and address to an array adapter to show in a ListView
		    	pairedDevicesView.add(device.getName() + "\n" + device.getAddress());
		    }
		}		
		
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		Init();
	}


}


