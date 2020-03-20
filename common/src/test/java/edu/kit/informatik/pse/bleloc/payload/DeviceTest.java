package edu.kit.informatik.pse.bleloc.payload;

import edu.kit.informatik.pse.bleloc.model.HashedMacAddress;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceTest {
	private static final HashedMacAddress testHardwareIdentifier = HashedMacAddress.fromString("123456");

	@Test
	public void testConstructer() {
		Device payload = new Device(testHardwareIdentifier);

		Assert.assertEquals(testHardwareIdentifier.toString(), payload.getHardwareIdentifier());
	}

	@Test
	public void testGetterAndSetter() {
		Device payload = new Device();

		payload.setHardwareIdentifier(testHardwareIdentifier.toString());

		Assert.assertEquals(testHardwareIdentifier.toString(), payload.getHardwareIdentifier());
	}
}