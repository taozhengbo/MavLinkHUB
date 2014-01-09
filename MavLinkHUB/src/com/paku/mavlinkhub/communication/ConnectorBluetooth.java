package com.paku.mavlinkhub.communication;

import com.paku.mavlinkhub.AppGlobals;
import com.paku.mavlinkhub.threads.ThreadBTConnect;
import com.paku.mavlinkhub.threads.ThreadBTSocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ConnectorBluetooth extends ConnectorBufferedStream {

	private static final String TAG = "ConnectorBluetooth";

	private BluetoothAdapter mBluetoothAdapter;
	BluetoothDevice mBluetoothDevice;
	BluetoothSocket mBluetoothSocket;

	ThreadBTConnect connThread;
	ThreadBTSocket socketThread;
	Handler btConnectorMsgHandler;

	public ConnectorBluetooth() {
		super(1024);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public boolean openConnection(String address) {

		// start connection threat
		if (mBluetoothAdapter == null) {
			return false;
		}
		try {
			mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
			connThread = new ThreadBTConnect(mBluetoothAdapter, mBluetoothDevice, this);
			connThread.start();
			return true;
		}
		catch (Exception e) {
			Log.d(TAG, "ConnectBT: " + e.getMessage());
			return false;
		}

	}

	@Override
	public void startConnectorReceiver(BluetoothSocket socket) {

		mBluetoothSocket = socket;

		// could be it's already running
		// if (socketThread == null) {
		if (true) {

			btConnectorMsgHandler = new Handler(Looper.getMainLooper()) {
				public void handleMessage(Message msg) {

					switch (msg.what) {
					// Received data
					case AppGlobals.MSG_CONNECTOR_DATA_READY:
						waitForStreamLock(3);

						mConnectorStream.write((byte[]) msg.obj, 0, msg.arg1);
						/*
						 * Log.d(TAG, "#" + mConnectorStream.toString() + "[" +
						 * mConnectorStream.size() + "]:[" + msg.arg1 + "]");
						 */
						releaseStream();

						break;
					default:
						super.handleMessage(msg);
					}

				}
			};

			socketThread = new ThreadBTSocket(socket, btConnectorMsgHandler);
			socketThread.start();

		}

	}

	@Override
	public void closeConnection() {
		Log.d(TAG, "Closing connection..");

		try {
			socketThread.stopRunning();
		}
		catch (Exception e) {
			Log.d(TAG, "Exception [socketThread.cancel]: " + e.getMessage());
		}
	}

	@Override
	public boolean isConnected() {
		if (mBluetoothSocket == null)
			return false;
		else
			return mBluetoothSocket.isConnected();

	}

	@Override
	public String getPeerName() {

		return mBluetoothSocket.getRemoteDevice().getName();

	}

	@Override
	public String getPeerAddress() {
		return mBluetoothSocket.getRemoteDevice().getAddress();

	}

	public BluetoothAdapter getBluetoothAdapter() {
		return mBluetoothAdapter;
	}

	public void setBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
		this.mBluetoothAdapter = mBluetoothAdapter;
	}

}
