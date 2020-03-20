package edu.kit.informatik.pse.bleloc.client.model.connectivity;

import android.util.Log;
import edu.emory.mathcs.backport.java.util.Arrays;
import edu.kit.informatik.pse.bleloc.client.model.connectivity.requests.*;
import edu.kit.informatik.pse.bleloc.client.model.device.*;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import edu.kit.informatik.pse.bleloc.payload.DeviceIndexPayload;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class TrackingStateManagerTest {

	private TrackingStateManager trackingStateManager;
	private DeviceTrackingStateStore store;
	private DeviceStore deviceStore;
	private RequestManager requestManager;
	private Device device;
	private Long deviceId = 0L;
	private HashedMacAddress hashedMacAddress;

	@Before
	public void setUp() {
		this.store = Mockito.mock(DeviceTrackingStateStore.class);

		this.requestManager = Mockito.mock(RequestManager.class);
		Mockito.doNothing().when(requestManager).send(Mockito.any(BackendRequest.class));

		this.hashedMacAddress = Mockito.mock(HashedMacAddress.class);
		Mockito.when(this.hashedMacAddress.toString()).thenReturn("AA:BB:CC:DD:EE:FF");

		this.device = Mockito.mock(Device.class);
		Mockito.when(this.device.getHashedHardwareIdentifier()).thenReturn(this.hashedMacAddress);
		Mockito.when(this.device.getId()).thenReturn(0L);

		this.deviceStore = Mockito.mock(DeviceStore.class);
		Mockito.when(this.deviceStore.getDevices()).thenReturn(Arrays.asList(new Device[]{this.device}));

		this.trackingStateManager = new TrackingStateManager(store, deviceStore, requestManager);
	}

	@After
	public void tearDown() {
		this.trackingStateManager = null;
	}

	@Test
	public void isActive_inactive() {
		Assert.assertFalse(this.trackingStateManager.isActive());
	}

	@Test
	public void isActive_active() {
		this.trackingStateManager.onPreSync();
		Assert.assertFalse(this.trackingStateManager.isActive());
	}

	@Test
	public void cancel() {
		this.trackingStateManager.cancel();
	}

	@Test
	public void isPendingCancel() {
		Assert.assertFalse(this.trackingStateManager.isPendingCancel());
	}

	@Test
	public void isPendingCancel_isPending() {
		this.trackingStateManager.onPostSync();
		this.trackingStateManager.cancel();
		Assert.assertTrue(this.trackingStateManager.isPendingCancel());
	}

	@Test
	public void refreshTrackingState_inactive() {
		this.trackingStateManager.refreshTrackingState();
	}

	@Test
	public void refreshTrackingState_active() {
		this.trackingStateManager.refreshTrackingState();
		this.trackingStateManager.refreshTrackingState();
	}

	@Test (expected = NullPointerException.class)
	public void toggleDeviceTrackingStatus_nullInput() {
		this.trackingStateManager.toggleDeviceTrackingStatus(null, false);
	}

	@Test
	public void toggleDeviceTrackingStatus_setTracking_notTracked() {
		Mockito.when(this.store.getByDevice(this.deviceId)).thenReturn(null);
		this.trackingStateManager.toggleDeviceTrackingStatus(this.device, true);
	}

	@Test
	public void toggleDeviceTrackingStatus_setNonTracking_isTracked() {
		Mockito.when(this.store.getByDevice(this.deviceId)).thenReturn(new DeviceTrackingState(this.deviceId, TrackingState.TRACKED));
		this.trackingStateManager.toggleDeviceTrackingStatus(this.device, false);
	}

	@Test (expected = NullPointerException.class)
	public void isTracked() {
		Assert.assertFalse(this.trackingStateManager.isTracked(null));
	}

	@Test
	public void onPreSync() {
		this.trackingStateManager.onPreSync();
	}

	@Test
	public void onPostSync() {
		this.trackingStateManager.onPostSync();
	}

	@Test
	public void onReceiveRegisterTrackedDeviceResult() {
		this.trackingStateManager.onReceiveRegisterTrackedDeviceResult();
	}

	@Test
	public void onReceiveDeregisterTrackedDeviceResult() {
		this.trackingStateManager.onReceiveDeregisterTrackedDeviceResult();
	}

	@Test
	public void onReceiveListTrackedDevicesResult_nullInput() {
		this.trackingStateManager.onReceiveListTrackedDevicesResult(null);
	}

	@Test
	public void onReceiveListTrackedDevicesResult_normal() {
		DeviceIndexPayload payload = new DeviceIndexPayload();
		payload.setIndex(Arrays.asList(new String[]{this.device.getHashedHardwareIdentifier().toString()}));
		this.trackingStateManager.onReceiveListTrackedDevicesResult(payload);
	}

}
