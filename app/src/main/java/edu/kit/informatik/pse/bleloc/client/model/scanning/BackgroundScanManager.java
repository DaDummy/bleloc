package edu.kit.informatik.pse.bleloc.client.model.scanning;

import android.bluetooth.BluetoothDevice;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.listeners.BloomFilterDownloadResultListener;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.BloomFilterDownloadRequest;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.devicehashtable.DeviceHashTableStore;
import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import edu.kit.informatik.pse.bleloc.client.model.utils.Encryptor;
import edu.kit.informatik.pse.bleloc.client.model.utils.PeriodicExecutor;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;

import java.util.ArrayList;
import java.util.Date;

/**
 * Manages a Scanner for background scanning.
 */
public class BackgroundScanManager implements Runnable, ScanResultEventListener {

	protected static class LocationProxy implements LocationListener {
		interface LocationResultReceiver {
			void gotLocation(Location location);
		}

		private LocationManager locationManager;
		private boolean queryPending;
		private ArrayList<LocationResultReceiver> arrays;

		static private Criteria criteria = new Criteria();
		static {
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setPowerRequirement(Criteria.POWER_HIGH);
		}

		LocationProxy(LocationManager locationManager) {
			this.arrays = new ArrayList<>();
			this.locationManager = locationManager;
		}

		void query(LocationResultReceiver r) {
			arrays.add(r);
			if (!queryPending) {
				locationManager.requestSingleUpdate(criteria, this, null);
				queryPending = true;
			}
		}

		@Override
		public void onLocationChanged(Location location) {
			for (LocationResultReceiver receiver : arrays) {
				receiver.gotLocation(location);
			}
			arrays.clear();
			queryPending = false;
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		public ArrayList<LocationResultReceiver> getArrays(){
			return this.arrays;
		}
	}

	private Scanner scanner;
	private PeriodicExecutor periodicExecutor;
	private DeviceHashTableStore deviceHashTableStore;
	private RequestManager requestManager;
	private ScanResultToUploadStore resultStore;
	private ScanResultUploadManager scanResultUploadManager;
	private LocationProxy locationProxy;

	/**
	 * Initailized the BackgroundScanManager with a Scanner to be executed in background.
	 *
	 * @param scanner
	 * 		The scanner to use for background scanning
	 */
	public BackgroundScanManager(Scanner scanner, DeviceHashTableStore deviceHashTableStore, RequestManager requestManager, ScanResultToUploadStore resultStore, LocationManager locationManager, ScanResultUploadManager scanResultUploadManager) {
		periodicExecutor = new PeriodicExecutor(150000, this);
		this.deviceHashTableStore = deviceHashTableStore;
		this.requestManager = requestManager;
		this.resultStore = resultStore;
		this.locationProxy = new LocationProxy(locationManager);
		this.scanResultUploadManager = scanResultUploadManager;
		this.scanner = scanner;
		scanner.registerScanResultEventListener(this);
	}

	/**
	 * Activates or deactivates the background scanner. This determines if the scanner is run in background or
	 * deactivated.
	 *
	 * @param state
	 * 		<code>true</code> activates the scanner, <code>false</code> deactivates it
	 */
	public void setActive(boolean state) {
		periodicExecutor.setTimerActive(state);
	}

	@Override
	public void run() {
		BloomFilterDownloadRequest bloomFilterDownloadRequest = new BloomFilterDownloadRequest();
		bloomFilterDownloadRequest.registerListener(new BloomFilterDownloadResultListener() {
			@Override
			public void onReceiveBloomFilterDownloadResult(DeviceHashTable deviceHashTable) {
				deviceHashTableStore.replace(deviceHashTable);
			}
		});
		requestManager.send(bloomFilterDownloadRequest);

		Log.i("BackgroundScanManager", "Scanning");
		scanner.startScan(10000);
	}

	@Override
	public void onDeviceFound(BluetoothDevice device, int rssi) {
		byte[] address = MacAdressConverter.stringToByteArray(device.getAddress());
		byte[] key = Encryptor.keyFromAddress(address);
		HashedMacAddress hashedMacAddress = Encryptor.hashedMacAddressFromKey(key);

		Log.i("BackgroundScanManager", "Found device: " + device.getAddress()
		                               + ((device.getName() != null) ? ", Name: " + device.getName() : "")
		                               + ", HashedMacAddress: " + hashedMacAddress.toString()
		                               + ", RSSI: " + rssi);

		if (deviceHashTableStore.get() != null && deviceHashTableStore.get().contains(hashedMacAddress)) {
			Log.i("BackgroundScanManager", "Device is in store");
			Date seenDate = new Date();
			locationProxy.query(new LocationProxy.LocationResultReceiver() {
				@Override
				public void gotLocation(Location location) {
					Log.i("BackgroundScanManager", "Got location for: " + device.getAddress());
					edu.kit.informatik.pse.bleloc.client.model.device.Location result = new edu.kit.informatik.pse.bleloc.client.model.device.Location();
					result.setLongitude(location.getLongitude());
					result.setLatitude(location.getLatitude());
					result.setDate(seenDate);
					result.setSignalStrength(rssi);
					ScanResultToUpload scanResultToUpload = new ScanResultToUpload(hashedMacAddress, Encryptor.encryptScanResult(address, result));
					resultStore.add(scanResultToUpload);
					scanResultUploadManager.triggerUpload();
				}
			});
		}
	}

	@Override
	public void onScanFinished() {
	}

	public LocationProxy getLocationProxy(){
		return  this.locationProxy;
	}
}
