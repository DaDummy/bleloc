package edu.kit.informatik.pse.bleloc.client.model.connectivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;
import edu.kit.informatik.pse.bleloc.client.controller.AppDatabase;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.RequestManager;
import edu.kit.informatik.pse.bleloc.client.model.device.Device;
import edu.kit.informatik.pse.bleloc.client.model.device.DeviceStore;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;

public class ScanResultDownloadManagerTest {

	private static ScanResultDownloadManager manager;
	private static DeviceStore deviceStore;
	private static Device device;
	private static HashedMacAddress hashedMacAddress;
	private static byte[] hashedMacByteArray;

	@BeforeClass
	public static void setUpBeforeClass() {
		hashedMacByteArray = new byte[12];

		hashedMacAddress = HashedMacAddress.fromByteArray(hashedMacByteArray);

		device = Mockito.mock(Device.class);
		Mockito.when(device.getAlias()).thenReturn("alias");
		Mockito.when(device.getHardwareIdentifier()).thenReturn(hashedMacByteArray);
		Mockito.when(device.getName()).thenReturn("name");
		Mockito.when(device.getHashedHardwareIdentifier()).thenReturn(hashedMacAddress);

		deviceStore = Mockito.mock(DeviceStore.class);
		Mockito.when(deviceStore.getDevice(ArgumentMatchers.anyInt())).thenReturn(device);
		List<Device> list = Arrays.asList(new Object[]{device});
		Mockito.when(deviceStore.getDevices()).thenReturn(list);
	}

	@Before
	public void setUp() {
		AppDatabase database = Mockito.mock(AppDatabase.class);
		Mockito.when(database.getDeviceStore()).thenReturn(deviceStore);
		RequestManager requestManager = Mockito.mock(RequestManager.class);
		UserDataSyncManager userDataSyncManager = Mockito.mock(UserDataSyncManager.class);
		manager = new ScanResultDownloadManager(database, requestManager, userDataSyncManager);
	}

	@After
	public void tearDown() {
		manager = null;
	}

	@Test
	public void ScanResultDownloadManager_nullInput() {
		new ScanResultDownloadManager(null, null, null);
	}

	@Test
	public void triggerDownload() {
		manager.triggerDownload();
		manager.triggerDownload();
	}

	@Test
	public void cancel_inactive() {
		manager.cancel();
	}

	@Test
	public void cancel_active() {
		manager.triggerDownload();
		manager.cancel();
	}

	@Test
	public void onPreSync() {
		manager.onPreSync();
	}

	@Test
	public void onPostSync() {
		manager.onPostSync();
	}

	@Test
	public void onReceiveScanResultDownloadResult() {
		DeviceTrackingResultPayload result = Mockito.mock(DeviceTrackingResultPayload.class);
		String hash = hashedMacAddress.toString();
		Mockito.when(result.getHashedHardwareIdentifier()).thenReturn(hash);
		Mockito.when(result.getEncryptedData()).thenReturn(new byte[1000000]);
		manager.onReceiveScanResultDownloadResult(result);
	}

	@Test
	public void onReceiveScanResultDownloadResult_nullInput() {
		manager.onReceiveScanResultDownloadResult(null);
	}

	@Test
	public void onReceiveScanResultDownloadResult_correctCipher() {
		DeviceTrackingResultPayload result = Mockito.mock(DeviceTrackingResultPayload.class);
		String hash = hashedMacAddress.toString();
		Mockito.when(result.getHashedHardwareIdentifier()).thenReturn(hashedMacAddress.toString());
		Mockito.when(result.getEncryptedData()).thenReturn(new byte[1000000]);
		manager.onReceiveScanResultDownloadResult(result);
	}

	@Test
	public void onReceiveScanResultConfirmDownloadResult() {
		manager.onReceiveScanResultConfirmDownloadResult();
	}
}
