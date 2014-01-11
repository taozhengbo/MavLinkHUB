package com.paku.mavlinkhub.threads;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.paku.mavlinkhub.HUBGlobals;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ThreadSocketBluetooth extends Thread {

	private static final String TAG = "ThreadSocketBluetooth";

	private static final int bufferSize = 1024;

	private final BluetoothSocket mmSocket;
	private final BufferedInputStream mmInStream;
	private final OutputStream mmOutStream;
	private final Handler mConnectorReceiverHandler;
	private boolean running = true;

	public ThreadSocketBluetooth(BluetoothSocket socket, Handler connectorReceiverHandler) {

		mmSocket = socket;
		mConnectorReceiverHandler = connectorReceiverHandler;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [getstreams]:" + e.getMessage());
		}

		mmInStream = new BufferedInputStream(tmpIn);
		mmOutStream = tmpOut;
	}

	public void run() {
		byte[] buffer = new byte[bufferSize]; // mConnectorStream store for the
												// stream
		int bytes; // bytes received

		while (running) {
			try {
				// sleep as we are to fast for BT serial :)
				sleep(50);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {

				bytes = mmInStream.read(buffer, 0, buffer.length);

				if (bytes > 0) {
					mConnectorReceiverHandler.obtainMessage(HUBGlobals.MSG_CONNECTOR_DATA_READY, bytes, -1, buffer)
							.sendToTarget();
				}
				else
					Log.d(TAG, "** empty stream **");
			}
			catch (IOException e) {
				Log.d(TAG, "Exception [run.read.buffer]:" + e.getMessage());
				break;
			}
		}

		// if we are here it's time to close ...
		try {
			mmSocket.close();
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [mmSocket Close]:" + e.getMessage());
			e.printStackTrace();
		}

	}

	public void write(byte[] bytes) {
		try {
			mmOutStream.write(bytes);
		}
		catch (IOException e) {
			Log.d(TAG, "Exception [write]:" + e.getMessage());
		}
	}

	public void stopRunning() {
		running = false;
	}
}