package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DevicePayloadTest {
	private static final Date testDate = new GregorianCalendar(2019, 0, 5).getTime();
	private static final String testHardwareIdentifier = "123456";

	@Test
	public void testGettersAndSetters() {
		DevicePayload payload = new DevicePayload();

		payload.setTrackUntil(testDate);
		payload.setHashedHardwareIdentifier(testHardwareIdentifier);

		Assert.assertEquals(testDate.getTime(), payload.getTrackUntil().getTime());
		Assert.assertEquals(testHardwareIdentifier, payload.getHashedHardwareIdentifier());
	}
}