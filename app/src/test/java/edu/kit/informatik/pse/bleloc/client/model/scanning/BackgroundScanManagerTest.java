package edu.kit.informatik.pse.bleloc.client.model.scanning;

import android.bluetooth.BluetoothDevice;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.device.DeviceStore;
import edu.kit.informatik.pse.bleloc.client.model.devicehashtable.DeviceHashTableStore;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class BackgroundScanManagerTest {

	private static BackgroundScanManager backgroundScanManager;
	private static BackgroundScanManager.LocationProxy locationProxy;

	@Mock
	Scanner scanner;
	@Mock
	DeviceHashTableStore deviceHashTableStore;
	@Mock
	ScanResultToUploadStore resultStore;
	@Mock
	LocationManager locationManager;
	@Mock
	ScanResultUploadManager scanResultUploadManager;
	@Mock
	BluetoothDevice bluetoothDevice;
	@Mock
	Location location;
	@Mock
	BackgroundScanManager.LocationProxy.LocationResultReceiver locationResultReceiver;
	@Mock
	Bundle extras;
	@Mock
	RequestManager requestManager;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		backgroundScanManager = new BackgroundScanManager(scanner, deviceHashTableStore, requestManager,resultStore, locationManager,
		                                                  scanResultUploadManager);
		when(bluetoothDevice.getAddress()).thenReturn("AA:BB:CC:DD:EE:FF");
		when(bluetoothDevice.getName()).thenReturn("DeviceName");
		when(deviceHashTableStore.get()).thenReturn(null);
		locationProxy = backgroundScanManager.getLocationProxy();
	}

	@Test
	public void initialTest(){
		Assert.assertNotNull(backgroundScanManager);
	}

	@Test
	public void setActiveTest(){
		backgroundScanManager.setActive(true);
	}

	@Test
	public void onScanFinishedTest() {
		backgroundScanManager.onScanFinished();
	}

	@Test
	public void runTest(){
		backgroundScanManager.run();
	}

	@Test
	public void onDeviceFoundTest(){
		backgroundScanManager.onDeviceFound(bluetoothDevice);
	}

	@Test
	public void LocationProxyTest(){
		locationProxy.onProviderDisabled("provider");
		locationProxy.query(locationResultReceiver);
		Assert.assertTrue(locationProxy.getArrays().contains(locationResultReceiver));
		locationProxy.onLocationChanged(location);
		locationProxy.onProviderEnabled("privider");
		locationProxy.onStatusChanged("provider", 0, extras);
	}

	@Mock
	DeviceHashTable deviceHashTable;

	@Test
	public void gotLocationProxyTest() {
		when(deviceHashTableStore.get()).thenReturn(deviceHashTable);
		when(deviceHashTableStore.get().contains(isA(HashedMacAddress.class))).thenReturn(true);
		backgroundScanManager.onDeviceFound(bluetoothDevice);
		backgroundScanManager.getLocationProxy().getArrays().get(0).gotLocation(location);
	}

}
