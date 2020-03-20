package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DeviceTrackingResultPayloadTest {
	private long testTrackingResultId = 1L;
	private Date testDate = new GregorianCalendar(2019, 0, 5).getTime();
	private String testHashedMacAddress = "123456";
	private byte[] testEncryptedData = "testData".getBytes();

	@Test
	public void testGettersAndSetters() {
		DeviceTrackingResultPayload payload = new DeviceTrackingResultPayload();

		payload.setTrackingResultId(testTrackingResultId);
		payload.setEncounteredAt(testDate);
		payload.setHashedHardwareIdentifier(testHashedMacAddress);
		payload.setEncryptedData(testEncryptedData);

		Assert.assertEquals(testTrackingResultId, payload.getTrackingResultId());
		Assert.assertEquals(testDate.getTime(), payload.getEncounteredAt().getTime());
		Assert.assertEquals(testHashedMacAddress, payload.getHashedHardwareIdentifier());
		Assert.assertArrayEquals(testEncryptedData, payload.getEncryptedData());
	}
}