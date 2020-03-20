package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DeviceHashTablePayloadTest {
	private static final Date testDate = new GregorianCalendar(2019, 0, 5).getTime();
	private static final byte[] testData = "testData".getBytes();

	@Test
	public void testGetterAndSetter() {
		DeviceHashTablePayload payload = new DeviceHashTablePayload();

		payload.setModifiedAt(testDate);
		payload.setData(testData);

		Assert.assertEquals(testDate, payload.getModifiedAt());
		Assert.assertEquals(testData, payload.getData());
	}
}