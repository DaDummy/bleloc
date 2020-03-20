package edu.kit.informatik.pse.bleloc.client.model.device;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.GregorianCalendar;

public class LocationTest {

	private static final long testDeviceId = 1L;
	private static final Date testDate = new GregorianCalendar(2010, 1, 1).getTime();
	private static final int testSignalStrength = 1;
	private static final int testIntLongitude = 1;
	private static final int testIntLatitude = 1;
	private static final boolean testSeen = true;

	private static final double testLongitude = testIntLongitude * (180. / 0x7fffffff);
	private static final double testLatitude = testIntLatitude * (90. / 0x7fffffff);

	private static final double DELTA = 0.0000001;

	@Test
	public void testGettersAndSetters(){
		Location location = new Location();

		location.setDeviceId(testDeviceId);
		location.setDate(testDate);
		location.setSignalStrength(testSignalStrength);
		location.setIntLongitude(testIntLongitude);
		location.setIntLatitude(testIntLatitude);
		location.setSeen(testSeen);

		Assert.assertEquals(testDeviceId, location.getDeviceId());
		Assert.assertEquals(testDate.getTime(), location.getDate().getTime());
		Assert.assertEquals(testSignalStrength, location.getSignalStrength());
		Assert.assertEquals(testIntLongitude, location.getIntLongitude());
		Assert.assertEquals(testIntLatitude, location.getIntLatitude());
		Assert.assertEquals(testSeen, location.isSeen());

	}

	@Test
	public void setTestLongitudeLatitude() {
		Location location = new Location();

		location.setLongitude(testLongitude);
		location.setLatitude(testLatitude);

		Assert.assertEquals(testLongitude, location.getLongitude(), DELTA);
		Assert.assertEquals(testLatitude, location.getLatitude(), DELTA);
	}
}