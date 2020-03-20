package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class UserDataIndexEntryTest {
	private static final long testId = 1L;
	private static final Date testDate = new GregorianCalendar(2019, 0, 5).getTime();

	@Test
	public void testConstructor() {
		UserDataIndexEntry payload = new UserDataIndexEntry(testId, testDate);

		Assert.assertEquals(testId, payload.getId());
		Assert.assertEquals(testDate.getTime(), payload.getModifiedAt().getTime());
	}

	@Test
	public void testGettersAndSetters() {
		UserDataIndexEntry payload = new UserDataIndexEntry(2L, new Date());

		payload.setId(testId);
		payload.setModifiedAt(testDate);

		Assert.assertEquals(testId, payload.getId());
		Assert.assertEquals(testDate.getTime(), payload.getModifiedAt().getTime());
		Assert.assertNotNull(payload.toString());
	}
}