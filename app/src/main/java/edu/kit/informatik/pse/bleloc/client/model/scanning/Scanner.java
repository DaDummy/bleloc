package edu.kit.informatik.pse.bleloc.client.model.scanning;

import java.util.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

/**
 * Scans for bluetooth devices and returns results asynchonously.
 */
public class Scanner {
	private BluetoothAdapter bluetoothAdapter;
	private BroadcastReceiver deviceFoundReceiver, scanFinishedReceiver;
	private BluetoothAdapter.LeScanCallback leScanCallback;
	private Set<ScanResultEventListener> listeners = new HashSet<>();
	private Set<String> discoveredAddresses = new HashSet<>();
	private long leScanDuration;

	public Scanner(BluetoothAdapter bluetoothAdapter) {
		this.bluetoothAdapter = bluetoothAdapter;
		deviceFoundReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
					for (ScanResultEventListener listener : listeners) {
						listener.onDeviceFound(device, rssi);
					}
				}
			}
		};

		scanFinishedReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
					if (leScanDuration > 0) {
						bluetoothAdapter.startLeScan(leScanCallback);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								bluetoothAdapter.stopLeScan(leScanCallback);
								for (ScanResultEventListener listener : listeners) {
									listener.onScanFinished();
								}
							}
						}, leScanDuration);
					}
					else {
						for (ScanResultEventListener listener : listeners) {
							listener.onScanFinished();
						}
					}

				}
			}
		};

		leScanCallback = new BluetoothAdapter.LeScanCallback() {
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
				for (ScanResultEventListener listener : listeners) {
					if (!discoveredAddresses.contains(device.getAddress())) {
						discoveredAddresses.add(device.getAddress());
						listener.onDeviceFound(device, rssi);
					}
				}
			}
		};
	}

	/**
	 * Performs a scan for the given duration. The results are returned asynchronously via listeners.
	 *
	 * @param millisecondsDuration
	 * 		The duration to scan for
	 * @see Scanner#registerScanResultEventListener(ScanResultEventListener, Context)
	 */
	public void startScan(long millisecondsDuration) {
		leScanDuration = millisecondsDuration;
		stopScan();
		bluetoothAdapter.startDiscovery();
	}

	/**
	 * Stops scanning.<br> A running scan is aborted.<br> If no scan is active, this method has no effect.
	 */
	public void stopScan() {
		bluetoothAdapter.stopLeScan(leScanCallback);
		bluetoothAdapter.cancelDiscovery();
		discoveredAddresses.clear();
	}

	/**
	 * Registers a listener to receive scan results over.
	 *
	 * @param listener
	 * 		The listener to register
	 */
	public void registerScanResultEventListener(ScanResultEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Deregisters a listener.<br>
	 * <p>
	 * Deregistered listeners are no longer being called on incoming events.<br> To deregister, the direct object
	 * reference is required.
	 * </p>
	 *
	 * @param listener
	 * 		The listener to deregister
	 */
	public void deregisterScanResultEventListener(ScanResultEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Attach broadcast receivers to Context.
	 * @param context context to use
	 */
	public void attach(Context context) {
		context.registerReceiver(deviceFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		context.registerReceiver(scanFinishedReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
	}

	/**
	 * Detach broadcast receivers to Context.
	 * @param context used context
	 */
	public void detach(Context context) {
		context.unregisterReceiver(deviceFoundReceiver);
		context.unregisterReceiver(scanFinishedReceiver);
	}

	/**
	 * Retrieves and returns a list of all paired devices known to the client.
	 *
	 * @return A list of all paired devices
	 */
	public List<BluetoothDevice> getPairedDevices() {
		return new ArrayList<>(bluetoothAdapter.getBondedDevices());
	}

	/**
	 * Returns the leScanCallback
	 * @return BluetoothAdapter.LeScanCallback
	 */
	public BluetoothAdapter.LeScanCallback getLeScanCallback(){
		return this.leScanCallback;
	}

	/**
	 * Sets the leScanCallback
	 * @param leScanCallback
	 */
	public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback){
		this.leScanCallback = leScanCallback;
	}

	/**
	 * Returns the Receiver of found devices
	 * @return deviceFoundReceiver
	 */
	public BroadcastReceiver getDeviceFoundReceiver(){
		return this.deviceFoundReceiver;
	}

	/**
	 * Returns the Receiver of finished scan
	 * @return scanFinishedReceiver
	 */
	public BroadcastReceiver getScanFinishedReceiver(){
		return this.scanFinishedReceiver;
	}

	/**
	 * Returns the Set of discovered Addresses
	 * @return discoveredAddresses
	 */
	public  Set<String> getDiscoveredAddresses(){
		return this.discoveredAddresses;
	}

	/**
	 * Sets the scan duration
	 * @param leScanDuration
	 */
	public void setLeScanDuration(long leScanDuration){
		this.leScanDuration = leScanDuration;
	}

	/**
	 * Returns the scan duration
	 * @return leScanDuration
	 */
	public long getLeScanDuration(){
		return this.leScanDuration;
	}
}
