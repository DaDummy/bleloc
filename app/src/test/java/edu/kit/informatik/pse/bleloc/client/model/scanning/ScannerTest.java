package edu.kit.informatik.pse.bleloc.client.model.scanning;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLooper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.test.InstrumentationRegistry.getContext;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ScannerTest {

	private static Scanner scanner;
	private static Set<BluetoothDevice> bluetoothDevices;
	private static byte[] array = {1, 2, 3, 4, 5, 6};


	BluetoothAdapter bluetoothAdapter;
	@Mock
	BluetoothAdapter bluetoothAdapter1;
	@Mock
	ScanResultEventListener scanResultEventListener;
	@Mock
	BluetoothDevice bluetoothDevice1;
	@Mock
	BluetoothDevice bluetoothDevice2;
	@Mock
	Intent intent;
	@Mock
	BluetoothAdapter.LeScanCallback leScanCallback;

	@Before
	public void setUp(){

		MockitoAnnotations.initMocks(this);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		scanner = new Scanner(bluetoothAdapter);
		bluetoothDevices = new HashSet<>();
		bluetoothDevices.add(bluetoothDevice1);
		bluetoothDevices.add(bluetoothDevice2);
		when(bluetoothAdapter1.getBondedDevices()).thenReturn(bluetoothDevices);
		when(bluetoothDevice1.getAddress()).thenReturn("00:00:00:00:00:00");
		when(intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")).thenReturn(bluetoothDevice1);
	}

	@Test
	public void initialTest(){
		Assert.assertNotNull(scanner);
	}

	@Test
	public void deviceFoundReceiverTest(){
	when(intent.getAction()).thenReturn("android.bluetooth.device.action.FOUND");
	scanner.registerScanResultEventListener(scanResultEventListener);
	scanner.getDeviceFoundReceiver().onReceive(getContext(),intent);
	}

	@Test
	public void scanFinishedReceiverTest(){
	when(intent.getAction()).thenReturn("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
	scanner.setLeScanDuration(100);
	scanner.registerScanResultEventListener(scanResultEventListener);
	scanner.attach(getContext());
	scanner.getScanFinishedReceiver().onReceive(getContext(), intent);
	ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
	}

	@Test
	public void scanFinishedReceiverTest_2(){
		when(intent.getAction()).thenReturn("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
		scanner.setLeScanDuration(0);
		scanner.registerScanResultEventListener(scanResultEventListener);
		scanner.attach(getContext());
		scanner.getScanFinishedReceiver().onReceive(getContext(), intent);
	}

	@Test
	public void leScanCallbackTest(){
		scanner.registerScanResultEventListener(scanResultEventListener);
		scanner.getLeScanCallback().onLeScan(bluetoothDevice1, 50, array);
		Assert.assertTrue(scanner.getDiscoveredAddresses().contains(bluetoothDevice1.getAddress()));
	}

	@Test
	public void registerScanResultEventListenerTest(){
		scanner.registerScanResultEventListener(scanResultEventListener);
		scanner.deregisterScanResultEventListener(scanResultEventListener);
	}

	@Test
	public void attach_detach_Test(){
		scanner.attach(getContext());
		scanner.detach(getContext());
	}

	@Test
	public void getPairedDevicesTest(){
		Scanner scanner1 = new Scanner(bluetoothAdapter1);
		List<BluetoothDevice> bluetoothDevicesList = scanner1.getPairedDevices();
		Assert.assertTrue(bluetoothDevicesList.containsAll(bluetoothDevices));
	}

	@Test
	public void startScan() {
		scanner.startScan(100);
	}

	@Test
	public void getLeScanCallbackTest(){
		scanner.setLeScanCallback(leScanCallback);
		Assert.assertEquals(leScanCallback.toString(), scanner.getLeScanCallback().toString());
	}

	@Test
	public void getDiscoveredAddressesTest() {
		scanner.registerScanResultEventListener(scanResultEventListener);
		scanner.getLeScanCallback().onLeScan(bluetoothDevice1, 50, array);
		Set<String> discoveredAddresses = new HashSet<>();
		discoveredAddresses.add(bluetoothDevice1.getAddress());
		Assert.assertEquals(discoveredAddresses, scanner.getDiscoveredAddresses());
	}

	@Test
	public void setLeScanDurationTest(){
		scanner.setLeScanDuration(1000);
		Assert.assertEquals(scanner.getLeScanDuration(),1000);
	}
}
