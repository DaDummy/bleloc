package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTableManager;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTableStore;
import edu.kit.informatik.pse.bleloc.model.TrackingResult;
import edu.kit.informatik.pse.bleloc.payload.DeviceHashTablePayload;
import edu.kit.informatik.pse.bleloc.payload.DeviceTrackingResultPayload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Date;

public class DeviceTrackingTest extends ControllerTest {
	private static final Date NewTrackingResultEncounteredAt =
		Date.from(TrackingResultEncounteredAt.toInstant().minusSeconds(5));
	private static final byte[] NewTrackingResultEncryptedData =
		new byte[]{(byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef, 0x01};

	private DeviceTracking resource;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		resource = new DeviceTracking();
		resource.em = em;
		resource.accountProxy = generateAccountProxy();
		resource.deviceHashTableStore = deviceHashTableStore;
	}

	@Test
	public void getFilter() {
		Response response = resource.getFilter();

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		DeviceHashTablePayload responsePayload = (DeviceHashTablePayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		DeviceHashTable dht = deviceHashTableStore.get();
		Assert.assertEquals(dht.getLastUpdateTime(), responsePayload.getModifiedAt());
		Assert.assertArrayEquals(dht.getSerialized(), responsePayload.getData());
	}

	@Test
	public void getResultOk() {
		final String hardwareIdentifier = device.getHardwareIdentifier().toString();
		final int n = 0;

		Response response = resource.getResult(n);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		DeviceTrackingResultPayload responsePayload = (DeviceTrackingResultPayload) response.getEntity();
		Assert.assertNotNull(responsePayload);

		Assert.assertEquals((long) trackingResult.getId(), responsePayload.getTrackingResultId());
		Assert.assertEquals(hardwareIdentifier, responsePayload.getHashedHardwareIdentifier());
		Assert.assertEquals(TrackingResultEncounteredAt, responsePayload.getEncounteredAt());
		Assert.assertArrayEquals(TrackingResultEncryptedData, responsePayload.getEncryptedData());
	}

	@Test
	public void getResultNotFound() {
		final int n = 100;

		Response response = resource.getResult(n);

		Assert.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void postResultReceivedOk() {
		final long scanResultId = trackingResult.getId();

		Response response = resource.postResultReceived(scanResultId);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		Assert.assertNull(trackingResultStore.get(scanResultId));
	}

	@Test
	public void postResultReceivedNotFound() {
		final long scanResultId = trackingResult.getId() + 100;

		Response response = resource.postResultReceived(scanResultId);

		Assert.assertEquals(Response.Status.NOT_FOUND, response.getStatusInfo());
	}

	@Test
	public void postResult() {
		final String hardwareIdentifier = device.getHardwareIdentifier().toString();

		DeviceTrackingResultPayload payload = new DeviceTrackingResultPayload();
		payload.setHashedHardwareIdentifier(hardwareIdentifier);
		payload.setEncounteredAt(NewTrackingResultEncounteredAt);
		payload.setEncryptedData(NewTrackingResultEncryptedData);

		Response response = resource.postResult(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		TrackingResult newTrackingResult = trackingResultStore.getNthOldestByUserAccount(userAccount, 0);
		Assert.assertNotEquals(trackingResult, newTrackingResult);
		Assert.assertEquals(device, trackingResult.getDevice());
		Assert.assertEquals(NewTrackingResultEncounteredAt, newTrackingResult.getEncounteredAt());
		Assert.assertArrayEquals(NewTrackingResultEncryptedData, newTrackingResult.getEncryptedData());
	}
}