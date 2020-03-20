package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class UserDataIndexPayloadTest {
	private static final long testId = 1L;
	private static final Date testDate = new GregorianCalendar(2019, 0, 5).getTime();
	private static final UserDataIndexEntry testEntry = new UserDataIndexEntry(testId, testDate);
	private static final Collection<UserDataIndexEntry> index = new ArrayList<>(Arrays.asList(testEntry));

	@Test
	public void testGettersAndSetters() {
		UserDataIndexPayload payload = new UserDataIndexPayload();

		payload.setIndex(index);

		Assert.assertEquals(1, payload.getIndex().size());
		UserDataIndexEntry retrievedEntry = payload.getIndex().iterator().next();
		Assert.assertEquals(testEntry.getId(), retrievedEntry.getId());
		Assert.assertEquals(testEntry.getModifiedAt().getTime(), retrievedEntry.getModifiedAt().getTime());
		Assert.assertNotNull(payload.toString());
	}
}