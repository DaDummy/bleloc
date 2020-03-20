package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class UserDataPayloadTest {
	private static final long testId = 1L;
	private static final Date testDate = new GregorianCalendar(2019, 0, 5).getTime(); ;
	private static final byte[] testEncryptedData = "testData".getBytes();

	@Test
	public void testGettersAndSetters() {
		UserDataPayload payload = new UserDataPayload();

		payload.setSyncId(testId);
		payload.setModifiedAt(testDate);
		payload.setEncryptedData(testEncryptedData);

		Assert.assertEquals(testId, payload.getSyncId());
		Assert.assertEquals(testDate.getTime(), payload.getModifiedAt().getTime());
		Assert.assertArrayEquals(testEncryptedData, payload.getEncryptedData());
		Assert.assertNotNull(payload.toString());
	}
}