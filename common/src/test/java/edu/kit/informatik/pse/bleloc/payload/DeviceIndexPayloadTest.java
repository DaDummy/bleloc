package edu.kit.informatik.pse.bleloc.payload;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class DeviceIndexPayloadTest {
	private static final String testItem = "test1";
	private static final Collection<String> index = new ArrayList<>(Arrays.asList(testItem));

	@Test
	public void testGettersAndSetters() {
		DeviceIndexPayload payload = new DeviceIndexPayload();

		payload.setIndex(index);

		Assert.assertEquals(1, payload.getIndex().size());
		Assert.assertEquals(testItem, payload.getIndex().iterator().next());
	}
}