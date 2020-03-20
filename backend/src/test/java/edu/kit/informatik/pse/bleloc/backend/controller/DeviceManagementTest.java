package edu.kit.informatik.pse.bleloc.backend.controller;

import edu.kit.informatik.pse.bleloc.model.Device;
import edu.kit.informatik.pse.bleloc.model.DeviceHashTable;
import edu.kit.informatik.pse.bleloc.model.DeviceStore;
import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import edu.kit.informatik.pse.bleloc.payload.DeviceIndexPayload;
import edu.kit.informatik.pse.bleloc.payload.DevicePayload;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

public class DeviceManagementTest extends ControllerTest {
	private static final String NewDeviceAddressString = "bbbbbbbbbb";
	private static final HashedMacAddress NewDeviceAddress = HashedMacAddress.fromString(NewDeviceAddressString);

	DeviceManagement resource;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		resource = new DeviceManagement();
		resource.em = em;
		resource.accountProxy = generateAccountProxy();
		resource.deviceHashTableStore = deviceHashTableStore;
	}


	@Test
	public void postRegister() {
		edu.kit.informatik.pse.bleloc.payload.Device payload =
			new edu.kit.informatik.pse.bleloc.payload.Device(NewDeviceAddress);

		Response response = resource.postRegister(payload);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
		Assert.assertTrue(response.getEntity() instanceof DevicePayload);
		DevicePayload responsePayload = (DevicePayload)response.getEntity();
		Assert.assertEquals(NewDeviceAddressString, responsePayload.getHashedHardwareIdentifier());

		Assert.assertEquals(NumberOfDevices + 1, deviceStore.getCount());

		Device newDevice = deviceStore.get(userAccount, NewDeviceAddress);
		Assert.assertNotNull(newDevice);
		Assert.assertEquals(NewDeviceAddress, newDevice.getHardwareIdentifier());
		Assert.assertEquals(responsePayload.getTrackUntil(), newDevice.getTrackUntil());

		DeviceHashTable dht = deviceHashTableStore.get();
		Assert.assertTrue("The DeviceHashTable should contain the device", dht.contains(NewDeviceAddress));
	}

	@Test
	public void getList() {
		Response response = resource.getList();

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
		Assert.assertTrue(response.getEntity() instanceof DeviceIndexPayload);
		DeviceIndexPayload responsePayload = (DeviceIndexPayload)response.getEntity();

		Assert.assertEquals(NumberOfDevices, responsePayload.getIndex().size());
		String entry = responsePayload.getIndex().iterator().next();
		Assert.assertEquals(DeviceAddressString, entry);

		DeviceHashTable dht = deviceHashTableStore.get();
		Assert.assertTrue("The DeviceHashTable should contain the device", dht.contains(HashedMacAddress.fromString(entry)));
	}

	@Test
	public void postUnregister() {
		Response response = resource.postUnregister(DeviceAddressString);

		Assert.assertEquals(Response.Status.OK, response.getStatusInfo());

		Assert.assertEquals(NumberOfDevices - 1, deviceStore.getCount());

		Device deletedDevice = deviceStore.get(userAccount, DeviceAddress);
		Assert.assertNull(deletedDevice);

		DeviceHashTable dht = deviceHashTableStore.get();
		Assert.assertFalse("The DeviceHashTable should no longer contain the device", dht.contains(DeviceAddress));
	}
}