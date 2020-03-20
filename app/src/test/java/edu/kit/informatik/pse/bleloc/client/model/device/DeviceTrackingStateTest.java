package edu.kit.informatik.pse.bleloc.client.model.device;

import edu.kit.informatik.pse.bleloc.client.model.typeconverters.MacAdressConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceTrackingStateTest {
	private Device device;
	private DeviceTrackingState trackingState;
	private long deviceId = 1;

	@Before
	public void setupTestObjects() {
		device = new Device(MacAdressConverter.stringToByteArray("00:00:00:00:00:00"));
		device.setId(deviceId);
		trackingState = new DeviceTrackingState(device.getId(), TrackingState.INACTIVE);
	}

	@Test
	public void getDeviceId() {
		Assert.assertEquals(deviceId, trackingState.getDeviceId());
	}

	@Test
	public void testState() {
		trackingState.setTrackingState(TrackingState.TRACKED);
		Assert.assertEquals(TrackingState.TRACKED, trackingState.getTrackingState());
	}
}