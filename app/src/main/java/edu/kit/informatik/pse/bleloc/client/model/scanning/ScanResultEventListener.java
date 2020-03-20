package edu.kit.informatik.pse.bleloc.client.model.scanning;

import android.bluetooth.BluetoothDevice;

import java.util.EventListener;

/**
 * Listener interface for interaction with a scanner, to receive the results.
 */
public interface ScanResultEventListener extends EventListener {

	/**
	 * Is beging called when a device is found.
	 *
	 * @param device
	 * 		The found device
	 */
	public void onDeviceFound(BluetoothDevice device, int rssi);

	/**
	 * Is being called when a scan is done.
	 */
	public void onScanFinished();
}
